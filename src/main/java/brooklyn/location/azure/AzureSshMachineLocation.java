package brooklyn.location.azure;

import java.net.InetAddress;
import java.util.Map;

import brooklyn.location.basic.HasSubnetHostname;
import brooklyn.location.basic.SshMachineLocation;
import brooklyn.util.flags.SetFromFlag;


public class AzureSshMachineLocation extends SshMachineLocation implements HasSubnetHostname {
    private static final long serialVersionUID = 1L;
    
    public AzureSshMachineLocation() {}
    public AzureSshMachineLocation(Map<?, ?> flags) { super(flags); }

    @SetFromFlag(nullable = false)
    protected InetAddress subnetAddress;

    @Override
    public String getSubnetHostname() {
        return subnetAddress.getCanonicalHostName();
    }
    @Override
    public String getSubnetIp() {
        return subnetAddress.getHostAddress();
    }
}
