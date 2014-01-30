package twitter.scaling;

import at.ac.tuwien.infosys.cloudscale.annotations.CloudObject;
import at.ac.tuwien.infosys.cloudscale.annotations.DestructCloudObject;

@CloudObject
public class EmptyCloudObject {

	@DestructCloudObject
	public void run() {
		System.out.println("Dummy object being run.");
	}

}