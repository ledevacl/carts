
//@Library('dynatrace@master')
//@Library('keptn-library@master')
//import sh.keptn.Keptn
//def keptn = new sh.keptn.Keptn()
def keptn
def dynatrace_custom_info

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
    stage('Load keptn/dt libraries') {
      steps{
        script {
          keptn = load("Keptn.groovy")
          dynatrace_custom_info = load("dt_pushDynatraceInfoEvent.groovy")
        }
      }
    } 

    stage('Keptn Init') {
      steps{
        script {
          //def keptn = load("Keptn.groovy")
          keptn.keptnInit project:"${KEPTN_PROJECT}", service:"${KEPTN_SERVICE}", stage:"${KEPTN_STAGE}", monitoring:"${KEPTN_MONITORING}", shipyard: "${KEPTN_SHIPYARD}"
          keptn.keptnAddResources("${KEPTN_SLI}",'dynatrace/sli.yaml')
          keptn.keptnAddResources("${KEPTN_SLO}",'slo.yaml')
          keptn.keptnAddResources("${KEPTN_DT_CONF}",'dynatrace/dynatrace.conf.yaml')          
        }
      }
    }

    stage('Send Test Start to DT') {
      steps {
        container("curl") {
          script {
      
              def tagMatchRules = [[ meTypes: [[meType: 'SERVICE']],
                                        tags : [[context: 'CONTEXTLESS', key: 'keptn_project', value: KEPTN_PROJECT],
                                                [context: 'CONTEXTLESS', key: 'keptn_service', value: KEPTN_SERVICE],
                                                [context: 'CONTEXTLESS', key: 'keptn_stage', value: KEPTN_STAGE]]
                                  ]];
              // Push some Jenkins OOTB info to DT.
              def customProps = [ 
                  "Test Type": "Load",
                  "Test Provider": "Jmeter",
                  "Test Parameters": "[vuCount: ${env.JMETER_VUCOUNT}] [loopCount: ${env.JMETER_LOOPCOUNT}]"
              ];
              
              def notification = new dynatrace_custom_info.pushDynatraceInfoEvent();
              notification.call(title: "Test Start on ${env.KEPTN_PROJECT}/${env.KEPTN_SERVICE}", source: 'Jenkins', description: 'Starting load test.', tagRule: tagMatchRules, customProperties: customProps);
          }
        }
      } //end steps
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