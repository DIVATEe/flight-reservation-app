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
                withSonarQubeEnv(installationName: 'sonar', credentialsId: 'sonar_token') {
                  sh'''
                     cd FlightReservationApplication
                     mvn clean verify sonar:sonar \
                     -Dsonar.projectKey=flight-reservation \
                     -Dsonar.projectName='flight-reservation' \
                     -Dsonar.host.url=http://13.48.59.45:9000 \
                     -Dsonar.token=sqp_f93675ff50fff0f6d20ce68c77606324b7ecac79 
                  ''' 
                }
            }
        }
        stage('Docker-build'){
            steps{
                sh'''
                    cd FlightReservationApplication
                    docker build -t divatee/flight-reservation-4:latest . 
                    docker push divatee/flight-reservation-4:latest
                    docker rmi divatee/flight-reservation-4:latest
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