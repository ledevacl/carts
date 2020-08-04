@Library('dynatrace@master')
@Library('keptn-library@master')
import sh.keptn.Keptn
def keptn = new sh.keptn.Keptn()
import groovy.json.JsonSlurper
import groovy.json.JsonSlurperClassic


pipeline {
  agent {
    label 'kubegit'
  }
  environment {
    KEPTN_PROJECT = "pantry"
    KEPTN_SERVICE = "stockout"
    KEPTN_STAGE = "preproduction"
    KEPTN_MONITORING = "dynatrace"
    KEPTN_SHIPYARD = "keptn/carts-shipyard.yaml"
  }
  stages {
    
    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring:"${KEPTN_MONITORING}", shipyard: "${KEPTN_SHIPYARD}"
          keptn.keptnAddResources('keptn/carts-sli.yaml','dynatrace/sli.yaml')
          keptn.keptnAddResources('keptn/carts-slo.yaml','slo.yaml')
          keptn.keptnAddResources('keptn/dynatrace.conf.yaml','dynatrace/dynatrace.conf.yaml')          
        }
      }
    }
    stage('mark start') {
      steps {
        script {
            env.testStartTime = get_timestamp()
            keptn.markEvaluationStartTime
        }
      }
    }

    stage('Run Performance Test') {
      steps {
        script{
          build job: 'carts.performance', wait:true
        }
        script{
          def keptnContext = keptn.sendStartEvaluationEvent starttime:"", endtime:"" 
          echo "Open Keptns Bridge: ${keptn_bridge}/trace/${keptnContext}"
        }
      }
    }

  } // end stages
} // end pipeline
def get_timestamp(){
    DATE_TAG = java.time.LocalDate.now()
    DATETIME_TAG = java.time.LocalDateTime.now()
    echo "${DATETIME_TAG}"
                
    return DATETIME_TAG
}