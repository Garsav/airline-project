pipeline {
  agent any
  options { timestamps() }

  environment {
    // Jenkins credentials you must create:
    // - "dockerhub": Username/Password (or PAT) for Docker Hub
    // - "minikube-kubeconfig": Secret file that contains your ~/.kube/config
    DOCKERHUB = credentials('dockerhub')
    KCFG      = credentials('minikube-kubeconfig')
    IMAGE     = "docker.io/${DOCKERHUB_USR}/airline-api:latest"
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
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

    // Use Jib (no Docker-in-Jenkins required) to push image to Docker Hub
    stage('Containerize & Push (Jib)') {
      steps {
        dir('api-spring') {
          sh """
            ./gradlew jib \
              -Djib.to.image=${IMAGE} \
              -Djib.to.auth.username=${DOCKERHUB_USR} \
              -Djib.to.auth.password=${DOCKERHUB_PSW}
          """
        }
      }
    }

    stage('Deploy to Minikube') {
      steps {
        sh '''
          mkdir -p .k
          cp "$KCFG" .k/config
          # Apply service first (idempotent)
          kubectl --kubeconfig=.k/config apply -f k8s/service.yaml
          kubectl --kubeconfig=.k/config apply -f k8s/deployment.yaml
          kubectl --kubeconfig=.k/config rollout status deploy/airline-api --timeout=120s
        '''
      }
    }
  }

  post {
    always {
      junit testResults: 'api-spring/build/test-results/test/*.xml', allowEmptyResults: true
      archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
    }
  }
}
