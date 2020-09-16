 # to deploy to App Engine Standard:
 mvn clean package appengine:deploy -Dmaven.test.skip=true
 
 
 # to deploy to App Engine Flexible :
 gcloud app deploy
 
 
 # to deploy cron jobs :
 gcloud app deploy cron.yml