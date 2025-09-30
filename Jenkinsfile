pipeline {
  agent any

  environment {
    REGISTRY = 'docker.io'
    IMAGE    = 'garsav/airline-api'   // change only if you rename the Docker Hub repo
  }

  options {
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script {
          // short git SHA used as image tag
          TAG = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
          echo "Using image tag: ${TAG}"
        }
      }
    }

    stage('Sanity: Credentials & Tools') {
      steps {
        withCredentials([
          usernamePassword(credentialsId: 'dockerhub',
                           usernameVariable: 'DOCKERHUB_USER',
                           passwordVariable: 'DOCKERHUB_PSW'),
          file(credentialsId: 'minikube-kubeconfig', variable: 'KCFG')
        ]) {
          sh '''
            echo "DockerHub user: $DOCKERHUB_USER"
            echo "Kubeconfig injected"
            kubectl version --client
          '''
        }
      }
    }

    stage('Build (Gradle)') {
      steps {
        dir('api-spring') {
          sh 'chmod +x ./gradlew'
          sh './gradlew clean build'
          junit 'build/test-results/test/*.xml'
          archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
      }
    }

    stage('Containerize & Push (Jib)') {
      steps {
        dir('api-spring') {
          withCredentials([
            usernamePassword(credentialsId: 'dockerhub',
                             usernameVariable: 'DOCKERHUB_USER',
                             passwordVariable: 'DOCKERHUB_PSW')
          ]) {
            // Jib reads creds from env (configured in build.gradle)
            withEnv(["JIB_TO_IMAGE=${env.REGISTRY}/${env.IMAGE}:${TAG}"]) {
              sh './gradlew jib'
            }
          }
        }
      }
    }

    stage('Deploy to Minikube') {
      when {
        expression { return fileExists('k8s/deployment.yaml') && fileExists('k8s/service.yaml') }
      }
      steps {
        withCredentials([file(credentialsId: 'minikube-kubeconfig', variable: 'KCFG')]) {
          sh '''
            set -e
            export KUBECONFIG="$KCFG"

            # Update image/tag and apply manifests
            kubectl set image deployment/airline-api airline-api=${REGISTRY}/${IMAGE}:'"${TAG}"' --record || true
            kubectl apply -f k8s/deployment.yaml
            kubectl apply -f k8s/service.yaml

            # Wait for rollout, then show resources
            kubectl rollout status deployment/airline-api --timeout=120s
            kubectl get svc,deploy,pods -l app=airline-api -o wide
          '''
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
    }
    success {
      echo "✅ Pipeline OK — pushed ${REGISTRY}/${IMAGE}:${TAG} and deployed to Minikube."
    }
    failure {
      echo "❌ Pipeline failed. Check the stage logs above."
    }
  }
}
