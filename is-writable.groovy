def version = '1.0'

/**
 * Check if folder is writable. Optionally make the folder if
 * it does not already exist.
 * @param string isWritableFolder the folder to check
 */
def isWritable(isWritableFolder) {

    // make folders if it does not exist
    if (File.notExists(isWritableFolder)) {
        sh "docker-compose exec -T code bash -c \"mkdir -p ${isWritableFolder}\""
    }

    // Set the ownership of that folder to the www-data user
    sh "docker-compose exec -T code bash -c \"chown -R www-data:www-data ${isWritableFolder}\""

    // Set the permissions for the www-data user user
    sh "docker-compose exec -T code bash -c \"chmod 755 -fR ${isWritableFolder}\""

    if (File.notExists("isWritable.php")) {
        // get the writable script
        echo "Getting the permissions checking script"
        sh "curl --silent -k https://gitlab.paulbunyan.net/snippets/9/raw > isWritable.php"
    }
    // do a check that the www-data user can write to this folder now.
    sh "docker-compose exec -T code bash -c \"php isWritable.php --dir=${isWritableFolder}\""
}

return this
