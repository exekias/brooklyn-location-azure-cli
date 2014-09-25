package brooklyn.location.azure;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.NoSuchElementException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import brooklyn.config.BrooklynProperties;
import brooklyn.entity.basic.Entities;
import brooklyn.management.internal.LocalManagementContext;
import brooklyn.test.entity.LocalManagementContextForTests;

@Test
public class AzureLocationResolverTest {
    
    private BrooklynProperties brooklynProperties;
    private LocalManagementContext managementContext;
    
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        managementContext = LocalManagementContextForTests.newInstance();
        brooklynProperties = managementContext.getBrooklynProperties();
    }
    
    @AfterMethod(alwaysRun=true)
    public void tearDown() throws Exception {
        if (managementContext != null) Entities.destroyAll(managementContext);
    }
    
    @Test
    public void testTakesAzureScopedProperties() {
        brooklynProperties.put("brooklyn.location.azure.imageId", "myimageid");

        AzureCliMachineProvisioningLocation loc = resolve("azure:myregion");
        Map<String, Object> conf = loc.getAllConfig(true);

        assertEquals(conf.get("region"), "myregion");
        assertEquals(conf.get("imageId"), "myimageid");

        assertEquals(loc.getRegion(), "myregion");
    }

    @Test
    public void testDefaults() throws Exception {
        AzureCliMachineProvisioningLocation loc = resolve("azure");
        assertEquals(loc.getRegion(), null);
        assertEquals(loc.getDisplayName(), "azure");
    }

    @Test
    public void testNamedLocation() throws Exception {
        brooklynProperties.put("brooklyn.location.named.mynamed", "azure:myregion");
        brooklynProperties.put("brooklyn.location.named.mynamed.displayName", "mydisplayname");
        
        AzureCliMachineProvisioningLocation loc = resolve("named:mynamed");
        assertEquals(loc.getDisplayName(), "mydisplayname");
    }

    @Test
    public void testNamedLocationDefaultName() throws Exception {
        brooklynProperties.put("brooklyn.location.named.mynamed", "azure:myregion");
        
        AzureCliMachineProvisioningLocation loc = resolve("named:mynamed");
        assertEquals(loc.getDisplayName(), "azure:myregion");
        // TODO maybe it should be something like the following, as default?:
//        assertEquals(loc.getDisplayName(), "azure:myregion (mynamed)");
    }

    @Test
    public void testInheritedNamedLocation() throws Exception {
        brooklynProperties.put("brooklyn.location.azure.displayName", "mydisplayname");
        brooklynProperties.put("brooklyn.location.named.mynamed", "azure:myregion");
        
        AzureCliMachineProvisioningLocation loc = resolve("named:mynamed");
        assertEquals(loc.getDisplayName(), "mydisplayname");
    }

    @Test
    public void testPropertyScopePrescedence() throws Exception {
        brooklynProperties.put("brooklyn.location.named.mynamed", "azure:myregion");
        
        // prefer those in "named" over everything else
        brooklynProperties.put("brooklyn.location.named.mynamed.imageId", "imageId-inNamed");
        brooklynProperties.put("brooklyn.location.azure.imageId", "imageId-inProviderSpecific");
        brooklynProperties.put("brooklyn.localhost.imageId", "imageId-inGeneric");

        // prefer those in provider-specific over generic
        brooklynProperties.put("brooklyn.location.azure.foo", "foo-inProviderSpecific");
        brooklynProperties.put("brooklyn.location.foo", "foo-inGeneric");

        // prefer location-generic if nothing else
        brooklynProperties.put("brooklyn.location.bar", "bar-inGeneric");

        Map<String, Object> conf = resolve("named:mynamed").getAllConfig(true);
        
        assertEquals(conf.get("imageId"), "imageId-inNamed");
        assertEquals(conf.get("foo"), "foo-inProviderSpecific");
        assertEquals(conf.get("bar"), "bar-inGeneric");
    }

    @Test
    public void testThrowsOnInvalid() throws Exception {
        assertThrowsNoSuchElement("wrongprefix:(hosts=\"1.1.1.1\")");
        assertThrowsIllegalArgument("single");
    }
    
    private void assertThrowsNoSuchElement(String val) {
        try {
            resolve(val);
            fail();
        } catch (NoSuchElementException e) {
            // success
        }
    }
    
    private void assertThrowsIllegalArgument(String val) {
        try {
            resolve(val);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }
    
    private AzureCliMachineProvisioningLocation resolve(String val) {
        return (AzureCliMachineProvisioningLocation) managementContext.getLocationRegistry().resolve(val);
    }
}
