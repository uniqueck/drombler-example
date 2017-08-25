pipeline {
	agent any
	
	stages {
		stage('Build') {
			steps {
				def mvnHome = tool 'M3'
				sh "'${mvnHome}/bin/mvn' -B clean package"
			}
		}
	}
}
