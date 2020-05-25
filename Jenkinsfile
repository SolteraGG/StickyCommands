// Scripted Pipeline
// Requires libraries from https://github.com/Prouser123/jenkins-tools
// Made by @Prouser123 for https://ci.jcx.ovh.

node('docker-cli') {
  cleanWs()

  docker.image('jcxldn/jenkins-containers:jdk11-mvn-ubuntu').inside {

    stage('Setup') {
      checkout scm
      
      // Download BuildTools and required dependencies.
      sh 'apt-get update && apt-get install git wget -y && wget -O https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar'
      
    }
    
    stage('Setup (BuildTools)') {
      sh 'mkdir -p libs && cd libs && java -jar BuildTools.jar --rev 1.15.2'
    }

    stage('Build') {
      sh 'mvn clean install'
        
      archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
				
      ghSetStatus("The build passed.", "success", "ci")
    }
  }
}
