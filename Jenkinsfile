pipeline {
  agent any

  environment {
    // Image name (repo must exist on Docker Hub)
    IMG = "docker.io/garsav/airline-api"
    // Short git SHA used as an immutable tag
    TAG = ""
  }

  options {
    timestamps()
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
        script {
          TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
          echo "Using image tag: ${TAG}"
        }
      }
    }

    stage('Sanity: Credentials & Tools') {
      steps {
        withCredentials([
          // Docker Hub (ID = 'dockerhub'): Username + Password (PAT)
          usernamePassword(credentialsId: 'dockerhub',
                           usernameVariable: 'DOCKERHUB_USER',
                           passwordVariable: 'DOCKERHUB_PSW'),
          // Minikube kubeconfig (ID = 'minikube-kubeconfig') as a secret file
          file(credentialsId: 'minikube-kubeconfig', variable: 'KCFG')
        ]) {
          sh '''
            echo "DockerHub user: $DOCKERHUB_USER"
            echo "Kubeconfig present at: $KCFG"
            kubectl version --client || { echo "kubectl missing"; exit 1; }
          '''
        }
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
        always {
          junit testResults: 'api-spring/build/test-results/test/*.xml', allowEmptyResults: true
          archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
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
                -Djib.to.image=${IMG}:${TAG} \
                -Djib.to.auth.username=${DOCKERHUB_USER} \
                -Djib.to.auth.password=${DOCKERHUB_PSW}
            """
          }
        }
      }
    }

    stage('Deploy to Minikube') {
      steps {
        withCredentials([file(credentialsId: 'minikube-kubeconfig', variable: 'KCFG')]) {
          withEnv(["KUBECONFIG=${KCFG}"]) {
            sh """
              kubectl get nodes
              # Update image on the existing deployment (create if missing)
              if kubectl get deploy/airline-api >/dev/null 2>&1; then
                kubectl set image deploy/airline-api airline-api=${IMG}:${TAG}
              else
                kubectl apply -f k8s/deployment.yaml
                kubectl apply -f k8s/service.yaml
                kubectl set image deploy/airline-api airline-api=${IMG}:${TAG} --record=true
              fi

              kubectl rollout status deploy/airline-api --timeout=90s
              kubectl get pods -o wide
            """
          }
        }
      }
    }

  } // <-- end stages

  post {
    always {
      archiveArtifacts artifacts: 'api-spring/build/libs/*.jar', allowEmptyArchive: true
    }
  }

} // <-- end pipeline
