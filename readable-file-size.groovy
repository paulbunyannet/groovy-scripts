/**
 * Return human readable file size
 * https://stackoverflow.com/a/5599842/405758
 */

def version = '1.0'
import java.text.DecimalFormat;

public static String readableFileSize(long size) {
    if(size <= 0) return "0";
    final String[] units = ["B","kB","MB","GB","TB"] as  String[];
    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
}

return this;
​
