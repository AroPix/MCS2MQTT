package de.aropix.mcs2mqtt.utils;

public class BinarySensor {
    public String name;
    public String stateTopic;
    public String valueTemplate;
    public String payloadOff;
    public String payloadOn;
    public String deviceClass;
    public String uniqueId;


    public BinarySensor(String name, String stateTopic, String deviceClass, String uniqueId, String valueTemplate, String payloadOn, String payloadOff) {
        this.name = name;
        this.stateTopic = stateTopic;
        this.deviceClass = deviceClass;
        this.uniqueId = uniqueId;
        this.valueTemplate = valueTemplate;
        this.payloadOn = payloadOn;
        this.payloadOff = payloadOff;
    }

}
