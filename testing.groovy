#!/usr/bin/groovy

/**
 * Prep and run tests with codeception
 * @param params
 * Params keys:
 * baseString 	-> The folder to run tests in
 * container 	-> The container to run the tests in
 * permissions	-> Permissions to use when making items writable
 * upstreamUser -> The name of the upstream user, used to check whether we should be running tests verbosely or not
 * comments 	-> instance of comments.groovy
 * isWritable 	-> instance of is-writable.groovy
 * gitlab 		-> instance of gitlab.groovy
 * @return
 */
//String basePath = env.WORKSPACE, String container = "code", String permissions = "www-data:www-data", String upstreamUser = "Web Development"
def runCodeceptionTests(Object params) {
	try {
		String container 	= params.container
		String basePath 	= params.basePath
		String permissions 	= params.permissions
		String upstreamUser = params.upstreamUser

		File tempDir = new File("${basePath}/tmp")
		if (!tempDir.exists()) {
			tempDir.mkdirs()
		}
		String groovies = "gitlab.groovy,is-writable.groovy,comments.groovy"
		def getGroovies = groovies.split(',')
		getGroovies.each {
			sh "curl --silent -k https://raw.githubusercontent.com/paulbunyannet/groovy-scripts/master/${it} > ${tempDir.toString()}/${it}"
			evaluate(new File("${tempDir.toString()}/${it}"))
		}

	} catch (intError) {
		return error(intError.getMessage())
	}

	commentBlock("Starting Tests", "Running tests in the ${container} container!" as String)

	try {
		//migrate before running anything
		// if artisan exists then run migrations
		File artisan = new File("${basePath}/artisan")
		if (artisan.exists() && artisan.isFile()) {
			commentBlock("Migrations", "Running migrations with Artisan")
			sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"php artisan migrate\""
		}

		// If a generic migration script exists then try and run it
		File genericMigrate = new File("${basePath}/migrate.php")
		if (!artisan.exists() && genericMigrate.exists() && genericMigrate.isFile()) {
			commentBlock("Migrations", "Running the generic migration runner ${genericMigrate.toString()}")
			sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"php migrate.php\""
		}
		String writable = "cache,storage,tests"
		def makeWritableFolders = writable.split(',')
		commentBlock("Writable", "Making the folders ${basePath}/{${writable}} writable for tests.")
		makeWritableFolders.each {
			isWritableInDocker(it, basePath, container, permissions)
		}
		dir(basePath) {
			File cThreeErrorLog = new File("${basePath}/c3_error.log")
			commentBlock("Remote Coverage Error Log", "Making sure the ${cThreeErrorLog.toString()} exists and is writable.")
			if (!cThreeErrorLog.exists()) {
				sh "echo \"#c3_error.log\" > c3_error.log"
			}
			sh "docker-compose exec -T ${container} bash -c \"chown ${permissions} c3_error.log \""
			sh "docker-compose exec -T ${container} bash -c \"chmod 777 -f c3_error.log\""
			comments.commentBlock("Codeception Config", "Clearing the current Codeception build and checking the configuration.")
			sh "docker-compose exec -T ${container} bash -c \"codecept clean --no-interaction\""
			sh "docker-compose exec -T ${container} bash -c \"codecept build --no-interaction\""
			sh "docker-compose exec -T ${container} bash -c \"codecept config:validate -q --no-interaction\""
		}
	} catch (setupError) {
		return error(setupError.getMessage())
	}

	// if there's a "failed" test file in the previous build then try and run that first to see if we fixed what was broken
//                        File previousFailed = new File("${Globals.WORKSPACE_ROOT}/${SCM_REPO.toUpperCase()}/${SCM_REPO.toLowerCase()}_${currentBuild.previousBuild.number}/failed")
//                        if (previousFailed.exists()) {
//                            sh "cp ${previousFailed.toString()} ${env.WORKSPACE}/tests/_output"
//                            sh "docker-compose exec -T code bash -c \"chown ${Globals.WWWDATA} tests/_output/failed\""
//                            sh "docker-compose exec -T code bash -c \"chmod 777 -f tests/_output/failed\""
//                            sh "docker-compose exec -T code bash -c \"codecept run -g failed --fail-fast --steps\""
//                        }

	try {
		dir(basePath) {
			String flags
			if (isMainRepoAndMaster(upstreamUser)) {
				flags = "--fail-fast --coverage --coverage-html --coverage-xml"
				commentBlock("Codeception tests", "Running Codeception tests as \"${upstreamUser}\" with flags \"${flags}\"")
				sh "docker-compose exec -T ${container} bash -c \"vendor/bin/codecept run ${flags}\""
			} else {
				flags = "--fail-fast --coverage --coverage-html --coverage-xml --steps"
				commentBlock("Codeception tests", "Running Codeception tests with flags \"${flags}\"")
				sh "docker-compose exec -T ${container} bash -c \"vendor/bin/codecept run ${flags}\""
			}
		}
	} catch (testError) {
		return error(testError.getMessage())
	}
}

return this
