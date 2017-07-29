node {
    properties([
        disableConcurrentBuilds(),
        parameters([
            booleanParam(defaultValue: false, description: 'Clean', name: 'Clean'),
            string(defaultValue: '', description: 'Release version', name: 'ReleaseVersion'),
            string(defaultValue: '', description: 'Next version', name: 'NextVersion')
        ])
    ])

    if (params.Clean) {
        step([$class: 'WsCleanup'])
    }

    checkout scm
    stage('Build') {
        sh 'chmod +x gradlew'
        sh './gradlew install'
    }

    stage(name: 'Upload', concurrency: 1) {
        sh 'chmod +x gradlew'
        sh './gradlew uploadArchives'
    }

    if (params.ReleaseVersion != '' && params.NextVersion != '') {
        stage(name: 'Release', concurrency: 1) {
            sh "./gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=$params.ReleaseVersion -Prelease.newVersion=$params.NextVersion"
        }
    }

}
