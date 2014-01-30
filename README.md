aic13_twitter_sentiment
=======================

For the project to work with AWS put the accessKey + secretKey in the file src/main/resource/awsconfig.cfg

```
accessKey = 
secretKey = 
```

start the following AWS instances:
```
aic13-team3-group2_ActiveMQ
aic13-team3-group2_Twitter_Small
```
assign the elastic IP ```54.247.105.211``` to the instance ```aic13-team3-group2_Twitter_Small```.

assign the elastic IP ```54.246.86.65``` to the instance ```aic13-team3-group2_ActiveMQ```.

 
now run via mvn

 * ```mvn install:install-file -DgroupId=com.aliasi -DartifactId=lingpipe -Dversion=4.1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=lib/lingpipe-4.1.0.jar```
 * ```mvn clean compile exec:exec```

