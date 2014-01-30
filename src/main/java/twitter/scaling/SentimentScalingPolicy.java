package twitter.scaling;

import at.ac.tuwien.infosys.cloudscale.policy.AbstractScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;

import java.util.UUID;

public class SentimentScalingPolicy extends AbstractScalingPolicy {

	/*
	 * TODO: 
	 * What is the limiting factor for our sentiment analysis?
	 * I assumed CPU load, but we might as well use RAM usage, depending on what the bottleneck is
	 * 
	 */	
	
	// Maximum number of hosts that may be running at any one time
	private final int maxHosts = 4;
	
	// Minimum number of hosts that have to be ready and running at any one time
	private final int minHosts = 2;
	
	// When this threshold for average CPU load is exceeded, a new host will be started (up to the maximum defined in maxHosts)
	private final double scaleUpCPUThreshold = 0.8;
	
	// When average CPU load falls below this threshold, scale down a host (down to the minimum defined in minHosts)
	private final double scaleDownCPUThreshold = 0.2;	
	
	// When average CPU load falls below this threshold, stop assigning new jobs to the lowest-load hosts to prepare for downscaling
	private final double balancingCPUThreshold = 0.4;
	
	private Object lock = new Object();
	
    @Override
    public IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool) {
    	
    	synchronized (lock) {
    	
    	IHost minLoadHost = null;
    	IHost secondMinLoadHost = null;
    	IHost selectedHost;
    	
    	double averageCPULoad = 0;
    	double totalCPULoad = 0;
    	double minCPULoad = 1;
    	
    	int runningHosts = 0;
    	
    	// Calculate average CPU load
    	for (IHost h : hostPool.getHosts()) {
    		if (h.isOnline()) {
    			runningHosts++;
    			totalCPULoad += h.getCurrentCPULoad().getCpuLoad();
    		}
    		if (h.isOnline() && (h.getCurrentCPULoad().getCpuLoad() < minCPULoad)) {
    			minCPULoad = h.getCurrentCPULoad().getCpuLoad();
    			secondMinLoadHost = minLoadHost;
    			minLoadHost = h;
    		}
    	}
    	
    	if (runningHosts != 0) {
    		averageCPULoad = totalCPULoad / runningHosts;
    	}
    	
    	System.out.println("(Upscaling) Currently running hosts: " + runningHosts);
    	
    	//There should always be a minimum amount of hosts running
    	if(runningHosts < minHosts)
        {
    		for (int i = 0; i < (minHosts - runningHosts-1); i++)
    		{
    			System.out.println("(Upscaling) Starting new host.");
    			hostPool.startNewHostAsync();
    			runningHosts++;
    		}
    		//return last started Host
    		 runningHosts++;
    		 return hostPool.startNewHostAsync();
        }    		
    	
    	// Select a host to run on
    	// Note that selection and upscaling are independent of each other
    	
    	// Current solution: 
    	// when average load is high (l > balancingCPUThreshold), or there are only the minimum number of hosts running,
    	// then the lowest loaded host is selected for optimal load balancing
    	// when average load is low (l < balancingCPUThreshold) then the second lowest loaded host is selected
    	// This is done so that the lowest loaded host can finish its jobs and be scaled down afterwards
    	if ((runningHosts <= minHosts) || (averageCPULoad >= balancingCPUThreshold)) {
    		selectedHost = minLoadHost;
    	}
    	else if ((runningHosts > minHosts) && (averageCPULoad < balancingCPUThreshold)) {
    		selectedHost = secondMinLoadHost;
    	}
    	else {
    		// this shouldn't happen, but when in doubt, select the host with the lowest load
    		selectedHost = minLoadHost;
    	}
    	
    	// Start a new host if necessary
    	// do this after selection so that the newly started host cannot be selected
    	if (averageCPULoad > scaleUpCPUThreshold) {
    		if (hostPool.getHostsCount() < maxHosts) { 
    			hostPool.startNewHostAsync();
    		}
    	}

    	
        UUID hostId = selectedHost.getId();
        System.out.println("(Selection) Deploying new object "+
                newCloudObject.getCloudObjectClass().getName() +
                " on "+(hostId != null ? hostId: "not started yet host."));

    	
    	return selectedHost;
    	
    	}
    }

    @Override
    public boolean scaleDown(IHost scaledHost, IHostPool hostPool) {

		// we scale down iff
    	// - host is online
		// - runningHosts > minHosts
    	// - host currently not used
    	// - averageCPULoad < scaleDownCPUThreshold
    	
		boolean result = true;
		
		synchronized (lock) {

	    	double averageCPULoad = 0;
	    	double totalCPULoad = 0;
	    	int runningHosts = 0;
	    	
	    	// Calculate average CPU load
	    	for (IHost h : hostPool.getHosts()) {
	    		totalCPULoad += h.getCurrentCPULoad().getCpuLoad();
	    		if (h.isOnline()) {
	    			runningHosts++;
	    		}
	    	}
	    	
	    	if (runningHosts != 0) {
	    		averageCPULoad = totalCPULoad / runningHosts;
	    	}
	    	
	    	System.out.println("\n-------------------------------------------------------------");
	    	System.out.println("Downscaling attempt for host "+scaledHost.getId().toString());
	    	System.out.println("  Current load: " + scaledHost.getCurrentCPULoad());
	    	System.out.println("  Current objects: " + scaledHost.getCloudObjectsCount());
	    	System.out.println("  Running hosts: " + runningHosts);
	    	System.out.println("  System average load/total load: " + averageCPULoad + "/" + totalCPULoad);
			System.out.println("-------------------------------------------------------------");
			
			if(averageCPULoad > scaleDownCPUThreshold) {
				result = false;
				System.out.println("Not scaling down. Average load too high");
			}
			if (runningHosts <= minHosts) {
				result = false;
				System.out.println("Not scaling down. Already running minimum number of hosts");
			}
			if(!scaledHost.isOnline()) {
				result = false;
				System.out.println("Not scaling down. Host is offline");
			}

			if(scaledHost.getCloudObjectsCount() > 0) {
				result = false;
				System.out.println("Not scaling down. Host is in use");
			}

		}
		if(result) {
			System.out.println("Scaling down host "+scaledHost.getId().toString());
		}
		return result;
    }
}
