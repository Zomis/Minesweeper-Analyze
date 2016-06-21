node {
    stage 'Dev'
    checkout scm
    sh './gradlew install'

    stage 'QA'
    sh './gradlew test'

    stage name: 'Staging', concurrency: 1
    sh './gradlew uploadArchives'
}
