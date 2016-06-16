node {
    stage 'Dev'
    checkout scm
    sh 'chmod +x gradlew'
    sh './gradlew install'

    stage 'QA'
    sh './gradlew test'

    stage name: 'Staging', concurrency: 1
    sh 'chmod +x gradlew'
    sh './gradlew uploadArchives'

    def buildParams = input parameters: [[$class: 'StringParameterDefinition', name: 'release_version', defaultValue: '0.0.0'], 
	[$class: 'StringParameterDefinition', name: 'next_version', defaultValue: '0.0.0-SNAPSHOT'],
        [$class: 'StringParameterDefinition', name: 'branch_name', defaultValue: 'master']]
    def releaseVersion = buildParams['release_version']
    def nextVersion = buildParams['next_version']
    def branchName = buildParams['branch_name']
    stage name: 'Production', concurrency: 1
    sh "./gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=$releaseVersion -Prelease.newVersion=$nextVersion"
}
