pipeline {
    agent any
    parameters {
        choice(name: 'ACTION', choices: ['normal', 'deploy'], description: 'Choose whether to have normal ci or with deployment')
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
                expression { params.ACTION == 'deploy' }
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
                expression { params.ACTION == 'deploy' }
            }
            steps {
                sh 'gcloud container clusters get-credentials dcom-cluster --zone europe-west1-b --project dkkom-446515'
            }
        }
        // stage('Build Api Gateway') {
        //     steps {
        //         echo 'Building Api Gateway'
        //         dir('api gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage("Test Api Gateway") {
        //     steps {
        //         echo 'Testing Api Gateway'
        //         dir('api gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew test'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Api Gateway') {
        //     steps {
        //         dir('api gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Api Gateway') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('api gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Api Gateway") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         echo 'Dockerizing Api Gateway'
        //         dir('api gateway') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/api-gateway:latest'
        //         }
        //     }
        // }
        stage('create test base env') {
            agent {
                label 'local-tests-env'
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            steps {
                sh 'echo "Creating integration test environment"'
                sh 'docker-compose up -d'
                sh 'docker ps'
            }
        }
        stage('Setup services for test') {
            agent {
                label 'local-tests-env'
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            steps {
                sh 'echo "Setting up services for integration tests"'
                dir('Cave Service'){
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh './gradlew build'
                        sh 'docker build -f Dockerfile-test-env -t cave-service:latest .'
                        sh 'docker run --network=test-network -d --name cave-service cave-service:latest'
                        sh 'Cave service running on test environment'
                        sleep 10
                    }
                }
            }
        }
        stage('Run integration tests cave-service') {
            agent {
                label 'local-tests-env'
            }
            environment {
                GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
                GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
            }
            when {
                expression { params.ACTION == 'normal' }
            }
            steps {
                dir('Cave Service') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'docker stop cave-service'
                        sh 'docker build --build-arg GITLAB_USER=$GITLAB_USER --build-arg GITLAB_TOKEN=$GITLAB_TOKEN -f Dockerfile-run-test -t cave-service-tests:latest .'
                        sh 'docker run --rm --network=test-network cave-service-tests:latest'
                        sh 'docker start cave-service'
                        sh 'Cave service back running'
                        sleep 10
                    }
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
                sh 'docker-compose down'
                sh 'docker system prune -af'
            }
        }
        // stage('Deploy Api Gateway') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('api gateway') {
        //             echo 'Deploying Api Gateway'
        //             sh 'kubectl delete deployment api-gateway --ignore-not-found=true'
        //             sh 'kubectl apply -f api-gateway-deployment.yaml'
        //         }
        //     }
        // }

        // // voice service
        // stage('Build Voice Service') {
        //     steps {
        //         echo 'Building Voice Service'
        //         dir('voice-video-service') {
        //             sh 'echo $PATH'
        //             sh 'go version'
        //             sh 'go mod tidy'
        //             sh 'go mod download'
        //             sh 'go build -o voice-service'
        //         }
        //     }
        // }
        // stage("Test Voice Service") {
        //     steps {
        //         echo 'Testing Voice Service'
        //         dir('voice-video-service') {
        //             sh 'echo no teste'
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Voice Service') {
        //     steps {
        //         dir('voice-video-service') {
        //             withSonarQubeEnv('Sonarqube Server') {
        //                 sh 'sonar-scanner \
        //                     -Dsonar.projectKey=Voice-Service \
        //                     -Dsonar.sources=. \
        //                     -Dsonar.host.url=http://192.168.138.132:9000 \
        //                     -Dsonar.token=sqp_cc99befa0fa1ba7d0ccc1ed72660e4bc557fc2fe'
        //             }
        //         }
        //     }
        // }
        // // stage('Snyk Scan Voice Service') {
        // //     agent {
        // //         label 'snyk-agent'
        // //     }
        // //     steps {
        // //         dir('voice-video-service') {
        // //             sh 'ls -la'
        // //             sh 'snyk auth $SNYK_TOKEN'
        // //             sh 'snyk test --all-projects'
        // //         }
        // //     }
        // // }
        // stage("Dockerize Voice Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         echo 'Dockerizing Voice Service'
        //         dir('voice-video-service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/voice-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/voice-service:latest'
        //         }
        //     }
        // }
        // stage('Deploy Voice Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('voice-video-service') {
        //             echo 'Deploying Voice Service'
        //             sh 'kubectl delete deployment voice-service --ignore-not-found=true'
        //             sh 'kubectl apply -f voice-service-deployment.yaml'
        //         }
        //     }
        // }

        // // media service
        // stage('Build Media Service') {
        //     steps {
        //         dir('Media Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Media Service') {
        //     steps {
        //         dir('Media Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Media Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('Media Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Media Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         echo 'Dockerizing Api Gateway'
        //         dir('Media Service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/media-service:latest'
        //         }
        //     }
        // }

        // // Deploy Media Service
        // stage('Deploy Media Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Media Service') {
        //             echo 'Deploying Media Service'
        //             sh 'kubectl delete deployment media-service --ignore-not-found=true'
        //             sh 'kubectl apply -f media-service-deployment.yaml'
        //         }
        //     }
        // }

        // //cave service
        // stage('Build Cave Service') {
        //     steps {
        //         dir('Cave Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Cave Service') {
        //     steps {
        //         dir('Cave Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Cave Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('Cave Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Cave Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Cave Service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/cave-service:latest'
        //         }
        //     }
        // }

        // // Deploy Cave Service
        // stage('Deploy Cave Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Cave Service') {
        //             echo 'Deploying Cave Service'
        //             sh 'kubectl delete deployment cave-service --ignore-not-found=true'
        //             sh 'kubectl apply -f cave-service-deployment.yaml'
        //         }
        //     }
        // }

        // //message service
        // stage('Build Message Service') {
        //     steps {
        //         dir('Messaging Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Message Service') {
        //     steps {
        //         dir('Messaging Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Message Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('Messaging Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Message Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Messaging Service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/message-service:latest'
        //         }
        //     }
        // }

        // // Deploy Message Service
        // stage('Deploy Message Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Messaging Service') {
        //             echo 'Deploying Message Service'
        //             sh 'kubectl delete deployment message-service --ignore-not-found=true'
        //             sh 'kubectl apply -f message-service-deployment.yaml'
        //         }
        //     }
        // }

        // //permission service
        // stage('Build Permission Service') {
        //     steps {
        //         dir('PermissionsService') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Permission Service') {
        //     steps {
        //         dir('PermissionsService') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Permission Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('PermissionsService') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Permission Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('PermissionsService') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/permission-service:latest'
        //         }
        //     }
        // }

        // // Deploy Permission Service
        // stage('Deploy Permission Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('PermissionsService') {
        //             echo 'Deploying Permission Service'
        //             sh 'kubectl delete deployment permission-service --ignore-not-found=true'
        //             sh 'kubectl apply -f permission-service.yaml'
        //         }
        //     }
        // }

        // //user service service
        // stage('Build User Service') {
        //     steps {
        //         dir('User Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis User Service') {
        //     steps {
        //         dir('User Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan User Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('User Service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize User Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('User Service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-service:latest'
        //         }
        //     }
        // }

        // // Deploy User Service
        // stage('Deploy User Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('User Service') {
        //             echo 'Deploying User Service'
        //             sh 'kubectl delete deployment user-service --ignore-not-found=true'
        //             sh 'kubectl apply -f user-service-deployment.yaml'
        //         }
        //     }
        // }

        // //user presence service service
        // stage('Build User Presence Service') {
        //     steps {
        //         dir('user-presence-service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis User Presence Service') {
        //     steps {
        //         dir('user-presence-service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan User Presence Service') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('user-presence-service') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize User Presence Service") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('user-presence-service') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/user-presence-service:latest'
        //         }
        //     }
        // }

        // // Deploy User Presence Service
        // stage('Deploy User Presence Service') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('user-presence-service') {
        //             echo 'Deploying User Presence Service'
        //             sh 'kubectl delete deployment user-presence-service --ignore-not-found=true'
        //             sh 'kubectl apply -f user-presence-service.yaml'
        //         }
        //     }
        // }

        // //websocket gateway
        // stage('Build Websocket Gateway') {
        //     steps {
        //         dir('Websocket-gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh './gradlew build'
        //             }
        //         }
        //     }
        // }
        // stage('Sonarqube Analysis Websocket Gateway') {
        //     steps {
        //         dir('Websocket-gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh './gradlew sonar'
        //             }
        //         }
        //     }
        // }
        // stage('Snyk Scan Websocket Gateway') {
        //     agent {
        //         label 'snyk-agent'
        //     }
        //     steps {
        //         dir('Websocket-gateway') {
        //             withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
        //                 sh 'ls -la'
        //                 sh 'chmod +x ./gradlew'
        //                 sh 'snyk auth $SNYK_TOKEN'
        //                 sh 'snyk test --all-projects'
        //             }
        //         }
        //     }
        // }
        // stage("Dockerize Websocket gateway") {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Websocket-gateway') {
        //             sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
        //             sh 'docker build -t europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:latest .'
        //             sh 'docker push europe-west1-docker.pkg.dev/dkkom-446515/cluster-repo/websocket-gateway:latest'
        //         }
        //     }
        // }

        // // Deploy Websocket Gateway
        // stage('Deploy Websocket Gateway') {
        //     when {
        //         expression { params.ACTION == 'deploy' }
        //     }
        //     steps {
        //         dir('Websocket-gateway') {
        //             echo 'Deploying Websocket Gateway'
        //             sh 'kubectl delete deployment websocket-gateway --ignore-not-found=true'
        //             sh 'kubectl apply -f websocket-gateway-deployment.yaml'
        //         }
        //     }
        // }
    }
}
