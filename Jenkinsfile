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

        stage('Load Environment Variables') {
            steps {
                script {
                    // Read env.config file content
                    def envConfigContent = readFile('/env.config') // 请确保路径正确

                    // Split content by line and parse key-value pairs
                    def envVars = envConfigContent.split("\n").collect { line ->
                        line.trim()
                    }.findAll { line ->
                        line && !line.startsWith('#')
                    }.collect { line ->
                        def pair = line.split('=')
                        return "${pair[0]}=${pair[1]}"
                    }

                    // Use withEnv to set environment variables
                    withEnv(envVars) {
                        // Environment variables are now available within this block
                        // Optionally print them for debugging purposes
                        sh 'printenv' // 可选：用于调试，检查环境变量是否正确加载
                    }
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
                    if [ \$(docker ps -aq -f name=${CONTAINER_NAME}) ]; then
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
                    docker run -d --name ${CONTAINER_NAME} \
                    --network app \
                    -p 39291:8080 \
                    -e DB_USERNAME=${env.DB_USERNAME} \
                    -e DB_PASSWORD=${env.DB_PASSWORD} \
                    ${IMAGE_NAME}:${IMAGE_TAG}
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
