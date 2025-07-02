# Monsieur Cuisine 2 MQTT
> [!CAUTION]
> WIP, app needs to be compiled yourself because you need to set your own MQTT Credentials inside MainHook.java

> [!WARNING]  
> The module hooks the Monsieur Cuisine app, do this with your own caution, I take no responsibility

This app is a LSPosed module which hooks into the Monsieur Cuisine app of the Monsieur Cuisine Smart cooking device, adding it's own Serial data listener and then return this data to Homeassistant

## Requirements
You need to have a rooted machine, with Magisk and the LSPosed module installed (also install the LSPosed manager, this way its easier for you to open it)
A guide on how to do this can be found here:
https://github.com/EliasKotlyar/Monsieur-Cuisine-Connect-Hack/issues/38#issuecomment-1928542640

Another requirement is Homeassistant with a MQTT server set up, when you have done that correctly and everything is configured properly and you launch the Monsieur Cuisine App it will start sending data to MQTT server, where Homeassistant will automatically discover it.

In the end it should look like this:

![hass_screenshot.png](hass_screenshot.png)