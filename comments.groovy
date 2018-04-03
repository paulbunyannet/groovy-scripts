#!/usr/bin/groovy

def comments (String title="", String body="" ) {
	Integer length = 75
	String parsedBody = body.replaceAll("(.{${length})", "$1\n")
	echo "    /*\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    | ${title}\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    |\n" +
			"    | ${parsedBody}\n" +
			"    */"
}
return this
