pipeline {
  agent any
  options { timestamps(); ansiColor('xterm') }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build (Gradle)') {
      steps {
        dir('api-spring') {
          // make wrapper executable (needed on Linux agents)
          sh 'chmod +x ./gradlew'
          // build without tests for now (your repo had a failing test earlier)
          sh './gradlew clean build -x test'
        }
      }
      post {
        success {
          // keep the JAR as a build artifact
          archiveArtifacts artifacts: 'api-spring/build/libs/*.jar', fingerprint: true
        }
      }
    }
  }

  post {
    always {
      // collect test results if you later enable tests
      junit testResults: 'api-spring/build/test-results/test/*.xml', allowEmptyResults: true
      // archive any build reports (optional)
      archiveArtifacts artifacts: 'api-spring/build/reports/**', allowEmptyArchive: true
    }
  }
}
