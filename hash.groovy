def version = '1.0'

import java.security.MessageDigest

//https://gist.github.com/ikarius/299062#gistcomment-1463377
def generateMD5(String s){
    MessageDigest.getInstance("MD5").digest(s.bytes).encodeHex().toString()
}

return this;
