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
    APP_NAME = "carts"
    KEPTN_PROJECT = "sockshop-qg"
    KEPTN_SERVICE = "carts"
    KEPTN_STAGE = "hardening"
    JMETER_VUCOUNT = 1
    JMETER_LOOPCOUNT = 4000
  }
  stages {
    
    stage('Keptn Init') {
      steps{
        script {
          keptn.keptnInit project:"sockshop-qg", service:"carts", stage:"hardening", monitoring: "dynatrace", shipyard:'../keptn/carts-shipyard.yaml'
        }
      }
    }
  }// end stages
} // end pipeline