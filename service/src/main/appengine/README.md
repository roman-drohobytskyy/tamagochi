 # to deploy the service:
 
 mvn clean package appengine:deploy -Dmaven.test.skip=true
 
 # to deploy cron jobs :
 gcloud app deploy cron.yml