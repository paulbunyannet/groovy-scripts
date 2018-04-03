#!/usr/bin/groovy
import java.io.File

def comments
def gitlab
def isWritable
def groovy
groovy = fileLoader.withGit('https://github.com/paulbunyannet/groovy-scripts.git', 'master', '', '') {
	comments = file.load('commentBlock')
	gitlab = fileLoader.load('gitlab')
	isWritable = fileLoader.load('is-writable')
}

def runCodeceptionTests(String basePath = env.WORKSPACE, String container = "code", String permissions = "www-data:www-data", String upstreamUser = "Web Development") {
	comments.commentBlock("Starting Tests", "Running tests in the ${container} container!" as String)

	try {
		//migrate before running anything
		// if artisan exists then run migrations
		File artisan = new File("${basePath}/artisan")
		if (artisan.exists() && artisan.isFile()) {
			comments.commentBlock("Migrations", "Runing migrations with Artisan")
			sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"php artisan migrate\""
		}

		// If a generic migration script exists then try and run it
		File genericMigrate = new File("${basePath}/migrate.php")
		if (!artisan.exists() && genericMigrate.exists() && genericMigrate.isFile()) {
			comments.commentBlock("Migrations", "Running the generic migration runner ${genericMigrate.toString()}")
			sh "cd ${basePath}; docker-compose exec -T ${container} bash -c \"php migrate.php\""
		}
		String writable = "cache,storage,tests"
		def makeWritableFolders = writable.split(',')
		comments.commentBlock("Writable", "Making the folders ${basePath}/{${writable}} writable for tests.")
		makeWritableFolders.each {
			isWritable.isWritableInDocker(it, basePath, container, permissions)
		}
		dir(basePath) {
			File cThreeErrorLog = new File("${basePath}/c3_error.log")
			comments.commentBlock("Remote Coverage Error Log", "Making sure the ${cThreeErrorLog.toString()} exists and is writable.")
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
			if (gitlab.isMainRepoAndMaster(upstreamUser)) {
				flags = "--fail-fast --coverage --coverage-html --coverage-xml"
				comments.commentBlock("Codeception tests", "Running Codeception tests as \"${upstreamUser}\" with flags \"${flags}\"")
				sh "docker-compose exec -T ${container} bash -c \"vendor/bin/codecept run ${flags}\""
			} else {
				flags = "--fail-fast --coverage --coverage-html --coverage-xml --steps"
				comments.commentBlock("Codeception tests", "Running Codeception tests with flags \"${flags}\"")
				sh "docker-compose exec -T ${container} bash -c \"vendor/bin/codecept run ${flags}\""
			}
		}
	} catch (testError) {
		return error(testError.getMessage())
	}
}

return this
