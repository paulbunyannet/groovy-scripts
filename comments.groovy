#!/usr/bin/groovy
/**
 * Create a comment block
 * @param title
 * @param body
 * @return
 */
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
/**
 * Create an inline comment
 * @param body
 * @param prefix
 * @param suffix
 * @return
 */
def commentInline(String body='', String prefix="/**", String suffix="*/") {
	echo "${prefix} ${body} ${suffix}"
}

return this
