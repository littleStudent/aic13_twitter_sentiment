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

###usage
usage is pretty simple. Type in the search text and add a date range.
Since our tweets are in a very short range (<1day) please insert a range from 2010 to 2014
Dateformat: (2010-01-30)
