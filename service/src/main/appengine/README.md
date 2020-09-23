# to deploy the service:
 
mvn clean package appengine:deploy
 
# to deploy cron jobs :
gcloud app deploy cron.yml