package brooklyn.location.azure;

import static org.testng.Assert.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
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
public class AzureCliMachineProvisioningLocationTest {

    private static final Logger LOG = LoggerFactory.getLogger(AzureCliMachineProvisioningLocationTest.class);

    public static final String LOCATION_SPEC = "azure:North Europe";
    
    protected ManagementContext managementContext;
    protected AzureCliMachineProvisioningLocation location;
    protected AzureSshMachineLocation machine;

    @DataProvider(name = "imageIds")
    private Object[][] provideImageIds() {
        return new Object[][] {
            new Object[] {
                "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_1-LTS-amd64-server-20140909-en-us-30GB", "Ubuntu 14.04.1"
            },
            new Object[] {
                "bd507d3a70934695bc2128e3e5a255ba__RightImage-Windows-2008R2-x64-v14", "Windows 2008 R2"
            }
        };
    }

    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        // Don't let any defaults from brooklyn.properties (except credentials) interfere with test
        managementContext = new LocalManagementContext();
        location = (AzureCliMachineProvisioningLocation) managementContext.getLocationRegistry().resolve(LOCATION_SPEC);
    }

    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        try {
            if (machine != null && location.containsLocation(machine)) location.release(machine);
            machine = null;
        } finally {
            if (managementContext != null) Entities.destroyAllCatching(managementContext);
        }
    }

    @Test(groups={"Live"}, dataProvider = "imageIds")
    public void testObtainAndRelease(String imageId, String imageName) throws Exception {
        LOG.info("testing obtain and release for {}", imageName);
        machine = location.obtain(ImmutableMap.builder()
                .put(AzureCliMachineProvisioningLocation.IMAGE_ID, imageId)
                .put("azureUser", "brooklyn")
                .put("azurePassword", "p4ssW0rd!") // Requires upper case, lower case, number and special character
                .build());
        assertNotNull(machine);
        
        location.release(machine);
    }

}
