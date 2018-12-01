#!/groovy
properties properties: [
    [$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '10', numToKeepStr: '10']],
    disableConcurrentBuilds()
]

node {

  env.JAVA_HOME = tool 'jdk-8-oracle'
  def mvnHome = tool 'maven latest'
  env.PATH = "${env.JAVA_HOME}/bin:${mvnHome}/bin:${env.PATH}"
  def err = null
  def mvnOpts = "-V -U --batch-mode"
  currentBuild.result = "SUCCESS"

  timestamps {

    try {

      dir(projectHome) {

        stage('Checkout project') {
          checkout scm
        }

        stage('Build') {
          sh "mvn clean install ${mvnOpts}"
        }
        stage('I-Test') {
          sh "mvn integration-test -Pitest ${mvnOpts}"

          try {
            // check if there were errors
            sh "mvn failsafe:verify ${mvnOpts}"
          } catch (buildError) {
            // set status to unstable, if errors found
            currentBuild.result = "UNSTABLE"
          }
        }
      } // dir

    } catch (caughtError) {
      err = caughtError
      currentBuild.result = "FAILURE"

    } finally {

      // collect coverage
      step([$class: 'JacocoPublisher'])

      // collect unit
      step([$class: 'JUnitResultArchiver', allowEmptyResults: true, testResults: '**/target/*-reports/TEST-*.xml'])

      cleanWs cleanWhenSuccess: false, cleanWhenUnstable: false

      /* Must re-throw exception to propagate error */
      if (err) {
        throw err
      }
    } // finally

  } // timestamps
}
