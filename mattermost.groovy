#!groovy
def _send(String d_message, String web_url="", String my_messages="", String icon="", Globals=[], env=[], gitlabSourceRepoName="") {
    String my_icon = "https://emojipedia-us.s3.amazonaws.com/thumbs/120/apple/118/skull-and-crossbones_2620.png"
    String my_color = "#ff0000"
    String my_message = "Nothing was passed to mattermost.sendMessage function"
    String server_account = "jenkinstrial@w2-dev.pbndev.net"
    Boolean deploy = false
    if( d_message == "OPTIONAL"){
        my_icon = "${icon}"
        my_color = "#2aff00"
        my_message = "url: <${web_url}|${web_url}> message: ${my_messages}"
    }
    if( d_message == "SUCCESS"){
        deploy = true;
        my_icon = "https://www.freeiconspng.com/uploads/success-icon-11.png";
        my_color = "#2aff00"
        my_message = "SUCCESS!"
    }
    if( d_message == "FAILED"){
        deploy = true;
        my_icon = "https://emojipedia-us.s3.amazonaws.com/thumbs/60/apple/114/shocked-face-with-exploding-head_1f92f.png"
        my_color = "#ff0000"
        my_message = "JOB FAILED! (<https://jenkinstrial.pbndev.net/from_jenkins_failed/${gitlabSourceRepoName}/${Globals.ARCHIVE_ZIP}| Download the test output folder ${Globals.ARCHIVE_ZIP} >)"
    }
    if( d_message == "MERGE"){
        my_icon = "https://cdn4.iconfinder.com/data/icons/socialcones/508/Gitlab-256.png";
        my_color = "#ff8c00"
        my_message = "A <${web_url}|Merge Request> Is Waiting For Your Approval!!"
    }
    if( d_message == "SPACE"){
        my_icon = "https://findicons.com/files/icons/1620/crystal_project/128/hdd_mount.png"
        my_color = "#c842f4"
        my_message = "DISK SPACE WARNING! ${my_messages}"
    }
    def sent
    sent = [
            icon: "${my_icon}",
            color: "${my_color}",
            message: "${my_message}",
            server: "${server_account}",
            deploy: deploy
    ]
    return sent
}
return this