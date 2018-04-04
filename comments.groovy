#!/usr/bin/groovy

def commentBlock(String title="", String body="" ) {
	Integer lineLength = 75
	String linePrefix = "    | "
	parsedBody = sh(script:"echo ${body} | sed -e \"s/.\\{${lineLength}\\}/&\\n${linePrefix}/g\"", returnStdout: true)
	echo "    /*\n" +
			"    |${'-'.multiply(lineLength)}\n" +
			"    | ${title}\n" +
			"    |${'-'.multiply(lineLength)}\n" +
			"    |\n" +
			"    | ${parsedBody.trim()}\n" +
			"    */"
}
return this
