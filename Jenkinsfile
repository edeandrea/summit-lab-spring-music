pipeline {
	agent {
		label 'maven'
	}

	environment {
		BUILD_VERSION = "1.0.0.${currentBuild.number}"
	}

	stages {
		stage('Build App') {
			steps {
				sh "mvn versions:set clean package -DnewVersion=${env.BUILD_VERSION} -DskipTests"
			}
		}

		stage('Unit Test') {
		  steps {
		  	sh "mvn versions:set verify -DnewVersion=${env.BUILD_VERSION}"
		  }
		}

		stage('Publish Artifact') {
			steps {
				sh "mvn versions:set deploy -DskipTests -Dmaven.install.skip=true -DnewVersion=${env.BUILD_VERSION} -DaltDeploymentRepository=libs-snapshot::default::http://nexus.labs-infra.svc:8081/nexus/content/repositories/libs-snapshot -s misc/config/settings.xml"
			}
		}

		stage('Build Image') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							openshift.startBuild("spring-music", "--from-file=target/summit-lab-spring-music-${env.BUILD_VERSION}.jar").logs("-f")
						}
					}
				}
			}
		}

		stage('Deploy') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							dc = openshift.selector("dc", "spring-music")
							dc.rollout().latest()
							timeout(10) {
								dc.rollout().status()
							}
						}
					}
				}
			}
		}

		stage('Integration Test') {
			steps {
				script {
					sh "curl -s http://spring-music:8080/actuator/health | grep 'UP'"
				}
			}
		}

		stage('Promote to Prod') {
			steps {
				timeout(time:15, unit:'MINUTES') {
					input message: "Approve Promotion to Prod?", ok: "Promote"
				}

				script {
					openshift.withCluster() {
						openshift.tag("dev/spring-music:latest", "prod/spring-music:prod")
					}
				}
			}
		}

		stage('Deploy to Prod') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject('prod') {
							if (!openshift.selector('dc', 'spring-music').exists()) {
								def app = openshift.newApp("spring-music:prod")
								def dcpatch = [
									"apiVersion": "v1",
									"kind": "DeploymentConfig",
									"spec": [
										"template": [
											"spec": [
												"containers": [
													"env": [
														[
															"name": "DB_NAME",
															"valueFrom": [
																"secretKeyRef": [
																	"name": "summit-lab-spring-music-db",
																	"key": "database-name"
																]
															]
														],
														[
															"name": "SPRING_DATASOURCE_USERNAME",
															"valueFrom": [
																"secretKeyRef": [
																	"name": "summit-lab-spring-music-db",
																	"key": "database-user"
																]
															]
														],
														[
															"name": "SPRING_DATASOURCE_PASSWORD",
															"valueFrom": [
																"secretKeyRef": [
																	"name": "summit-lab-spring-music-db",
																	"key": "database-password"
																]
															]
														],
														[
															"name": "SPRING_DATASOURCE_URL",
															"value": "jdbc:postgresql://summit-lab-spring-music-db/$(DB_NAME)"
														]
													]
												]
											]
										]
									]
								]

								openshift.apply(dcpatch)

								app.narrow("svc").expose()
							}
						}
					}
				}
			}
		}
	}
}
