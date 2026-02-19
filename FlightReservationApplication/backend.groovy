pipeline{
    agent any 
    stages{
        stage('Code-pull'){
            steps{
                git branch: 'main', url: 'https://github.com/DIVATEe/flight-reservation-app.git'
            }
        }
        stage('Build'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    mvn clean package
                '''
            }
        }
        stage('QA-Test'){
            steps{
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar-token') {
                  sh'''
                     cd FlightReservationApplication
                     mvn sonar:sonar -Dsonar.projectKey=flight-reservation   
                  ''' 
                }
            }
        }
        stage('Docker-build'){
            steps{
                sh'''
                    cd FlightReservationApplication
                    docker build -t divatee/flight-reservation-1:latest . 
                    docker push divatee/flight-reservation-1:latest
                    docker rmi divatee/flight-reservation-1:latest
                '''
            }
        }
        stage('Deploy'){
            steps{
                sh'''
                    cd FlightReservationApplication   
                    kubectl apply -f k8s/
                '''
            }
        }
    }
}