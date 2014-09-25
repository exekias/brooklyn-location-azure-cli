brooklyn-location-azure-cli
===

This project provides Brooklyn support for using Azure cloud, using the Azure CLI. 

To use, add a maven dependency for this project to your Brooklyn project (built from the Brooklyn arhectype, for example),
ensure the Azure CLI is installed, and define the following location (shown in YAML; also supported via `brooklyn.propeties`):

    location:
      azure:
        region: North Europe
        imageId: <image to create>
        azureBinaryLocation: /path/to/azure
        # fields below are optional, defaulting to brooklyn and a randomly created password
        azureUser: <login name to create>
        azurePassword: <password to create>

To build, simply `mvn clean install`.

NB:  This library is experimental and requires the CLI to be installed.
We hope as jclouds Azure support matures, that will replace this library!
