
//@Library('dynatrace@master')
//@Library('keptn-library@master')
//import sh.keptn.Keptn
//def keptn = new sh.keptn.Keptn()
def keptn_lib
keptn_lib = load 'Keptn.groovy'
def keptn = new keptn_lib.sh.keptn.Keptn()

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
  }
  stages {
    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring:"${KEPTN_MONITORING}", shipyard: "${KEPTN_SHIPYARD}"
          keptn.keptnAddResources("${KEPTN_SLI}",'dynatrace/sli.yaml')
          keptn.keptnAddResources("${KEPTN_SLO}",'slo.yaml')
          keptn.keptnAddResources("${KEPTN_DT_CONF}",'dynatrace/dynatrace.conf.yaml')          
        }
      }
    }

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
    }

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