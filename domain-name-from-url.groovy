
/**
 * return domain name from url
 * Example:
 * http://
 */

def version = '1.0'

def domainNameFromUrl(url) {
  def base = ["bash", "-c", "echo ${url} | cut -d'/' -f3 | cut -d':' -f1"].execute();
  def buffer = new StringBuffer();
  base.consumeProcessErrorStream(buffer);
  def urlOut = base.text;
  return urlOut.trim();
}

return this;
