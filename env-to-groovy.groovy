
def version = '1.0'

def envToGroovy(original, output) {

  // the origina file that will be used a base for the groovy script output
  File originalFile = new File(original)
  // the outputed groovy script path
  File outputFile = new File(output)
  // absolute path to the output file
  String absoluteOutputPath = outputFile.getAbsolutePath();
  // path relative the the output file
  String thisPath = absoluteOutputPath.substring(0,absoluteOutputPath.lastIndexOf(File.separator));
  // processing script file name
  String scriptName="envToGroovy.php"
  // download path of the processing script
  String scriptPath="https://gitlab.paulbunyan.net/snippets/2/raw"

  // get the script
  sh "curl -k ${scriptPath} --output ${thisPath}/${scriptName}"
  // run the script to cleanup the env for output
  sh "php ${thisPath}/${scriptName} ${originalFile.toString()} ${outputFile.toString()}"

  // load the env groovy file and remove it afterwards
  load output
  //echo "${outputFile.toString()} is loaded, the APP_ENV is set to \"${APP_ENV}\""
  // now remove the temp groovy file
  sh "rm -f ${output}"
  // remove the script, it's no longer needed
  sh "rm -f ${thisPath}/${scriptName}"

}

return this;
