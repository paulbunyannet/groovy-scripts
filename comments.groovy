#!/usr/bin/groovy

def commentBlock(String title="", String body="" ) {
	Integer lineLength = 75
	StringBuilder sb = new StringBuilder()
	for (int i = 0; i < body.length(); i++) {
		if (i > 0 && (i % lineLength == 0)) {
			sb.append("\n")
		}

		sb.append(body.charAt(i))
	}

	body = sb.toString()
	echo "    /*\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    | ${title}\n" +
			"    |--------------------------------------------------------------------------\n" +
			"    |\n" +
			"    | ${body}\n" +
			"    */"
}
return this
