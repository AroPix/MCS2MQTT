package de.aropix.mcs2mqtt.utils;

public class Sensor {
    public String name;
    public String stateTopic;
    public String valueTemplate;
    public String deviceClass;
    public String uniqueId;
    public String unitOfMeasurement;


    public Sensor(String name, String uniqueId, String jsonKey) {
        this.name = name;
        this.stateTopic = null;
        this.deviceClass = null;
        this.uniqueId = uniqueId;
        this.unitOfMeasurement = null;
        this.valueTemplate = "{{ value_json." + jsonKey + " }}";
    }

    public Sensor(String name, String deviceClass, String uniqueId, String unitOfMeasurement, String jsonKey) {
        this.name = name;
        this.stateTopic = null;
        this.deviceClass = deviceClass;
        this.uniqueId = uniqueId;
        this.unitOfMeasurement = unitOfMeasurement;
        this.valueTemplate = "{{ value_json." + jsonKey + " }}";
    }

    public Sensor(String name, String stateTopic, String deviceClass, String uniqueId, String unitOfMeasurement, String valueTemplate) {
        this.name = name;
        this.stateTopic = stateTopic;
        this.deviceClass = deviceClass;
        this.uniqueId = uniqueId;
        this.unitOfMeasurement = unitOfMeasurement;
        this.valueTemplate = valueTemplate;
    }

}
