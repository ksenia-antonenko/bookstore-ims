pipeline {
    agent any

    environment {
        // ---- customize these ----
        APP_NAME          = 'bookstore-ims'
        DOCKER_IMAGE_NAME = 'your-dockerhub-username/bookstore'
        DOCKER_CREDS_ID   = 'dockerhub-creds'
        KUBECONFIG_CRED   = 'kubeconfig-bookstore'
        KUBE_NAMESPACE    = 'bookstore'
        // -------------------------

        GRADLE_OPTS = "-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false"
        IMAGE_TAG   = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build (compile only)') {
            steps { sh './gradlew clean build -x test -x integrationTest' }
        }

        stage('Unit Tests') {
            steps { sh './gradlew test' }
            post { always { junit '**/build/test-results/test/*.xml' } }
        }

        stage('Integration Tests') {
            steps { sh './gradlew integrationTest' }
            post { always { junit '**/build/test-results/integrationTest/*.xml' } }
        }

        stage('Static Analysis') {
            steps {
                sh './gradlew checkstyleMain checkstyleTest jacocoTestReport jacocoIntegrationTestReport'
            }
            post {
                always {
                    recordIssues(tools: [checkStyle(pattern: '**/build/reports/checkstyle/*.xml')])
                    junit '**/build/test-results/**/*.xml'
                }
            }
        }

        stage('Build Jar') {
            steps { sh './gradlew bootJar' }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build & Push') {
            when { anyOf { branch 'main'; branch 'master' } }
            steps {
                script {
                    docker.withRegistry('', DOCKER_CREDS_ID) {
                        def img = docker.build("${DOCKER_IMAGE_NAME}:${IMAGE_TAG}")
                        img.push()
                        img.push('latest')
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when { anyOf { branch 'main'; branch 'master' } }
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CRED, variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                        export KUBECONFIG="$KUBECONFIG_FILE"
                        kubectl config current-context

                        # Ensure namespace exists
                        kubectl get ns ${KUBE_NAMESPACE} >/dev/null 2>&1 || \
                          kubectl create ns ${KUBE_NAMESPACE}

                        # Apply manifests (Deployment, Service, etc.)
                        kubectl -n ${KUBE_NAMESPACE} apply -f k8s/

                        # Patch Deployment image to the new tag (keeps manifests generic)
                        kubectl -n ${KUBE_NAMESPACE} set image deployment/${APP_NAME} \
                          ${APP_NAME}=${DOCKER_IMAGE_NAME}:${IMAGE_TAG} --record

                        # Wait for rollout to complete
                        kubectl -n ${KUBE_NAMESPACE} rollout status deployment/${APP_NAME} --timeout=120s
                    '''
                }
            }
        }

        stage('Smoke Test') {
            when { anyOf { branch 'main'; branch 'master' } }
            steps {
                withCredentials([file(credentialsId: KUBECONFIG_CRED, variable: 'KUBECONFIG_FILE')]) {
                    sh '''
                        export KUBECONFIG="$KUBECONFIG_FILE"
                        # Example: hit readiness/liveness or a simple GET (adjust service/ingress URL)
                        # If you have a ClusterIP + Ingress, curl the ingress host.
                        # Here we just log the service/endpoints for visibility:
                        kubectl -n ${KUBE_NAMESPACE} get deploy,svc,endpoints | sed "s/^/K8S: /"
                    '''
                }
            }
        }
    }

    post {
        always { cleanWs() }
    }
}