pipeline {
  agent any
  options { timestamps() }   // removed ansiColor

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
  }

  post {
    always {
      junit testResults: 'api-spring/build/test-results/test/*.xml', allowEmptyResults: true
      archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
    }
  }
}
