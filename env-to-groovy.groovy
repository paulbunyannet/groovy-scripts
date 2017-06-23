
def version = '1.0'

def envToGroovy(original, output) {
  sh "cp ${original} ${output}"
  sh "chmod 777 ${output}"

  // remove all comment lines
  // https://stackoverflow.com/questions/8206280/delete-all-lines-beginning-with-a-from-a-file#comment41301697_8206295
  sh "sed -i '/^#/d' ${output}"
  // add quote to end of all lines
  // https://stackoverflow.com/a/2869736/405758
  sh "sed -i -e 's/\$/\"/' ${output}"
  // add quote after the equal sign
  //https://unix.stackexchange.com/a/159369
  sh "sed -i -e 's/=/=\"/g' ${output}"
  // remove double quotes
  sh "sed -i -e 's/\"\"/\"/g' ${output}"
  // add return at the end of the file
  sh "echo \"\nreturn this;\" >> ${output}"

  // load the env groovy file and remove it afterwards
  load output
  fileOperations([fileDeleteOperation(excludes: '', includes: output)])

}
