pipeline {
    agent any

    environment {
        IMAGE_NAME = "aigc-platform-server"
        IMAGE_TAG = "1.0.0"
        CONTAINER_NAME = "aigc-platform-server"
    }

    tools {
        jdk 'jdk-21'
        maven 'maven-3.9.8'
    }

    stages {
        stage('Set Environment Variable from Host') {
            steps {
                script {
                    env.JASYPT_ENCRYPTOR_PASSWORD = sh(script: 'echo $JASYPT_ENCRYPTOR_PASSWORD', returnStdout: true).trim()
                }
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build . -t ${IMAGE_NAME}:${IMAGE_TAG}"
                }
            }
        }

        stage('Stop and Remove Previous Container') {
            steps {
                script {
                    sh """
                    if [ \$(docker ps -q -f name=${CONTAINER_NAME}) ]; then
                        docker stop ${CONTAINER_NAME}
                    fi
                    if [ \$(docker ps -aq -f status=exited -f name=${CONTAINER_NAME}) ]; then
                        docker rm ${CONTAINER_NAME}
                    fi
                    """
                }
            }
        }

        stage('Run New Container') {
            steps {
                script {
                    sh """
                    docker run -d --name ${CONTAINER_NAME} --network app -p 39291:8080 -e JASYPT_ENCRYPTOR_PASSWORD=${JASYPT_ENCRYPTOR_PASSWORD} ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Deployment finished'
        }
    }
}
