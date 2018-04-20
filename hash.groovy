def version = '1.0'

/**
 * MD5 a file contents
 * https://gist.github.com/mcquinne/2655782
 * @param file
 * @return
 */
def generateMD5( File file ) {
    def digest = java.security.MessageDigest.getInstance("MD5")
    file.eachByte( 4096 ) { buffer, length ->
        digest.update( buffer, 0, length )
    }
    new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
}

return this
