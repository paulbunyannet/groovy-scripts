import java.io.File

/**
 * Check if folder is writable inside docker.
 * @param String isWritableFolder the folder to check
 * @param String basePath the folder to work in (workspace)
 * @param String container container to run the command in
 */
def isWritableInDocker(String folder, String basePath, String container, String user="www-data:www-data") {

    echo "Running the isWritable method with folder ${basePath}/${folder}"

    // make folders if it does not exist
    File makeFolder = new File("${basePath}/${folder}")
    if (!makeFolder.exists()) {
        echo "${makeFolder.toString()} does not exist and will be created."
        if(makeFolder.mkdirs()) {
            echo "${makeFolder.toString()} was created."
        } else {
            return error("${makeFolder.toString()} could not be created.")
        }
    }

    // Set the ownership of that folder to the www-data user
    sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"chown -R ${user} ${folder}\""

    // Set the permissions for the www-data user user
    sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"chmod 755 -fR ${folder}\""

    File isWritable = new File("${basePath}/isWritable.php")
    if (!isWritable.exists()) {
        // get the writable script
        echo "Getting the permissions checking script"
        sh "curl --silent -k https://gitlab.paulbunyan.net/snippets/9/raw > ${basePath}/isWritable.php"
    }
    // do a check that the www-data user can write to this folder now.
    sh "cd ${env.WORKSPACE}; docker-compose exec -T ${container} bash -c \"php isWritable.php --dir=${folder}\""

}

return this
