/**
 * Remove old builds that are older than x days
 */

def version = '1.0'

def removeOldBuilds(buildDirectory, daysBack = 7) {
  def wp = new File("${buildDirectory}")
  def currentTime = new Date()
  def backTime = currentTime - daysBack
  wp.list().each { fileName ->
      folder = new File("${buildDirectory}/${fileName}")
      if (folder.isDirectory()) {
          def timeStamp = new Date(folder.lastModified())
          if (timeStamp.before(backTime)) {
            folder.delete()
          }
      }
  }
}

return this;
