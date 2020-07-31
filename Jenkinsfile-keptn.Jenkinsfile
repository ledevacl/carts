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
    KEPTN_STAGE = "preprod"
    KEPTN_MONITORING = "dynatrace"
    KEPTN_SHIPYARD = "keptn/carts-shipyard.yaml"
  }
  stages {
    
    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring: "{KEPTN_MONITORING}", shipyard:'${KEPTN_SHIPYARD}'
        }
      }
    }
  }// end stages
} // end pipeline