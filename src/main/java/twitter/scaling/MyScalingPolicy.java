package twitter.scaling;

import at.ac.tuwien.infosys.cloudscale.policy.AbstractScalingPolicy;
import at.ac.tuwien.infosys.cloudscale.vm.ClientCloudObject;
import at.ac.tuwien.infosys.cloudscale.vm.IHost;
import at.ac.tuwien.infosys.cloudscale.vm.IHostPool;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Simerle Christopher
 * Matr. Nr.: 0828922
 * Date: 03/11/13
 * Time: 17:12
 */
public class MyScalingPolicy extends AbstractScalingPolicy {

    @Override
    public synchronized IHost selectHost(ClientCloudObject newCloudObject, IHostPool hostPool) {
        if(hostPool.getHostsCount() > 0)
        {
            IHost selectedHost = hostPool.getHosts().iterator().next();
            UUID hostId = selectedHost.getId();
            System.out.println("SCALING: Deploying new object "+
                    newCloudObject.getCloudObjectClass().getName() +
                    " on "+(hostId != null ? hostId: "not started yet host."));

            return selectedHost;
        }
        else
        {
            System.out.println("SCALING: Deploying new object "+
                    newCloudObject.getCloudObjectClass().getName() +
                    " on new host.");
            return hostPool.startNewHostAsync();
        }
    }

    @Override
    public boolean scaleDown(IHost scaledHost, IHostPool hostPool) {
        // We will not scale down for this sample application as
        // CloudScale will shut down all hosts at the end, but you may need that.
        return false;
    }
}
