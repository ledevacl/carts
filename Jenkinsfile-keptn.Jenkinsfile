@Library('keptn-library@1.0')
import sh.keptn.Keptn
def keptn = new sh.keptn.Keptn()

pipeline {
  agent {
    label 'kubegit'
  }
  environment {
    APP_NAME = "carts"
    KEPTN_PROJECT = "sockshop-qg"
    KEPTN_SERVICE = "carts"
    KEPTN_STAGE = "dev"
    JMETER_VUCOUNT = 1
    JMETER_LOOPCOUNT = 4000
  }
  stages {
    
    stage('Initialize keptn') {
      steps{
        script {
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"hardening", monitoring: "dynatrace", shipyard:'keptn/carts-shipyard.yaml'
        }
      }
    }
  }
}