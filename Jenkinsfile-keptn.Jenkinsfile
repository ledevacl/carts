@Library('dynatrace@master')
@Library('keptn-library@3.2')
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
  }
  stages {
    
    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring:"${KEPTN_MONITORING}", shipyard:'keptn/carts-shipyard.yaml'
        }
      }
    }
  }// end stages
} // end pipeline
