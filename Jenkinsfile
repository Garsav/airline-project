pipeline {
  agent any
  options { timestamps() }

  environment {
    // Will fail early if these IDs are not found:
    DOCKERHUB = credentials('dockerhub')               // username/password or token
    KCFG      = credentials('minikube-kubeconfig')     // secret file (kubeconfig)
    IMAGE     = "docker.io/${DOCKERHUB_USR}/airline-api:latest"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Sanity: Credentials & Tools') {
      steps {
        sh '''
          echo "DockerHub user: ${DOCKERHUB_USR}"
          echo "Kubeconfig file path injected as: $KCFG"
          # Show kubectl version (must be installed in Jenkins container)
          kubectl version --client || { echo "kubectl missing in Jenkins container"; exit 1; }
        '''
      }
    }

    stage('Build (Gradle)') {
      steps {
        dir('api-spring') {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean build'
        }
      }
      post {
        success {
          archiveArtifacts artifacts: 'api-spring/build/libs/*.jar', fingerprint: true
        }
      }
    }

    stage('Containerize & Push (Jib)') {
    steps {
        dir('api-spring') {
            withCredentials([usernamePassword(credentialsId: 'dockerhub',
                                             usernameVariable: 'DOCKERHUB_USER',
                                             passwordVariable: 'DOCKERHUB_PSW')]) {
                sh """
                    ./gradlew jib \
                        -Djib.to.image=docker.io/${DOCKERHUB_USER}/airline-api:latest \
                        -Djib.to.auth.username=$DOCKERHUB_USER \
                        -Djib.to.auth.password=$DOCKERHUB_PSW
                """
            }
        }
    }

    stage('Deploy to Minikube') {
      steps {
        sh '''
          mkdir -p .k
          cp "$KCFG" .k/config

          echo "Using kubeconfig:"
          ls -l .k/config || true
          kubectl --kubeconfig=.k/config config current-context || true

          kubectl --kubeconfig=.k/config apply -f k8s/service.yaml
          kubectl --kubeconfig=.k/config apply -f k8s/deployment.yaml
          kubectl --kubeconfig=.k/config rollout status deploy/airline-api --timeout=120s
        '''
      }
    }
  }

  post {
    always {
      script {
        // Only publish tests if the directory exists (prevents MissingContextVariable noise)
        if (fileExists('api-spring/build/test-results/test')) {
          junit testResults: 'api-spring/build/test-results/test/*.xml', allowEmptyResults: true
        } else {
          echo 'No test results found; skipping junit publish.'
        }
      }
      archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
    }
  }
}
