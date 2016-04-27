// TODO: Add parameter for release version somehow?

stage 'Dev'
node {
    checkout scm
    servers = load 'servers.groovy'
    sh './gradlew install'
    // dir('target') {stash name: 'war', includes: 'x.war'}
}

stage 'QA'
sh './gradlew test'

stage name: 'Staging', concurrency: 1
node {
    sh './gradlew uploadArchives'
}

input message: "Perform release?"

stage name: 'Production', concurrency: 1
node {
    sh './gradlew release'
}
