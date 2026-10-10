pipeline {
    agent any

    // Environment settings that can be changed per run (Build with Parameters)
    parameters {
        choice(name: 'DEPLOY_ENV', choices: ['dev', 'test'], description: 'Target environment label')
        string(name: 'TOMCAT_HOME', defaultValue: 'C:\\tomcat', description: 'Tomcat installation folder')
        string(name: 'TOMCAT_PORT', defaultValue: '8081', description: 'Tomcat HTTP port used for the health check')
    }

    // Check GitHub about every 2 minutes and build when there is a new commit
    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        JAVA_HOME = 'C:\\Program Files\\Microsoft\\jdk-21.0.12.101-hotspot'
        MVN       = 'C:\\maven\\bin\\mvn.cmd'
        APP_NAME  = 'faculty-notes'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('Build') {
            steps {
                bat 'call "%MVN%" -B clean compile'
            }
        }

        stage('Package') {
            steps {
                bat 'call "%MVN%" -B package'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
                success {
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def tomcatHome = params.TOMCAT_HOME ?: 'C:\\tomcat'
                    def tomcatPort = params.TOMCAT_PORT ?: '8081'
                    def deployEnv  = params.DEPLOY_ENV ?: 'dev'

                    echo "Deploying ${env.APP_NAME} to environment '${deployEnv}' (Tomcat: ${tomcatHome}, port ${tomcatPort})"

                    bat "copy /Y target\\${env.APP_NAME}.war \"${tomcatHome}\\webapps\\${env.APP_NAME}.war\""

                    bat "powershell -NoProfile -ExecutionPolicy Bypass -File scripts\\healthcheck.ps1 -Url http://localhost:${tomcatPort}/${env.APP_NAME}/actuator/health"

                    echo "Application URL: http://localhost:${tomcatPort}/${env.APP_NAME}/"
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline finished: build, package and deploy succeeded.'
        }
        failure {
            echo 'Pipeline failed. Check the stage that turned red.'
        }
    }
}
