# Monsieur Cuisine 2 MQTT
> [!CAUTION]
> WIP, app needs to be compiled yourself because you need to set your own MQTT Credentials inside [SerialDataHook.java](app/src/main/java/de/aropix/mcs2mqtt/hooks/SerialDataHook.java)

> [!WARNING]  
> The module hooks the Monsieur Cuisine app, do this with your own caution, I take no responsibility

This app is a LSPosed module which hooks into the Monsieur Cuisine app of the Monsieur Cuisine Smart cooking device, adding it's own Serial data listener and then return this data to Homeassistant

## Requirements
You need to have a rooted machine, with Magisk and the LSPosed module installed (also install the LSPosed manager, this way its easier for you to open it, this can be found inside the ZIP file of the LSPosed Zygisk module)
A guide on how to do this can be found here:
https://github.com/EliasKotlyar/Monsieur-Cuisine-Connect-Hack/issues/38#issuecomment-1928542640

Afterwards compile and install the APK on the device and enable it for the Monsieur Cuisine app inside the LSPosed manager.

Another requirement is Homeassistant with a MQTT server set up, when you have done that correctly and everything is configured properly and you launch the Monsieur Cuisine App it will start sending data to MQTT server, where Homeassistant will automatically discover it.

In the end it should look like this:

![hass_screenshot.png](hass_screenshot.png)

## Features

- [x] Implement listener for serial data
- [x] Send data to MQTT
- [ ] Implement saving of MQTT preferences
- [ ] Hook button inside MC-App to show the module settings
- [ ] Add utilities inside module settings