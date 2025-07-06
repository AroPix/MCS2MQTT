package de.aropix.mcs2mqtt.utils;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.aropix.mcs2mqtt.MqttHandler;

public class HassSensors {
    List<Sensor> sensors = new ArrayList<>();
    List<BinarySensor> binarySensors = new ArrayList<>();
    Mqtt3AsyncClient client;
    String defaultTopic = "mcs/data";

    public HassSensors(Mqtt3AsyncClient client) {
        this.client = client;
    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public void addBinarySensor(BinarySensor binarySensor) {
        binarySensors.add(binarySensor);
    }

    private String generateDiscoveryConfig(Sensor sensor) {
        String deviceClass;
        if (!Objects.equals(sensor.deviceClass, "") && sensor.deviceClass != null) {
            deviceClass = "\"" + sensor.deviceClass + "\"";
        } else {
            deviceClass = "null";
        }

        if (sensor.stateTopic == null) {
            sensor.stateTopic = defaultTopic;
        }



        String payload = "{"
                + "\"name\": \"" + sensor.name + "\","
                + "\"state_topic\": \"" + sensor.stateTopic + "\","
                + "\"value_template\": \"" + sensor.valueTemplate + " \","
                + "\"device_class\": " + deviceClass + ","
                + "\"unique_id\": \"" + sensor.uniqueId + "\",";


        if (sensor.unitOfMeasurement != "" && sensor.unitOfMeasurement != null) {
            payload += "\"unit_of_measurement\": \"" + sensor.unitOfMeasurement + "\",";
        }


        payload += this.deviceConfig();

        return payload;

    }

    private String generateDiscoveryConfigBinary(BinarySensor sensor) {
        String deviceClass;
        if (!Objects.equals(sensor.deviceClass, "")) {
            deviceClass = "\"" + sensor.deviceClass + "\"";
        } else {
            deviceClass = "null";
        }

        String payload = "{"
                + "\"name\": \"" + sensor.name + "\","
                + "\"state_topic\": \"" + sensor.stateTopic + "\","
                + "\"value_template\": \"" + sensor.valueTemplate + " \","
                + "\"device_class\": " + deviceClass + ","
                + "\"unique_id\": \"" + sensor.uniqueId + "\","
                + "\"payload_on\": \"" + sensor.payloadOn + "\","
                + "\"payload_off\": \"" + sensor.payloadOff + "\",";

        payload += this.deviceConfig();

        return payload;

    }

    public void generateDiscoveryConfigs() {
        for (Sensor sensor : sensors) {
            String payload = generateDiscoveryConfig(sensor);
            sendPayload("homeassistant/sensor/" + sensor.uniqueId + "/config", payload);
        }
        for (BinarySensor binarySensor : binarySensors) {
            String payload = generateDiscoveryConfigBinary(binarySensor);
            sendPayload("homeassistant/binary_sensor/" + binarySensor.uniqueId + "/config", payload);
        }
    }

    private void sendPayload(String topic, String payload) {
        client.publishWith()
                .topic(topic)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .retain(true)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Publish failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Discovery config published to " + topic);
                    }
                });
    }

    private String deviceConfig() {
        return "\"device\": {"
                + "\"identifiers\": [\"mcs\"],"
                + "\"name\": \"MCS\","
                + "\"model\": \"Monsieur Cuisine Smart\","
                + "\"manufacturer\": \"Silvercrest\""
                + "}"
                + "}";


    }


}
