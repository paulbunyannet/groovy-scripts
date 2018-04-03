#!/usr/bin/groovy

def commentBlock(String title="", String body="" ) {
	Integer lineLength = 75
	String linePrefix = "    | "
	parsedBody = sh(script:"echo ${body} | sed -e \"s/.\\{${lineLength}\\}/&\\n${linePrefix}/g\"", returnStdout: true)
	echo "    /*\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    | ${title}\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    |\n" +
			"    | ${parsedBody.trim()}\n" +
			"    */"
}
return this
