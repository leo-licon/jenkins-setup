# jenkins-setup
Setup Jenkis with AWS cloudformation &amp; ansible

## Goals
  - Provision a Jenkins instance with cloudformation
  - Configutre and manage this instance with Ansible
  - Generate a Jenkins pipeline with Groovy scripts
  - Provide instructios for usage

## Assumptions
We already have an AWS account with basic setup like:
  - VPC
  - Subnets
  - Route tables
  - pem file (named _jenkins.pem_)
  - AWS role that allow us to create resources with cloudfromation
  - AWS CLI is installed and configured with valid Access Key, Secret Key, Region, etc
Ansible control node exists and has right to perform operation

## Definition
`jenkinsResources.yaml` file contains Resource declaration for cloudformation in order to provision infrastructure:
  - EC2 instance
  - Instance Profile
  - AWS Instance role
  - Security group
  - EC2 Volume
  - Volume Attachment
  - S3 buklet to deliver artifacts
  - S3 policy
`pipeline.groovy` file contains gorvy script that defines CI pipeline for a react static site stored in S3. (This is the jenkinsfile for the pipeline, I use groovy extension in order lint it)
  - Using no agent agent we set 2 variables TARGET_ENV and S3_BUCKET_NAME
  - Pipeline consists in 3 stages:
    - Build:
      - Agent used: node-16 docker image
      - `npm ci`: which installs all dependencies from lock file, making sure env is just as is in developer machine.
      - `npm run build`: builds node application and generates build and dist directories
    - Test:
      - Agent used: node-16 docker image
      - `NODE_ENV=env.TARGET_ENVIRONMENT npm test`: run tests, using env, in case node requires different setting for each environment
      - `npm prune --production` clean up node_modules directory for any dependency declared in _devDependencies_
    - Deploy:
      - Agent used: amazonlinux docker image
      - Using AWS pluigin, cleanup S3 destination directory, and replace with contents in _build_ directory
  -Post block deletes directory each time it finishes to avoid have residual files that can affect further builds

## Instructions

#### Cloudformation Provision

In order to create jenkins ecosystem infrastructure create a new stack by using cli and yaml file
```sh
aws cloudformation create-stack --stack-name infrastructure-jenkins --template-body file://jenkinsResources.yaml --parameters ParameterKey=pemFileName,ParameterValue=jenkins.pem
```
In this case we won't pass any parameter as parameters will be retrieved from system manager


