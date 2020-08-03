@Library('dynatrace@master')
@Library('keptn-library@master')
import sh.keptn.Keptn
def keptn = new sh.keptn.Keptn()

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

    stage('Run Performance Test') {
      steps {
        script {
            env.testStartTime = get_timestamp()
            keptn.markEvaluationStartTime
        }
        //script{
          build job: 'sockshop/carts.performance/master', wait:true
        //}
        script{
          env.testEndTime = get_timestamp()
        }
      }
    }

  } // end stages
} // end pipeline
