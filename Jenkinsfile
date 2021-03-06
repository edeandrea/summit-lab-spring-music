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
				sh "./mvnw -version"
				sh "./mvnw versions:set clean package -DnewVersion=${env.BUILD_VERSION} -DskipTests"
			}
		}

		stage('Unit Test') {
		  steps {
		  	sh "./mvnw versions:set verify -DnewVersion=${env.BUILD_VERSION}"
		  }
		  post {
		  	always {
		  		junit 'target/surefire-reports/TEST-*.xml'
		  		jacoco execPattern: 'target/jacoco.exec'
		  	}
		  }
		}

		stage('Sonar Scan') {
			steps {
				sh "./mvnw versions:set sonar:sonar -Dsonar.host.url=http://sonarqube.labs-infra.svc:9000 -DskipTests -DnewVersion=${env.BUILD_VERSION} -P sonar -s misc/config/settings.xml"
			}
		}

		stage('Publish Artifact') {
			steps {
				sh "./mvnw versions:set deploy -DskipTests -Dmaven.install.skip=true -DnewVersion=${env.BUILD_VERSION} -DaltDeploymentRepository=libs-snapshot::default::${params.NEXUS_URL}/repository/libs-snapshot/ -s misc/config/settings.xml"
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
							// dc.rollout().latest()
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

		stage('Quay - Container Image Scan') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							openshift.startBuild("image-quay-image-mover").logs("-f")
						}
					}
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
							def dc = openshift.selector("dc", "spring-music")

							// If this is the first time this app is being deployed into the prod namespace
							if (!dc.exists()) {
								def app = openshift.newApp("spring-music:prod", "--as-deployment-config=true")
								app.describe()

								dc = app.narrow("dc")
								def dcmap = dc.object()
								
								dcmap.remove('status')
								//dcmap.metadata.remove('annotations')
								//dcmap.metadata.remove('labels')
								dcmap.metadata.remove('creationTimestamp')
								dcmap.metadata.remove('generation')
								dcmap.metadata.remove('resourceVersion')
								dcmap.metadata.remove('selfLink')
								dcmap.metadata.remove('uid')
								dcmap.spec.remove('replicas')
								dcmap.spec.remove('revisionHistoryLimit')
								dcmap.spec.remove('selector')
								dcmap.spec.remove('strategy')
								dcmap.spec.remove('test')
								dcmap.spec.remove('triggers')

								dcmap.metadata.labels['app.kubernetes.io/part-of'] = 'spring-music'
								dcmap.metadata.labels['app.openshift.io/runtime'] = 'spring'
								dcmap.metadata.labels['app.openshift.io/runtime-version'] = '11'
								dcmap.metadata.annotations['app.openshift.io/vcs-ref'] = 'pipeline'
								dcmap.metadata.annotations['app.openshift.io/vcs-uri'] = 'https://github.com/edeandrea/summit-lab-spring-music'
								dcmap.metadata.annotations['app.openshift.io/connects-to'] = 'summit-lab-spring-music-db'

								def envList = []
								envList << [
									"name": "DB_NAME",
									"valueFrom": [
										"secretKeyRef": [
											"name": "summit-lab-spring-music-db",
											"key": "database-name"
										]
									]
								]

								envList << [
									"name": "SPRING_DATASOURCE_USERNAME",
									"valueFrom": [
										"secretKeyRef": [
											"name": "summit-lab-spring-music-db",
											"key": "database-user"
										]
									]
								]

								envList << [
									"name": "SPRING_DATASOURCE_PASSWORD",
									"valueFrom": [
										"secretKeyRef": [
											"name": "summit-lab-spring-music-db",
											"key": "database-password"
										]
									]
								]

								envList << [
									"name": "SPRING_DATASOURCE_URL",
									"value": "jdbc:mysql://summit-lab-spring-music-db/\$(DB_NAME)"
								]

								def container = dcmap.spec.template.spec.containers[0]
								container['env'] = envList

								container.livenessProbe = [
									"failureThreshold": 3,
									"httpGet": [
										"path": "/actuator/health/liveness",
										"port": 8080,
										"scheme": "HTTP"
									],
									"initialDelaySeconds": 30,
									"periodSeconds": 10,
									"successThreshold": 1,
									"timeoutSeconds": 10
								]

								container.readinessProbe = [
									"failureThreshold": 3,
									"httpGet": [
										"path": "/actuator/health/readiness",
										"port": 8080,
										"scheme": "HTTP"
									],
									"initialDelaySeconds": 30,
									"periodSeconds": 10,
									"successThreshold": 1,
									"timeoutSeconds": 10
								]

								container.resources = [
									"limits": [
										"cpu": "2",
										"memory": "1Gi"
									],
									"requests": [
										"cpu": "500m",
										"memory": "512Mi"
									]
								]

								dcmap.spec.replicas = 2

								openshift.apply(dcmap)
								app.narrow("svc").expose()
							}

							timeout(10) {
								dc.rollout().status()
							}
						}
					}
				}
			}
		}
	}
}
