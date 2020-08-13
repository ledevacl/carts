/***************************\
  This function assumes we run on a standard Jenkins Agent.

  Returns either 0(=no errors), 1(=pushing event failed)
\***************************/
@NonCPS
def call( Map args ) {
  // check input arguments
  String dtTenantUrl = args.containsKey("dtTenantUrl") ? args.dtTenantUrl : "${DT_TENANT_URL}"
  String dtApiToken = args.containsKey("dtApiToken") ? args.dtApiToken : "${DT_API_TOKEN}"
  def tagRule = args.containsKey("tagRule") ? args.tagRule : ""
  String source = args.containsKey("source") ? args.source : "Jenkins"

  String description = args.containsKey("description") ? args.description : ""
  String title = args.containsKey("title") ? args.title : ""

  def customProperties = args.containsKey("customProperties") ? args.customProperties : [ ]

  // check minimum required params
  if(tagRule == "" ) {
    echo "tagRule is a mandatory parameter!"
    return 1
  }

  String eventType = "CUSTOM_INFO"

  def postBody = [
    eventType: eventType,
    attachRules: [tagRule: tagRule],
    description: description,
    title: title,
    customProperties: customProperties,
    source: source,
    tags: tagRule[0].tags
  ]

def postCustomInfoEvent = httpRequest contentType: 'APPLICATION_JSON', 
    customHeaders: [[maskValue: true, name: 'Authorization', value: "Api-Token ${dtApiToken}"]], 
    httpMode: 'POST',
    requestBody: postBody,
    url: "${dtTenantUrl }/api/v1/events",
    validResponseCodes: "100:404",
    ignoreSslErrors: true

    if (postCustomInfoEvent.status == 200) {
        echo "Pushed custom info event to dynatrace: ${project}"
    } else {
        echo "Couldn't push custom info event to dynatrace " + createProjectResponse.content          
    }


  return 0
}