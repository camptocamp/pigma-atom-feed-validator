#!/usr/bin/groovy

@Library('c2c-pipeline-library') import static com.camptocamp.utils.*

dockerBuild {

  stage('Checking out the repository') {
    git url: "https://github.com/camptocamp/pigma-atom-feed-validator.git",
      branch: "${env.BRANCH_NAME}"
  }
  stage('validate PIGMA ATOM feed') {
    withDockerContainer(image: 'groovy') {
      sh "/usr/bin/groovy validator.groovy > xunit.xml"
      junit allowEmptyResults: true, testResults: 'xunit.xml'
    }
  }

  stage('Announce on slack') {
    withCredentials([string(credentialsId: 'georchestra-slack-bot-token', variable: 'TOKEN')]) {
          sh "docker run --rm -e BOT_TOKEN=$TOKEN -e MESSAGE=\"[${JOB_NAME}] build finished: ${BUILD_URL}\" pmauduit/slack_notifier:latest"
    } // withCreds
  }
}
