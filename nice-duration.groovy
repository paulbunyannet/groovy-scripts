/**
 * return nice duration timestamp
 */

def version = '1.0'

def niceDuration(duration) {
  def seconds = ["bash", "-c", "echo \$(((\$(date +%s%N | cut -b1-13)-${duration})/1000));"].execute();
  def buffer = new StringBuffer()
  seconds.consumeProcessErrorStream(buffer)
   // http://unix.stackexchange.com/a/217604
  def hms = ["bash", "-c", "echo \"\$(date -d \"1970-01-01 + ${seconds.text} seconds\" \"+%H hours %M minutes %S seconds\")\""].execute();
  hms.consumeProcessErrorStream(buffer)
  return hms.text;
}

return this;
