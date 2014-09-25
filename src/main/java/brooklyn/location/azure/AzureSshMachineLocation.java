package brooklyn.location.azure;

import java.util.Map;

import brooklyn.location.basic.SshMachineLocation;


public class AzureSshMachineLocation extends SshMachineLocation {
    private static final long serialVersionUID = 1L;
    
    public AzureSshMachineLocation() {}
    public AzureSshMachineLocation(Map<?, ?> flags) { super(flags); }
    
}
