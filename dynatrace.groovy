//package dynatrace
import groovy.json.*

def dynatracePushCustomInfoEvent(Map args) {
    // check input arguments
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
    def tagRule = args.containsKey("tagRule") ? args.tagRule : [ ]
    String source = args.containsKey("source") ? args.source : "Jenkins"
    String description = args.containsKey("description") ? args.description : ""
    String title = args.containsKey("title") ? args.title : ""
    def customProperties = args.containsKey("customProperties") ? args.customProperties : [ ]

    // check minimum required params
    if ((dtTenantUrl == "") || (dtApiToken == "")) {
        echo "dynatracePushCustomInfoEvent requires dynatrace tenant URL/API token!"
        return false;
    }

    if ((description == "") || (source == "")) {
        echo "dynatracePushCustomInfoEvent requires source and description to be set. These values cant be empty!"
        return false;
    }

    String eventType = "CUSTOM_INFO"

    def createEventBody = new JsonBuilder()
    
    createEventBody (
        eventType: eventType,
        attachRules: [tagRule: tagRule],
        description: description,
        title: title,
        customProperties: customProperties,
        source: source,
    )

    //def postBody = new JsonOutput().prettyPrint(createEventBody.toString())

    postBody = new JsonOutput().toJson(createEventBody.content)

    echo postBody

    def createEventResponse = httpRequest contentType: 'APPLICATION_JSON', 
        customHeaders: [[maskValue: true, name: 'Api-Token ', value: "${dtApiToken}"]], 
        httpMode: 'POST',
        requestBody: postBody,
        responseHandle: 'STRING',
        url: "${dtTenantUrl}/api/v1/events",
        validResponseCodes: "200",
        ignoreSslErrors: true

        if (createEventResponse.status == 200) {
            echo "Custom info event posted successfully!"
        } else {
            echo "Failed To post event:" + createEventResponse.content
        }

    return true

}

def dynatracePushDeployEvent(Map args) {
    String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
    String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
    def tagRule = args.containsKey("tagRule") ? args.tagRule : [ ]
    String source = args.containsKey("source") ? args.source : "Jenkins"
    String deploymentName = args.containsKey("deploymentName") ? args.deploymentName : "${env.JOB_NAME}"
    String deploymentVersion = args.containsKey("deploymentVersion") ? args.deploymentVersion : "${env.VERSION}"
    String deploymentProject = args.containsKey("deploymentProject") ? args.deploymentProject : ""
    String ciBackLink = args.containsKey("ciBackLink") ? args.ciBackLink : "${env.BUILD_URL}"
    String remediationAction = args.containsKey("remediationAction") ? args.remediationAction : "null"
    def customProperties = args.containsKey("customProperties") ? args.customProperties : [ ]

    if ((dtTenantUrl == "") || (dtApiToken == "")) {
        echo "Missing dynatrace tenant URL/API token!"
        return false;
    }

    if ((source == "") || (deploymentName == "") || (deploymentVersion == "")) {
        echo "dynatracePushDeployEvent requires source, deploymentName and deploymentVersion to be set. These values cant be empty!"
        return false;
    }

    String eventType = "CUSTOM_DEPLOYMENT"

    def createEventBody = """{
        "eventType": "${eventType},
        "attachRules": {
            "tagRule": "${tagRule}"
            },
        "deploymentName": "${deploymentName}",
        "deploymentVersion": "${deploymentVersion}",
        "deploymentProject": "${deploymentProject}",
        "ciBackLink": "${ciBackLink}",
        "remediationAction": "${remediationAction}",
        "source": "${source}"
        "customProperties": "${customProperties}"
    }"""


    def createEventResponse = httpRequest contentType: 'APPLICATION_JSON', 
        customHeaders: [[maskValue: true, name: 'Api-Token ', value: "${dtApiToken}"]], 
        httpMode: 'POST',
        requestBody: createEventBody,
        responseHandle: 'STRING',
        url: "${dtTenantUrl}/api/v1/events",
        validResponseCodes: "200",
        ignoreSslErrors: true

        if (createEventResponse.status == 200) {
            echo "Deployment event posted successfully!"
        } else {
            echo "Failed To post event:" + createEventResponse.content          
        }
    return true

}

return this