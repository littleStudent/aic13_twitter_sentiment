package twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleConfigurationProvider;
import at.ac.tuwien.infosys.cloudscale.annotations.CloudScaleShutdown;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfiguration;
import at.ac.tuwien.infosys.cloudscale.configuration.CloudScaleConfigurationBuilder;
import at.ac.tuwien.infosys.cloudscale.vm.ec2.EC2CloudPlatformConfiguration;
import at.ac.tuwien.infosys.cloudscale.vm.localVm.LocalCloudPlatformConfiguration;

import service.RequestService;
import twitter.scaling.HostPerObjectScalingPolicy;
import twitter.scaling.SentimentScalingPolicy;
import twitter.scheduler.ThreadPoolScheduler;

public class Main {

    /**
     * Entrance point of the application.
     */
    public static void main(String[] args) {
        System.out.println("Starting...");
        
        // Default threads per request: 4
        int threadCount = 4;
        
        if (args.length == 1){
        	threadCount = Integer.parseInt(args[0]);        	
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));        
        RequestService rs = new RequestService(reader, threadCount);
        
        boolean running = true;
        
        String input = "";
        
        while (running) {
        	
        	try {
				input = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
        	
        	if (input.equals("!exit")) {
        		running = false;
        		System.out.println("Received exit command, shutting down.");
        	}
        	
        	rs.processCommand(input);
        	
        }

        /*
        long elapsed = (System.nanoTime() - start) / 1000000;

        long min = elapsed / (60 * 1000);
        long sec = (elapsed % (60 * 1000)) / 1000;
        long msec = elapsed % 1000;
        System.out.println(String.format("Elapsed: %02d:%02d.%03d", min, sec, msec));
        */
    }

    // this method is used to specify configuration for CloudScale.
    @CloudScaleConfigurationProvider
    public static CloudScaleConfiguration getConfiguration() {

    	CloudScaleConfiguration config;

    	CloudScaleConfiguration localConfig;

    	localConfig = new CloudScaleConfigurationBuilder(
    			new LocalCloudPlatformConfiguration())
    	.with(new HostPerObjectScalingPolicy())
    	.withLogging(Level.SEVERE)
    	.build();

    	// EC2 Configuration
    	config = new CloudScaleConfigurationBuilder(
                new EC2CloudPlatformConfiguration()
                    .withAwsConfigFile(ClassLoader.getSystemResource("awsconfig.cfg").getPath())
                    .withAwsEndpoint("ec2.us-east-1.amazonaws.com")
                    .withInstanceType("t1.micro")
                    .withSshKey("aic13-team3-group2")
         )
    	// THE FOLLOWING LINE HAS TO BE CHANGED ACCORDINGLY EVERY TIME THE MQ SERVER IS RESTARTED
        .withMQServer("ec2-54-204-215-52.compute-1.amazonaws.com", 61616)
        .with(new SentimentScalingPolicy())
        .withMonitoring(true)
        .build();

        return config;
    }

}
