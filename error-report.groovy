
import java.text.SimpleDateFormat
def version = '1.0'

def errorReport(String STAGE = null, String[] LOGS = (String[]) [], String RECIPIENT = null, String OWNER = null, String REPO = null, String BRANCH= "master", String WORKSPACE = env.WORKSPACE.toString(), Integer TAIL = 1000 ) {

  def ERROR_DATE = new Date()
  def ERROR_DATE_FORMAT_HUMAN = new SimpleDateFormat("EEEE',' MMMM dd',' YYYY 'at' HH:mm:ss z")
  def ERROR_DATE_FORMAT_LOGS = new SimpleDateFormat("yyyy-MM-dd")
  def ERROR_FORMAT_STAMP = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  def ERROR_ARCHIVE_NAME = "${OWNER}-${REPO}-${BRANCH}"

  if (LOGS.size() > 0) {
    for (int i=0; i > LOGS.size(); i++) {
      sh "cp ${LOGS[i]} ${WORKSPACE}/tests/_output"
    }
  }

  // copy over the CodeIgniter log file
  sh "cp ${WORKSPACE}/ci/application/logs/log-${ERROR_DATE_FORMAT_LOGS.format(ERROR_DATE)}.php ${WORKSPACE}/tests/_output"
  // get the log outputfrom the code container
  sh "echo \"\$(docker-compose logs --tail ${TAIL} --timestamps code)\" | dd of=${WORKSPACE}/tests/_output/docker-code-${ERROR_DATE_FORMAT_LOGS.format(ERROR_DATE)}.log"
  // get the log outputfrom the web container
  sh "echo \"\$(docker-compose logs --tail ${TAIL} --timestamps web)\" | dd of=${env.WORKSPACE}/tests/_output/docker-web-${ERROR_DATE_FORMAT_LOGS.format(ERROR_DATE)}.log"
  // get the log outputfrom the hub container
  sh "echo \"\$(docker-compose logs --tail ${TAIL} --timestamps hub)\" | dd of=${env.WORKSPACE}/tests/_output/docker-hub-${ERROR_DATE_FORMAT_LOGS.format(ERROR_DATE)}.log"
  // compress the test output to send
  zip dir: 'tests/_output', glob: '', zipFile: "${ERROR_ARCHIVE_NAME}-test-output.zip"
  // email the output
  emailext attachmentsPattern: "${ERROR_ARCHIVE_NAME}-test-output.zip", body: "Stage \"${STAGE}\" failed on ${OWNER}/${REPO}:${BRANCH}.\r Stage \"${STAGE}\" was run on ${ERROR_DATE_FORMAT_HUMAN.format(ERROR_DATE)}", subject: "Stage \"${STAGE}\" failed on ${OWNER}/${REPO}:${BRANCH}", to: "${RECIPIENT}"
  //
  echo "Error log ${ERROR_ARCHIVE_NAME}-test-output.zip was emailed to ${RECIPIENT} on ${ERROR_DATE_FORMAT_HUMAN.format(ERROR_DATE)}."
}

return this;
