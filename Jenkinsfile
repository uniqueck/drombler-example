pipeline {
	agent any
	
	stages {
		def mvnHome
		stage('Preparation') {
			mvnHome = tool 'M3'
		}
		stage('Build') {
			steps {
				sh "'${mvnHome}/bin/mvn' -B clean package"
			}
		}
	}
}
