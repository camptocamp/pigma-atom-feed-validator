
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
}
