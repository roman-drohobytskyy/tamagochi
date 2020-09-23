# to deploy the service (standard env):
```
mvn clean package appengine:deploy
```

# to deploy the service (flexible env):
```
gcloud app deploy -v v1 app.yml
```
 
# to deploy cron jobs :
```
gcloud app deploy cron.yml
```