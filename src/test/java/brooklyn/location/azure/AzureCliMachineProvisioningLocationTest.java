package brooklyn.location.azure;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.entity.basic.Entities;
import brooklyn.management.ManagementContext;
import brooklyn.management.internal.LocalManagementContext;

import com.google.common.collect.ImmutableMap;

/**
 * NOTE: provisioning Windows VMs on Azure takes a *long* time relative to Linux instances in other clouds!
 *
 * NOTE: the AzureCliMachineProvisioningLocation simply calls-out to the Azure command-line client,
 *       and assumes it is installed on the local machine and available on the current user's $PATH.
 */
@Test(groups={"Live"})
public class AzureCliMachineProvisioningLocationTest {

    public static final String AZURE_IMAGE_ID = "whatIsTheImageId?";
    public static final String LOCATION_SPEC = "azure:North Europe";
    
    protected ManagementContext managementContext;
    protected AzureCliMachineProvisioningLocation location;
    protected AzureSshMachineLocation machine;

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        // Don't let any defaults from brooklyn.properties (except credentials) interfere with test
        managementContext = new LocalManagementContext();
        location = (AzureCliMachineProvisioningLocation) managementContext.getLocationRegistry().resolve(LOCATION_SPEC);
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        try {
            if (machine != null) location.release(machine);
            machine = null;
        } finally {
            if (managementContext != null) Entities.destroyAllCatching(managementContext);
        }
    }

    @Test
    public void testObtainAndRelease() throws Exception {
        machine = location.obtain(ImmutableMap.builder()
                .put(AzureCliMachineProvisioningLocation.IMAGE_ID, AZURE_IMAGE_ID)
                .put("user", "brooklyn")
                .put("password", "p4ssw0rd")
                .build());
        assertNotNull(machine);
        
        location.release(machine);
    }

}
