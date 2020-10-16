def keptn
def dynatrace

node('kubegit'){
    JMETER_VUCOUNT = 1
    JMETER_LOOPCOUNT = 5

    tagMatchRules = [
    [ meTypes: ['SERVICE'],
        tags : [[context: 'ENVIRONMENT', key: 'product', value: KEPTN_PROJECT],
                [context: 'CONTEXTLESS', key: 'app', value: KEPTN_SERVICE],
                [context: 'CONTEXTLESS', key: 'environment', value: KEPTN_STAGE]]
    ]];

    stage ('Checkout') {
        println '**********************************************************************'
        checkout scm
    }

    stage('Load keptn/dynatrace libraries') {
        keptn = load("Keptn.groovy")
        dynatrace = load("dynatrace.groovy")
    }

    stage ('Keptn Init') {
        KEPTN_SHIPYARD = "${params.KEPTN_DIR}${params.KEPTN_SERVICE}-shipyard.yaml"
        KEPTN_SLI = "${params.KEPTN_DIR}${params.KEPTN_SERVICE}-sli.yaml"
        KEPTN_SLO = "${params.KEPTN_DIR}${params.KEPTN_SERVICE}-slo.yaml"
        KEPTN_DT_CONF = "${params.KEPTN_DIR}dynatrace.conf.yaml"
        keptn.keptnInit keptn_endpoint:"${KEPTN_ENDPOINT}", keptn_api_token:"${KEPTN_API_TOKEN}", project:"${params.KEPTN_PROJECT}", service:"${params.KEPTN_SERVICE}", stage:"${params.KEPTN_STAGE}", monitoring:"${params.KEPTN_MONITORING}", shipyard: "${KEPTN_SHIPYARD}"
        keptn.keptnAddResources("${KEPTN_SLI}",'dynatrace/sli.yaml')
        keptn.keptnAddResources("${KEPTN_SLO}",'slo.yaml')
        keptn.keptnAddResources("${KEPTN_DT_CONF}",'dynatrace/dynatrace.conf.yaml')
    }

    stage ('Send Deployment event to DT') {
        dynatrace.dynatracePushDeploymentEvent (
            tagRule: tagMatchRules, 
            deploymentVersion: "${env.BRANCH_NAME}",
            deploymentProject: "${KEPTN_PROJECT}",
            customProperties: [
                "Jenkins Build Number": "${env.BUILD_NUMBER}",
                    "Git commit": "${env.GIT_COMMIT}",
                    "Last commit by": "${env.GIT_COMMITTER_NAME}",
                    "Git Branch": "${env.GIT_BRANCH}",
                    "SCM": "${env.GIT_URL}"
            ]
        )
    }

    stage ('Run Warmup tests') {
        if (params.RUN_LOADTEST){
            //build job: "${params.WARMUP_TEST_JOB}", wait:true
            echo 'Running warmup tests'
            sleep 10
        }
        else{
            echo 'Not running warmup tests'
            sleep 10
        }
    }

    stage('Send Test Start to DT') {
        dynatrace.dynatracePushCustomInfoEvent (
            title: "Test Stop on ${KEPTN_PROJECT}/${KEPTN_SERVICE}", 
            source: 'Jenkins', 
            description: "Starting load test.", 
            tagRule: tagMatchRules, 
            customProperties: [
                    "Test Type": "Load",
                    "Test Provider": "Jmeter",
                    "Test Parameters": "[vuCount: ${JMETER_VUCOUNT}] [loopCount: ${JMETER_LOOPCOUNT}]"
            ]
        )
    }

    stage ('Run Performance Test') {
        //keptn.markEvaluationStartTime()
        def startlatestdate = new Date()
        def tz = TimeZone.getTimeZone('UTC')
        def startepochtime = startlatestdate.format("yyyy-MM-dd'T'HH:mm:ss.SSS", tz)
        if (params.RUN_LOADTEST){
            build job: "${params.LOAD_TEST_JOB}", wait:true
        }
        else{
            echo 'Not running load tests'
            sleep 20
        }
        def endlatestdate = new Date()
        def endepochtime = endlatestdate.format("yyyy-MM-dd'T'HH:mm:ss.SSS", tz)
        def keptnContext = keptn.sendStartEvaluationEvent starttime:"${startepochtime}", endtime:"${endepochtime}"
        //def keptnContext = keptn.sendStartEvaluationEvent starttime:"", endtime:""
        echo "Open Keptns Bridge: ${keptn_bridge}/trace/${keptnContext}"
    }

    stage('Send Test Stop to DT') {
        dynatrace.dynatracePushCustomInfoEvent (
            title: "Test Start on ${KEPTN_PROJECT}/${KEPTN_SERVICE}", 
            source: "Jenkins", 
            description: "Stopping load test.", 
            tagRule: tagMatchRules, 
            customProperties: [
                    "Test Type": "Load",
                    "Test Provider": "Jmeter",
                    "Test Parameters": "[vuCount: ${JMETER_VUCOUNT}] [loopCount: ${JMETER_LOOPCOUNT}]"
            ]
        )
    } 

    stage('Pipeline Quality Gate') {
        def result = keptn.waitForEvaluationDoneEvent setBuildResult:true, waitTime:'3'
        echo "EVALUATION RESULT: ${result}"
    }

} // END PIPELINE
