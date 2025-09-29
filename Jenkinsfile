pipeline {
  agent any
  options { timestamps(); ansiColor('xterm') }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Gradle Build (Spring)') {
      steps {
        dir('api-spring') {
          sh './gradlew clean build -x test'
        }
      }
      post {
        success { archiveArtifacts artifacts: 'api-spring/build/libs/*.jar', fingerprint: true }
      }
    }

    stage('Build ETL Image') {
      steps {
        sh 'docker build -t airline-etl:ci ./etl'
      }
    }

    stage('Build API Image') {
      steps {
        sh '''
          cp api-spring/build/libs/*.jar api-spring/app.jar
          docker build -t airline-api:ci ./api-spring
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
