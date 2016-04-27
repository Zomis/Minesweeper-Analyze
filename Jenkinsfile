// TODO: Add parameter for release version somehow?

stage 'Dev'
node {
    chmod +x gradlew
    checkout scm
    sh './gradlew install'
    // dir('target') {stash name: 'war', includes: 'x.war'}
}

stage 'QA'
node {
    chmod +x gradlew
    sh './gradlew test'
}

stage name: 'Staging', concurrency: 1
node {
    chmod +x gradlew
    sh './gradlew uploadArchives'
}

input message: "Perform release?"

stage name: 'Production', concurrency: 1
node {
    chmod +x gradlew
    sh './gradlew release'
}
