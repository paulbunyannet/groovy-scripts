
def version = '1.0'

/**
 * Is the current gitlab user master and using the develop branch?
 * @param string master the master user
 */
def isMainRepoAndDevelop(String master="Web Development"){
    echo "gitlabSourceNamespace ${gitlabSourceNamespace} | gitlabTargetBranch ${gitlabTargetBranch}"
    if ("${gitlabSourceNamespace}" == master && "${gitlabTargetBranch}" == "develop") {
      return true
    }
    return false
}

/**
 * Is the current gitlab user master and using the master branch?
 * @param string master the master user
 */
def isMainRepoAndMaster(String master="Web Development"){
    echo "gitlabSourceNamespace ${gitlabSourceNamespace} | gitlabTargetBranch ${gitlabTargetBranch}"
    if ("${gitlabSourceNamespace}" == master && "${gitlabTargetBranch}" == "master") {
      return true
    }
    return false
}

/**
 * Is the current gitlab user master and using either the develop or master branch?
 * @param string master the master user
 */
def isMainRepoAndDevelopOrMaster(String master="Web Development"){
  if (isMainRepoAndDevelop(master) || isMainRepoAndMaster(master)) {
    return true
  }
  return false
}

return this;
