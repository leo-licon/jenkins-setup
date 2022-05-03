# TODO PAGE
This page describes the goals for the branch, will be updated as I it has progress on what is left.

# Upcoming files and definition
`jenkisConfig.yaml` file contains ansible playbook that set up Jenkins

`pipeline.groovy` file contains gorvy script that defines CI pipeline for a react static site stored in S3.
  - Using any agent we set 2 variables TARGET_ENV and S3_BUCKET_NAME
  - Will use _node 16_, so ti is declared in the tools part
  - Pipeline consists in 3 stages:
    - Build:
      - `npm ci`: which installs all dependencies from lock file, making sure env is just as is in developer machine.
      - `npm run build`: builds node application and generates build and dist directories
    - Test:
      - `NODE_ENV=env.TARGET_ENVIRONMENT npm test`: run tests, using env, in case node requires different setting for each environment
      - `npm prune --production` clean up node_modules directory for any dependency declared in _devDependencies_
    - Deploy:
      - Using AWS pluigin, cleanup S3 destination directory, and replace with contents in _build_ directory
  -Post block deletes directory each time it finishes to avoid have residual files that can affect further builds


## Instructions

### Ansible
Here I explai how to use playbook to setup jenkins and create a pipeline using groovy script from `pipeline.groovy`

### Pipeline
Once jenkins file (or script) is created, I'll explain how to use it or intall it