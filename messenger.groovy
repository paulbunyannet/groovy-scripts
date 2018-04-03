def version = '1.21'
def _send(String d_message, String web_url="", String my_messages="", String icon="") {
    try{
        def my_icon = "https://emojipedia-us.s3.amazonaws.com/thumbs/120/apple/118/skull-and-crossbones_2620.png"
        def my_color = "#ff0000"
        def my_message = "Nothing was passed to mattermost.sendMessage function"
        def server_account = "jenkinstrial@w2-dev.pbndev.net"
        def deploy = "false"

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
            my_message = "JOB FAILED! (<https://jenkinstrial.pbndev.net/from_jenkins_failed/${gitlabSourceRepoName}/${Globals.ARCHIVE_ZIP}| Download the test output folder ${Globals.ARCHIVE_ZIP}.zip >)"
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
        if(deploy == "true"){
            dir("${env.WORKSPACE}") {
                withCredentials([sshUserPrivateKey(credentialsId: "646ef7ce-60bc-4480-9324-09c8928bf457", keyFileVariable: 'key', passphraseVariable: '', usernameVariable: 'username')]) {
                    String TO_WO_FILE = "/home/jenkinstrial/public_html/from_jenkins_failed/${gitlabSourceRepoName}"
                    String TO_WITH_FILE = "${TO_WO_FILE}/${Globals.ARCHIVE_ZIP}"
                    String FROM_WITH_FILE = "${env.WORKSPACE}/${Globals.ARCHIVE_ZIP}"
                    echo "sending zip to jenkinstrial"
                    try{
                        echo "inside of making dir "
                        sh "cd ${env.WORKSPACE}; ssh -i '${key}' -p 85 ${server_account} 'mkdir -p ${TO_WO_FILE}'";
                        echo "inside of making dir "
                    }catch(e){
                        "the folder already exists, moving on... pushing the file"
                    }
                    echo "inside of push files dir "
                    sh "cd ${env.WORKSPACE}; scp -i '${key}' -P 85 ${FROM_WITH_FILE} ${server_account}:${TO_WITH_FILE}";
                    echo "inside of push files dir "
                }
            }
        }
        mattermostSend channel: "#jenkins", prext: "@all", icon: "${my_icon}", color: "${my_color}", message: "**${my_message}** **_${env.JOB_NAME}_** build # ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open build>)(<${env.BUILD_URL}/consoleFull|Open Console>)(<${env.BUILD_URL}/consoleText|Open Console as Text>)"
    }
    catch(ooops){
        echo "found it.... ${ooops.getMessage()}"
    }
}

/*
* startMessage
* Output a formatted start message message
* @prop String stage
* @prop String message
* @prop mixed timestamp
*/
def _start(String stage, String d_message = "", timestamp = Globals.DATE_JOB_STARTED) {
    if(timestamp == Globals.DATE_JOB_STARTED){
        timestamp = "${BUILD_TIMESTAMP}"
    }
    echo "\n${Globals.DIVIDER}${Globals.DIVIDER}"
    println "\u2605 Started ${stage} ${d_message} at ${timestamp} \u2605"
    echo "${Globals.DIVIDER}"
}

/* Output a formatted success message */
def _success(String stage, String d_message = "", timestamp = Globals.DATE_JOB_STARTED) {
    if(timestamp == Globals.DATE_JOB_STARTED){
        timestamp = "${BUILD_TIMESTAMP}"
    }
    if("${Globals.ERROR_EXISTED}"=="true"){
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "There is a warning as the stage ${stage} completed successfully but a previous step didn't";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        _error(stage, d_message, timestamp)
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
        echo "------------------------------------------------------------------------------------------";
    }else{
        echo "\n${Globals.DIVIDER}${Globals.DIVIDER}"
        println "\u2713 ${stage} completed ${d_message} at ${timestamp} \u263A!"
        echo "${Globals.DIVIDER}"
    }
}

/* Output a formatted warning message */
def _warning(String stage, String d_message = "", timestamp = Globals.DATE_JOB_STARTED) {
    if(timestamp == Globals.DATE_JOB_STARTED){
        timestamp = "${BUILD_TIMESTAMP}"
    }
    echo "\n${Globals.DIVIDER}${Globals.DIVIDER}"
    println "\u2297 ${stage} warning: ${d_message} at ${timestamp} \u1F631!"
    echo "${Globals.DIVIDER}"
}

/* output a formatted error message */
def _error(String stage, String d_message = "", timestamp = Globals.DATE_JOB_STARTED) {
    if(timestamp == Globals.DATE_JOB_STARTED){
        timestamp = "${BUILD_TIMESTAMP}"
    }
    echo "\n${Globals.DIVIDER}${Globals.DIVIDER}"
    println "\u2297 ${stage} failed: ${d_message} \u1F631!"
    echo "${Globals.DIVIDER}"
    if("${Globals.ERROR_EXISTED}"!="true"){
        Globals.ERROR_EXISTED="true"
        Globals.ERROR_EXISTED_stage=stage
        Globals.ERROR_EXISTED_message=d_message
        Globals.ERROR_EXISTED_timestamp=timestamp
        failTheJob()
    }else{
        return error("${Globals.ERROR_EXISTED_stage} failed: ${Globals.ERROR_EXISTED_message} on ${Globals.ERROR_EXISTED_timestamp}")
    }
}
return this
