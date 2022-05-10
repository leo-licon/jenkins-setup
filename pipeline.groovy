pipeline {
    agent none
    environment {
        TARGET_ENVIRONMENT = env.BRANCH_NAME == "dev" ? "dev":"prod"
        S3_BUCKET_NAME = env.BRANCH_NAME == "dev" ? env.S3_BUCKET_NAME_DEV:envS3_BUCKET_NAME_PROD
     }
    triggers {
        cron('00 23 * * *') //run at 11 PM  for nightly builds
    }
    stages {
        stage("Build") {
            agent { docker { image 'node:16.13.1-alpine' } }
            steps {
                echo "Building artifact for env ${env.TARGET_ENVIRONMENT}..."
                sh "npm ci"
                sh "npm run build"
                echo 'Build process has ber completed.'
            }
        }
        stage("test") {
            agent { docker { image 'node:16.13.1-alpine' } }
            steps {
                echo "Testing app"
                sh "NODE_ENV=env.TARGET_ENVIRONMENT npm test"
                sh "npm prune --production"
                echo 'Tests stage has been completed'
            }
        }
        stage("Deploy") {
            agent { docker { image 'amazonlinux' } }
            steps {
                echo 'Deploying to S3 Bucket'
                withAWS(region:'us-east-1', credentials:'aws-jenkins-user') {
                    echo 'Removing old artifacts'
                    s3Delete(bucket: "${S3_BUCKET_NAME}", path:'**/*')
                    echo 'Uploading out directory'
                    s3Upload(bucket: "${S3_BUCKET_NAME}", workingDir:'build', includePathPattern:'**/*');
                }
                echo 'React App has been deployed to S3 Bucket'
            }
        }
        
    }
    post {
        always {
            echo "Process was completed in ${TARGET_ENV}, proceeding to clean up directory"
            deleteDir()
        }
        success {
            echo 'successful build'
        }
        failure {
            echo "Build Failed - ${env.JOB_NAME}  ${env.BUILD_NUMBER}."
        }
        unstable {
            echo "Build Unstable - ${env.JOB_NAME} ${env.BUILD_NUMBER}."
        }
        changed {
            echo 'changed'
        }
    }
}

