pipeline {
	agent {
		label 'maven'
	}

	stages {
		stage('Build App') {
			steps {
				sh "mvn clean package"
			}
		}

		stage('Unit Test') {
		  steps {
		  	sh "mvn verify"
		  }
		}

		stage('Build Image') {
			steps {
				script {
					openshift.withCluster() {
						openshift.withProject() {
							openshift.startBuild("spring-music", "--from-file=target/summit-lab-spring-music-1.0.jar").logs("-f")
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
	}
}
