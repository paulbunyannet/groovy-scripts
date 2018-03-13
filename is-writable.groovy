def version = '1.0'

/**
 * Check if folder is writable. Optionally make the folder if
 * it does not already exist.
 * @param string isWritableFolder the folder to check
 */
def isWritable(isWritableFolder) {
  // If the folder doesn't already exist, then create it
  if(File.notExists(isWritableFolder)) {
    writableFolder = new File(isWritableFolder)
    writableFolder.mkdirs()
  }

  if(File.notExists("isWritable.php")) {
    // get the writable script
    sh "curl --silent -k https://gitlab.paulbunyan.net/snippets/9/raw > isWritable.php"
  }
  // Set the ownership of that folder to the www-data user
  sh "docker-compose exec -T code bash -c \"chown -R www-data:www-data ${isWritableFolder}\""
  // Set the permissions for the www-data user user
  sh "docker-compose exec -T code bash -c \"chmod 755 -fR ${isWritableFolder}\""
  // do a check that the www-data user can write to this folder now.
  sh "docker-compose exec -T code bash -c \"php isWritable.php --dir=${isWritableFolder}\""
}

return this;
