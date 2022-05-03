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

## Instructions

#### Cloudformation Provision

In order to create jenkins ecosystem infrastructure create a new stack by using cli and yaml file
```sh
aws cloudformation create-stack --stack-name infrastructure-jenkins --template-body file://jenkinsResources.yaml --parameters ParameterKey=pemFileName,ParameterValue=jenkins.pem
```
In this case we won't pass any parameter as parameters will be retrieved from system manager


