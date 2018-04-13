#!/usr/bin/groovy

GString dockerComposeExecCmd(String container, String command) {
	return "docker-compose exec -T ${container} ${command}"
}

/**
 * Run a command in docker from docker-compose exec container command
 * @param container
 * @param command
 * @return
 */
def dockerComposeExec(String container, String command) {
	sh "${dockerComposeExecCmd(container, command)}"
}

/**
 * Run a bash command in docker from docker-compose exec container bash -c "command"
 * @param container
 * @param command
 * @return
 */
def dockerComposeBash(String container, String command) {
	dockerComposeExec(container, "bash -c \"${command}\"" as String)
}

return this
