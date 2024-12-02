pipeline {
    agent any
    environment {
        GOOGLE_APPLICATION_CREDENTIALS = credentials('GCP_KEY') // Use the ID from the stored credentials
        GITLAB_USER = credentials('SHARED_LIBRARY_USERNAME')
        GITLAB_TOKEN = credentials('SHARED_LIBRARY_PASSWORD')
    }
    stages {
        stage('Authenticate with Google Cloud') {
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
            steps {
                sh 'gcloud container clusters get-credentials dcom-cluster --zone europe-west1-b --project d-com-437216'
            }
        }
        stage('Build Api Gateway') {
            steps {
                echo 'Building Api Gateway'
                dir('api gateway') {
                    withEnv(['GRADLE_USER_HOME=$WORKSPACE/.gradle']) {
                        sh 'ls -la'
                        sh 'chmod +x ./gradlew'
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Api Gateway") {
            steps {
                echo 'Dockerizing Api Gateway'
                dir('api gateway') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/api-gateway:latest'
                }
            }
        }
        stage('Deploy Api Gateway') {
            steps {
                dir('api gateway') {
                    echo 'Deploying Api Gateway'
                    sh 'kubectl apply -f api-gateway-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Media Service") {
            steps {
                echo 'Dockerizing Api Gateway'
                dir('Media Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/media-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/media-service:latest'
                }
            }
        }

        // Deploy Media Service
        stage('Deploy Media Service') {
            steps {
                dir('Media Service') {
                    echo 'Deploying Media Service'
                    sh 'kubectl apply -f media-service-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Cave Service") {
            steps {
                dir('Cave Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/cave-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/cave-service:latest'
                }
            }
        }

        // Deploy Cave Service
        stage('Deploy Cave Service') {
            steps {
                dir('Cave Service') {
                    echo 'Deploying Cave Service'
                    sh 'kubectl apply -f cave-service-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Message Service") {
            steps {
                dir('Messaging Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/message-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/message-service:latest'
                }
            }
        }

        // Deploy Message Service
        stage('Deploy Message Service') {
            steps {
                dir('Messaging Service') {
                    echo 'Deploying Message Service'
                    sh 'kubectl apply -f message-service-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Permission Service") {
            steps {
                dir('PermissionsService') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/permission-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/permission-service:latest'
                }
            }
        }

        // Deploy Permission Service
        stage('Deploy Permission Service') {
            steps {
                dir('PermissionsService') {
                    echo 'Deploying Permission Service'
                    sh 'kubectl apply -f permission-service.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize User Service") {
            steps {
                dir('User Service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-service:latest'
                }
            }
        }

        // Deploy User Service
        stage('Deploy User Service') {
            steps {
                dir('User Service') {
                    echo 'Deploying User Service'
                    sh 'kubectl apply -f user-service-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize User Presence Service") {
            steps {
                dir('user-presence-service') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-presence-service:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/user-presence-service:latest'
                }
            }
        }

        // Deploy User Presence Service
        stage('Deploy User Presence Service') {
            steps {
                dir('user-presence-service') {
                    echo 'Deploying User Presence Service'
                    sh 'kubectl apply -f user-presence-service-deployment.yaml'
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
                        sh './gradlew build'
                    }
                }
            }
        }
        stage("Dockerize Websocket gateway") {
            steps {
                dir('Websocket-gateway') {
                    sh 'docker build -t europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/websocket-gateway:latest .'
                    sh 'gcloud auth configure-docker europe-west1-docker.pkg.dev || true'
                    sh 'docker push europe-west1-docker.pkg.dev/d-com-437216/cluster-repo/websocket-gateway:latest'
                }
            }
        }

        // Deploy Websocket Gateway
        stage('Deploy Websocket Gateway') {
            steps {
                dir('Websocket-gateway') {
                    echo 'Deploying Websocket Gateway'
                    sh 'kubectl apply -f websocket-gateway-deployment.yaml'
                }
            }
        }
    }
}
