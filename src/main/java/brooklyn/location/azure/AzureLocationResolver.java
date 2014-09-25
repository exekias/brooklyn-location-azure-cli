package brooklyn.location.azure;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.location.Location;
import brooklyn.location.LocationRegistry;
import brooklyn.location.LocationResolver;
import brooklyn.location.LocationSpec;
import brooklyn.location.basic.BasicLocationRegistry;
import brooklyn.location.basic.LocationInternal;
import brooklyn.location.basic.LocationPropertiesFromBrooklynProperties;
import brooklyn.location.cloud.CloudLocationConfig;
import brooklyn.management.ManagementContext;
import brooklyn.util.collections.MutableMap;

import com.google.common.base.Joiner;

public class AzureLocationResolver implements LocationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(AzureLocationResolver.class);
    
    static final String AZURE = "azure";
    static final Pattern PATTERN = Pattern.compile("(" + AZURE + ")" + "(?::(.*))?$");
    
    private ManagementContext managementContext;
    
    @Override
    public void init(ManagementContext managementContext) {
        this.managementContext = checkNotNull(managementContext, "managementContext");
    }

    @Override
    public String getPrefix() {
        return AZURE;
    }

    @Override
    public boolean accepts(String spec, LocationRegistry registry) {
        return BasicLocationRegistry.isResolverPrefixForSpec(this, spec, true);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Location newLocationFromString(Map locationFlags, String spec, brooklyn.location.LocationRegistry registry) {
        return newLocationFromString(spec, registry, registry.getProperties(), locationFlags);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected AzureCliMachineProvisioningLocation newLocationFromString(String spec, brooklyn.location.LocationRegistry registry, Map properties, Map locationFlags) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Resolving location '" + spec + "' with flags " + Joiner.on(",").withKeyValueSeparator("=").join(locationFlags));
        }
        String namedLocation = (String) locationFlags.get(LocationInternal.NAMED_SPEC_NAME.getName());

        Matcher matcher = PATTERN.matcher(spec);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid location '"+spec+"';");
        }
        
        // optional
        String region = matcher.group(2);

        Map<String, Object> filteredProperties = new LocationPropertiesFromBrooklynProperties().getLocationProperties(AZURE, namedLocation, properties);
        MutableMap<String, Object> flags = MutableMap.<String, Object>builder().putAll(filteredProperties).putAll(locationFlags).build();

        LocationSpec<AzureCliMachineProvisioningLocation> locationSpec = LocationSpec.create(AzureCliMachineProvisioningLocation.class)
                .configure(flags)
                .configureIfNotNull(CloudLocationConfig.CLOUD_REGION_ID, region)
//                .displayName(spec)
                ;
        return managementContext.getLocationManager().createLocation(locationSpec);
    }

}
