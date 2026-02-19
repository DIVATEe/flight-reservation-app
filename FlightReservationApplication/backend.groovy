pipeline{
    agent any 
    stages{
        stage('Code-pull'){
            steps{
                git branch: 'main', url: 'https://github.com/DIVATEe/flight-reservation-app.git'
            }
        }
    }
}