@Library('dynatrace@master')
@Library('keptn-library@master')
import sh.keptn.Keptn
def keptn = new sh.keptn.Keptn()

pipeline {
  agent {
    label 'kubegit'
  }
  parameters {
    string(name: 'KEPTN_PROJECT', defaultValue: 'sockshop', description: 'The name of the service to deploy.', trim: true)
    string(name: 'KEPTN_SERVICE', defaultValue: 'carts', description: 'The image of the service to deploy.', trim: true)
    string(name: 'KEPTN_STAGE', defaultValue: 'env', description: 'The version of the service to deploy.', trim: true)
    string(name: 'KEPTN_MONITORING', defaultValue: 'dynatrace', description: 'Custom properties to be supplied to Dynatrace.', trim: true)
    string(name: 'KEPTN_DIR', defaultValue: 'keptn/', description: 'keptn shipyard file location')
  }
  environment {
    KEPTN_SHIPYARD = "${KEPTN_PROJECT}${KEPTN_SERVICE}-shipyard.yaml"
    KEPTN_SLO = "${KEPTN_PROJECT}${KEPTN_SERVICE}-sli.yaml"
    KEPTN_SLI = "${KEPTN_PROJECT}${KEPTN_SERVICE}-slo.yaml"
    KEPTN_DT_CONF = "${KEPTN_PROJECT}${KEPTN_MONITORING}.conf.yaml"
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
          build job: 'carts.performance', wait:true
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