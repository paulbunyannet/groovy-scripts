#!/usr/bin/groovy
def commentBlock(String title="", String body="" ) {
	Integer lineLength = 75
	String linePrefix = " | "
	String[] lines = body.split("(?<=\\G.{${lineLength}})")
	String lineJoin = "\n${linePrefix}"
	echo "/*\n" +
			" |${'-'.multiply(lineLength)}\n" +
			" | ${title}\n" +
			" |${'-'.multiply(lineLength)}\n" +
			" |\n" +
			" | ${lines.join(lineJoin)}\n" +
			"*/"
}
return this
