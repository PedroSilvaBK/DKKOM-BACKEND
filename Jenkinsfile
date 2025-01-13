pipeline {
    agent any
    parameters {
        choice(name: 'ACTION', choices: ['normal', 'deploy-prod', 'deploy-staging'], description: 'Choose whether to have normal ci or with deployment')
    }
    environment {
        GOOGLE_APPLICATION_CREDENTIALS = credentials('GCP_KEY') // Use the ID from the stored credentials
        GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
        GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
        SNYK_TOKEN = credentials('SNYK_TOKEN')
        SONARQUBE_ENV = credentials('SONARQUBE_TOKEN')
        PATH = "/opt/sonar-scanner/bin:/usr/local/go/bin:${env.PATH}"
    }
    stages {
        stage('Authenticate with Google Cloud') {
            when {
                expression { params.ACTION == 'deploy-prod' || params.ACTION == 'deploy-staging' }
            }
            steps {
                script {
                    sh 'echo $GOOGLE_APPLICATION_CREDENTIALS > gcloud-key.json'
                    sh '''
                        gcloud auth activate-service-account --key-file=$GOOGLE_APPLICATION_CREDENTIALS
                    '''
                }
            }
        }
        stage('Get Cluster Credentials') {
            when {
                expression { params.ACTION == 'deploy-prod' || params.ACTION == 'deploy-staging' }
            }
            steps {
                sh 'gcloud container clusters get-credentials dcom-cluster --zone europe-west1-b --project dkkom-446515'
            }
        }
        stage('Create and configure external services for tests') {
            agent {
                label 'local-tests-env'
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            steps {
                sh 'echo "Creating integration test environment"'
                sh 'docker-compose up -d'
                sleep 5
                sh 'docker exec mysql_db mysql -uroot -padmin -e "CREATE DATABASE IF NOT EXISTS users_db;"'
                sleep 5
                sh '''
                    docker exec scylla_db cqlsh -e "CREATE KEYSPACE IF NOT EXISTS message_space WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};"
                '''
                sh 'docker ps'
            }
        }
        stage('Setup micro-services for test') {
            agent {
                label 'local-tests-env'
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            environment {
                GOOGLE_CLIENT_SECRET = credentials('GOOGLE_CLIENT_SECRET')
            }
            steps {
                sh 'echo "Setting up services for integration tests"'
                dir('Cave Service'){
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t cave-service:latest .'
                        sh 'docker run --network=test-network -d --name cave-service cave-service:latest'
                        sh 'echo Cave service running on test environment'
                    }
                }
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t api-gateway:latest .'
                        sh 'docker run --network=test-network -d -e GOOGLE_CLIENT_SECRET=$GOOGLE_CLIENT_SECRET --name api-gateway api-gateway:latest'
                        sh 'echo Api gateway running on test environment'
                    }
                }
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t user-service:latest .'
                        sh 'docker run --network=test-network -d --name user-service user-service:latest'
                        sh 'echo User Service running on test environment'
                    }
                }
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t websocket-gateway:latest .'
                        sh 'docker run --network=test-network -d --name websocket-gateway websocket-gateway:latest'
                        sh 'echo websocket-gateway running on test environment'
                    }
                }
                dir('PermissionsService') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t permission-service:latest .'
                        sh 'docker run --network=test-network -d --name permission-service permission-service:latest'
                        sh 'echo permission-service running on test environment'
                    }
                }
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t user-presence-service:latest .'
                        sh 'docker run --network=test-network -d --name user-presence-service user-presence-service:latest'
                        sh 'echo user-presence-service running on test environment'
                    }
                }
                dir('Messaging Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                        sh 'docker build -f Dockerfile-test-env -t message-service:latest .'
                        sh 'docker run --network=test-network -d --name message-service message-service:latest'
                        sh 'echo message-service running on test environment'
                    }
                }
                sh 'echo Service Deployed and running'
            }
        }
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage("Test Api Gateway") {
            agent {
                label 'local-tests-env'
            }
            environment {
                GOOGLE_CLIENT_SECRET = credentials('GOOGLE_CLIENT_SECRET')
            }
            steps {
                echo 'Unit and integration tests Api Gateway'
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop api-gateway'
                        sh 'docker build --build-arg GOOGLE_CLIENT_SECRET=$GOOGLE_CLIENT_SECRET -f Dockerfile-run-test -t api-gateway-tests:latest .'
                        sh 'docker run --rm --network=test-network api-gateway-tests:latest'
                        sh 'docker image rm api-gateway-tests:latest'
                        sh 'docker start api-gateway'
                        sh 'echo Api gateway back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Api Gateway') {
            steps {
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Api Gateway') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Api Gateway Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                echo 'Dockerizing Api Gateway'
                dir('api gateway') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:latest'
                }
            }
        }

        stage('Deploy Api Gateway Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('api gateway') {
                    echo 'Deploying Api Gateway with Helm'

                    sh '''
                    helm upgrade --install api-gateway ./api-gateway-helm \
                        -f ./api-gateway-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize Api Gateway Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                echo 'Dockerizing Api Gateway'
                dir('api gateway') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:staging'
                }
            }
        }

        stage('Deploy Api Gateway Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('api gateway') {
                    echo 'Deploying Api Gateway with Helm'

                    sh '''
                    helm upgrade --install api-gateway-staging ./api-gateway-helm \
                        -f ./api-gateway-helm/values-staging.yaml
                    '''
                }
            }
        }

        // voice service
        stage('Build Voice Service') {
            steps {
                echo 'Building Voice Service'
                dir('voice-video-service') {
                    sh 'echo $PATH'
                    sh 'go version'
                    sh 'go mod tidy'
                    sh 'go mod download'
                    sh 'go build -o voice-service'
                }
            }
        }
        stage("Test Voice Service") {
            steps {
                echo 'Testing Voice Service'
                dir('voice-video-service') {
                    sh 'echo no teste'
                }
            }
        }
        stage('Sonarqube Analysis Voice Service') {
            steps {
                dir('voice-video-service') {
                    withSonarQubeEnv('Sonarqube Server') {
                        sh 'sonar-scanner \
                            -Dsonar.projectKey=Voice-Service \
                            -Dsonar.sources=. \
                            -Dsonar.host.url=http://192.168.138.132:9000 \
                            -Dsonar.token=sqp_cc99befa0fa1ba7d0ccc1ed72660e4bc557fc2fe'
                    }
                }
            }
        }
        stage('Snyk Scan Voice Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('voice-video-service') {
                    sh 'ls -la'
                    sh 'snyk auth $SNYK_TOKEN'
                    sh 'snyk test --all-projects'
                }
            }
        }
        stage("Dockerize Voice Service") {
            when {
                expression { params.ACTION == 'deploy-prod' || params.ACTION == 'deploy-staging' }
            }
            steps {
                echo 'Dockerizing Voice Service'
                dir('voice-video-service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/voice-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/voice-service:latest'
                }
            }
        }
        stage('Deploy Voice Service') {
            when {
                expression { params.ACTION == 'deploy-prod' || params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('voice-video-service') {
                    echo 'Deploying Voice Service'
                    sh 'kubectl delete deployment voice-service --ignore-not-found=true'
                    sh 'kubectl apply -f voice-service-deployment.yaml'
                }
            }
        }

        // media service
        stage('Build Media Service') {
            steps {
                dir('Media Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Media Service') {
            steps {
                dir('Media Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Media Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('Media Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Media Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                echo 'Dockerizing Api Gateway'
                dir('Media Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:latest'
                }
            }
        }

        // Deploy Media Service
        stage('Deploy Media Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Media Service') {
                    echo 'Deploying Media Service with Helm'
                    sh '''
                    helm upgrade --install media-service ./media-service-helm \
                        -f ./media-service-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize Media Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                echo 'Dockerizing Media Service'
                dir('Media Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:staging'
                }
            }
        }

        // Deploy Media Service
        stage('Deploy Media Service Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Media Service') {
                    echo 'Deploying Media Service with Helm'
                    sh '''
                    helm upgrade --install media-service ./media-service-helm \
                        -f ./media-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //cave service
        stage('Build Cave Service') {
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test Cave Service') {
            agent {
                label 'local-tests-env'
            }
            environment {
                GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
                GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
            }
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop cave-service'
                        sh 'docker build --build-arg GITLAB_USER=$GITLAB_USER --build-arg GITLAB_TOKEN=$GITLAB_TOKEN -f Dockerfile-run-test -t cave-service-tests:latest .'
                        sh 'docker run --rm --network=test-network cave-service-tests:latest'
                        sh 'docker image rm cave-service-tests:latest'
                        sh 'docker start cave-service'
                        sh 'echo Cave service back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Cave Service') {
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Cave Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Cave Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Cave Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:latest'
                }
            }
        }

        // Deploy Cave Service
        stage('Deploy Cave Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Cave Service') {
                    echo 'Deploying Cave Service with Helm'
                    sh '''
                    helm upgrade --install cave-service ./cave-service-helm \
                        -f ./cave-service-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize Cave Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Cave Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:staging'
                }
            }
        }

        // Deploy Cave Service
        stage('Deploy Cave Service Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Cave Service') {
                    echo 'Deploying Cave Service with Helm'
                    sh '''
                    helm upgrade --install cave-service-staging ./cave-service-helm \
                        -f ./cave-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //message service
        stage('Build Message Service') {
            steps {
                dir('Messaging Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test Message Service') {
            agent {
                label 'local-tests-env'
            }
            steps {
                dir('Messaging Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop message-service'
                        sh 'docker build -f Dockerfile-run-test -t message-service-tests:latest .'
                        sh 'docker run --rm --network=test-network message-service-tests:latest'
                        sh 'docker image rm message-service-tests:latest'
                        sh 'docker start message-service'
                        sh 'echo Message service back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Message Service') {
            steps {
                dir('Messaging Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Message Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('Messaging Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Message Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Messaging Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:latest'
                }
            }
        }

        // Deploy Message Service
        stage('Deploy Message Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Messaging Service') {
                    echo 'Deploying Messaging Service'

                    sh '''
                    helm upgrade --install message-service ./api-gateway-helm \
                        -f ./message-service-helm/values.yaml
                    '''
                }
            }
        }
        
        stage("Dockerize Message Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Messaging Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:staging'
                }
            }
        }

        // Deploy Message Service
        stage('Deploy Message Service Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Messaging Service') {
                    echo 'Deploying Messaging Service'

                    sh '''
                    helm upgrade --install message-service-staging ./message-service-helm \
                        -f ./message-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //permission service
        stage('Build Permission Service') {
            steps {
                dir('PermissionsService') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test Permission Service') {
            agent {
                label 'local-tests-env'
            }
            environment {
                GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
                GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
            }
            steps {
                dir('PermissionsService') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop permission-service'
                        sh 'docker build --build-arg GITLAB_USER=$GITLAB_USER --build-arg GITLAB_TOKEN=$GITLAB_TOKEN -f Dockerfile-run-test -t permission-service-tests:latest .'
                        sh 'docker run --rm --network=test-network permission-service-tests:latest'
                        sh 'docker image rm permission-service-tests:latest'
                        sh 'docker start permission-service'
                        sh 'echo permission-service back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Permission Service') {
            steps {
                dir('PermissionsService') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Permission Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('PermissionsService') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Permission Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('PermissionsService') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:latest'
                }
            }
        }

        // Deploy Permission Service
        stage('Deploy Permission Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('PermissionsService') {
                    echo 'Deploying Permission Service with Helm'

                    sh '''
                    helm upgrade --install permission-service ./permission-service-helm \
                        -f ./permission-service-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize Permission Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('PermissionsService') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:staging'
                }
            }
        }

        // Deploy Permission Service
        stage('Deploy Permission Service Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('PermissionsService') {
                    echo 'Deploying Permission Service with Helm'

                    sh '''
                    helm upgrade --install permission-service-staging ./permission-service-helm \
                        -f ./permission-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //user service service
        stage('Build User Service') {
            steps {
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test User Service') {
            agent {
                label 'local-tests-env'
            }
            steps {
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop user-service'
                        sh 'docker build -f Dockerfile-run-test -t user-service-tests:latest .'
                        sh 'docker run --rm --network=test-network user-service-tests:latest'
                        sh 'docker image rm user-service-tests:latest'
                        sh 'docker start user-service'
                        sh 'echo User Service back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis User Service') {
            steps {
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan User Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('User Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize User Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('User Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:latest'
                }
            }
        }

        // Deploy User Service
        stage('Deploy User Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('User Service') {
                    echo 'Deploying User Service with Helm'

                    sh '''
                    helm upgrade --install user-service ./user-service-helm \
                        -f ./user-service-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize User Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('User Service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:staging'
                }
            }
        }

        // Deploy User Service
        stage('Deploy User Service Production Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('User Service') {
                    echo 'Deploying User Service with Helm'

                    sh '''
                    helm upgrade --install user-service-staging ./user-service-helm \
                        -f ./user-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //user presence service service
        stage('Build User Presence Service') {
            steps {
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test User Presence Service') {
            agent {
                label 'local-tests-env'
            }
            steps {
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop user-presence-service'
                        sh 'docker build -f Dockerfile-run-test -t user-presence-service-tests:latest .'
                        sh 'docker run --rm --network=test-network user-presence-service-tests:latest'
                        sh 'docker image rm user-presence-service-tests:latest'
                        sh 'docker start user-presence-service'
                        sh 'echo User Presence Service back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis User Presence Service') {
            steps {
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan User Presence Service') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('user-presence-service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize User Presence Service Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('user-presence-service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:latest'
                }
            }
        }

        // Deploy User Presence Service
        stage('Deploy User Presence Service Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('user-presence-service') {
                    echo 'Deploying user presence service with Helm'

                    sh '''
                    helm upgrade --install user-presence-service ./user-presence-service-helm \
                        -f ./user-presence-service-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize User Presence Service Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('user-presence-service') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:staging'
                }
            }
        }

        // Deploy User Presence Service
        stage('Deploy User Presence Service Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('user-presence-service') {
                    echo 'Deploying user presence service with Helm'

                    sh '''
                    helm upgrade --install user-presence-service-staging ./user-presence-service-helm \
                        -f ./user-presence-service-helm/values-staging.yaml
                    '''
                }
            }
        }

        //websocket gateway
        stage('Build Websocket Gateway') {
            steps {
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build -x test'
                    }
                }
            }
        }
        stage('Unit and Integration Test Websocket Gateway') {
            agent {
                label 'local-tests-env'
            }
            environment {
                GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
                GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
            }
            steps {
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop websocket-gateway'
                        sh 'docker build --build-arg GITLAB_USER=$GITLAB_USER --build-arg GITLAB_TOKEN=$GITLAB_TOKEN -f Dockerfile-run-test -t websocket-gateway-tests:latest .'
                        sh 'docker run --rm --network=test-network websocket-gateway-tests:latest'
                        sh 'docker image rm websocket-gateway-tests:latest'
                        sh 'docker start websocket-gateway'
                        sh 'echo websocket-gateway back running'
                    }
                }
            }
        }
        stage('Sonarqube Analysis Websocket Gateway') {
            steps {
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew sonar'
                    }
                }
            }
        }
        stage('Snyk Scan Websocket Gateway') {
            agent {
                label 'snyk-agent'
            }
            steps {
                dir('Websocket-gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh 'snyk auth $SNYK_TOKEN'
                        sh 'snyk test --all-projects'
                    }
                }
            }
        }
        stage("Dockerize Websocket gateway Production") {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Websocket-gateway') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-prod -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:latest .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:latest'
                }
            }
        }

        // Deploy Websocket Gateway
        stage('Deploy Websocket Gateway Production') {
            when {
                expression { params.ACTION == 'deploy-prod' }
            }
            steps {
                dir('Websocket-gateway') {
                    echo 'Deploying websocket gateway with Helm'

                    sh '''
                    helm upgrade --install websocket-gateway ./websocket-gateway-helm \
                        -f ./websocket-gateway-helm/values.yaml
                    '''
                }
            }
        }

        stage("Dockerize Websocket gateway Staging") {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Websocket-gateway') {
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker build -f Dockerfile-staging -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:staging .'
                    sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:staging'
                }
            }
        }

        // Deploy Websocket Gateway
        stage('Deploy Websocket Gateway Staging') {
            when {
                expression { params.ACTION == 'deploy-staging' }
            }
            steps {
                dir('Websocket-gateway') {
                    echo 'Deploying websocket gateway with Helm'

                    sh '''
                    helm upgrade --install websocket-gateway-staging ./websocket-gateway-helm \
                        -f ./websocket-gateway-helm/values-staging.yaml
                    '''
                }
            }
        }

        stage('clean test env') {
            agent {
                label 'local-tests-env'
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            steps {
                sh 'echo "Cleaning integration test environment"'
                sh 'docker image prune -f'
                sh 'docker stop $(docker ps -q)'
                sh 'docker-compose down'
                sh 'docker container prune -f'
                //
                sh 'docker image rm cave-service:latest || true'
                sh 'docker image rm api-gateway:latest || true'
                sh 'docker image rm user-service:latest || true'
                sh 'docker image rm websocket-gateway:latest || true'
                sh 'docker image rm permission-service:latest || true'
                sh 'docker image rm user-presence-service:latest || true'
                sh 'docker image rm message-service:latest || true'
                // sh 'docker system prune -af'
            }
        }

    }
}
