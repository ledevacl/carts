
//@Library('dynatrace@master')
//@Library('keptn-library@master')
//import sh.keptn.Keptn
//def keptn = new sh.keptn.Keptn()
def keptn
def dynatrace

def tagMatchRules = [
  [ meTypes: ['SERVICE'],
      tags : [[context: 'CONTEXTLESS', key: 'keptn_project', value: KEPTN_PROJECT],
              [context: 'CONTEXTLESS', key: 'keptn_service', value: KEPTN_SERVICE],
              [context: 'CONTEXTLESS', key: 'keptn_stage', value: KEPTN_STAGE]]
  ]];

pipeline {
  agent {
    label 'kubegit'
  }
  parameters {
    string(name: 'KEPTN_PROJECT', defaultValue: 'sockshop', description: 'The name of the application.', trim: true)
    string(name: 'KEPTN_SERVICE', defaultValue: 'carts', description: 'The name of the service', trim: true)
    string(name: 'KEPTN_STAGE', defaultValue: 'dev', description: 'The name of the environment.', trim: true)
    string(name: 'KEPTN_MONITORING', defaultValue: 'dynatrace', description: 'Name of monitoring provider.', trim: true)
    string(name: 'KEPTN_DIR', defaultValue: 'keptn/', description: 'keptn shipyard file location')
  }
  environment {
    KEPTN_SHIPYARD = "${KEPTN_DIR}${KEPTN_SERVICE}-shipyard.yaml"
    KEPTN_SLI = "${KEPTN_DIR}${KEPTN_SERVICE}-sli.yaml"
    KEPTN_SLO = "${KEPTN_DIR}${KEPTN_SERVICE}-slo.yaml"
    KEPTN_DT_CONF = "${KEPTN_DIR}${KEPTN_MONITORING}.conf.yaml"
    LOAD_TEST_JOB = "${KEPTN_SERVICE}.performance"
    JMETER_VUCOUNT = 1
    JMETER_LOOPCOUNT = 4000
  }

  stages {

    stage('Load keptn/dynatrace libraries') {
      steps{
        script {
          keptn = load("Keptn.groovy")
          dynatrace = load("dynatrace.groovy")
        }
      }
    } // end stage

    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring:"${KEPTN_MONITORING}", shipyard: "${KEPTN_SHIPYARD}"
          keptn.keptnAddResources("${KEPTN_SLI}",'dynatrace/sli.yaml')
          keptn.keptnAddResources("${KEPTN_SLO}",'slo.yaml')
          keptn.keptnAddResources("${KEPTN_DT_CONF}",'dynatrace/dynatrace.conf.yaml')          
        }
      }
    } // end stage

    stage('Send Deployment event to DT') {
      steps {
        script{
          dynatrace.dynatracePushDeploymentEvent (
            tagRule: tagMatchRules, 
            customProperties: [
                  "Jenkins Build Number": "${env.BUILD_ID}",
                  "Git commit": "${env.GIT_COMMIT}",
                  "Last commit by": "${env.GIT_COMMITTER_NAME}",
                  "Branch": "${env.GIT_BRANCH}",
                  "SCM": "${env.GIT_URL}"
            ]
          )
        }
      }
    } // end stage

    stage('Send Test Start to DT') {
      steps {
        script{
          dynatrace.dynatracePushCustomInfoEvent (
            title: "Test Start on ${env.KEPTN_PROJECT}/${env.KEPTN_SERVICE}", 
            source: 'Jenkins', 
            description: 'Starting load test.', 
            tagRule: tagMatchRules, 
            customProperties: [
                  "Test Type": "Load",
                  "Test Provider": "Jmeter",
                  "Test Parameters": "[vuCount: ${env.JMETER_VUCOUNT}] [loopCount: ${env.JMETER_LOOPCOUNT}]"
            ]
          )
        }
      }
    } // end stage

    stage('Run Performance Test') {
      steps {
        script {
            keptn.markEvaluationStartTime()
        }
        script{
          build job: "${LOAD_TEST_JOB}", wait:true
        }
        script{
          def keptnContext = keptn.sendStartEvaluationEvent starttime:"", endtime:"" 
          echo "Open Keptns Bridge: ${keptn_bridge}/trace/${keptnContext}"
        }
      }
    } // end stage

    stage('Send Test Stop to DT') {
      steps {
        script{
          dynatrace.dynatracePushCustomInfoEvent (
            title: "Test Start on ${env.KEPTN_PROJECT}/${env.KEPTN_SERVICE}", 
            source: 'Jenkins', 
            description: 'Stopping load test.', 
            tagRule: tagMatchRules, 
            customProperties: [
                  "Test Type": "Load",
                  "Test Provider": "Jmeter",
                  "Test Parameters": "[vuCount: ${env.JMETER_VUCOUNT}] [loopCount: ${env.JMETER_LOOPCOUNT}]"
            ]
          )
        }
      }
    } // end stage

    stage('Pipeline Quality Gate') {
      steps {
        script {
          def result = keptn.waitForEvaluationDoneEvent setBuildResult:true, waitTime:'3'
          echo "${result}"
        }
      }
    }// end stage

  } // end stages
} // end pipeline