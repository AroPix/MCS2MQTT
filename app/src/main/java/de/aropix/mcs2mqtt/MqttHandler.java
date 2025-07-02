package de.aropix.mcs2mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.nio.charset.StandardCharsets;
import java.util.Objects;


public class MqttHandler {

    private final Mqtt3AsyncClient client;

    public MqttHandler(String hostname, String port, String username, String password) {


        client = MqttClient.builder()
                .useMqttVersion3()
                .serverHost(hostname)
                .serverPort(Integer.parseInt(port))
                .identifier("android-mcs-client")
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .send()
                .whenComplete((ack, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Connection failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Connected to MQTT broker!");
                        createConfiguration();
                    }
                });
    }

    public void sendPayload(SerialData data) {

        String time;
        if (!Objects.equals(data.getTimeString(), "null")) {
            time = "\"" + data.getTimeString() + "\"";
        } else {
            time = data.getTimeString();
        }
        // {output=null, temp=0, weight=1430, time=0, speed=0}
        String payload = "{"
                + "\"temp\": " + data.getTemp() + ", "
                + "\"weight\": " + data.getWeight() + ", "
                + "\"time\": " + time + ", "
                + "\"speed\": " + data.getSpeed() + ", "
                + "\"running\": " + data.getIdleness()
                + "}";

        client.publishWith()
                .topic("mcs/data")
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .qos(MqttQos.AT_MOST_ONCE)
                .send()
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Publish failed: " + throwable.getMessage());
                    } else {
                        System.out.println("Published data");
                    }
                });
    }

    private void publishDiscoveryConfig(String id, String key, String unit, String deviceClass, String sensorType) {
        String topic = "homeassistant/" + sensorType + "/" + id + "/config";

        if (!Objects.equals(deviceClass, "")) {
            deviceClass = "\"" + deviceClass + "\"";
        } else {
            deviceClass = "null";
        }

        String payload = "{"
                + "\"name\": \"MCS " + key + "\","
                + "\"state_topic\": \"mcs/data\","
                + "\"unit_of_measurement\": \"" + unit + "\","
                + "\"value_template\": \"{{ value_json." + key + " }}\","
                + "\"device_class\": " + deviceClass + ","
                + "\"unique_id\": \"" + id + "\","
                + "\"device\": {"
                + "\"identifiers\": [\"mcs\"],"
                + "\"name\": \"MCS\","
                + "\"model\": \"Monsieur Cuisine Smart\","
                + "\"manufacturer\": \"Silvercrest\""
                + "}"
                + "}";

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

    private void publishBinarySensorDiscoveryConfig() {
        String topic = "homeassistant/binary_sensor/mcs_running/config";

        String payload = "{"
                + "\"name\": \"MCS running\","
                + "\"state_topic\": \"mcs/data\","
                + "\"value_template\": \"{{ 'on' if value_json.running else 'off' }}\","
                + "\"payload_on\": \"on\","
                + "\"payload_off\": \"off\","
                + "\"device_class\": \"running\","
                + "\"unique_id\": \"mcs_running\","
                + "\"device\": {"
                + "\"identifiers\": [\"mcs\"],"
                + "\"name\": \"MCS\","
                + "\"model\": \"Monsieur Cuisine Smart\","
                + "\"manufacturer\": \"Silvercrest\""
                + "}"
                + "}";

        client.publishWith()
                .topic(topic)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .retain(true)
                .qos(MqttQos.AT_LEAST_ONCE)
                .send()
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        System.err.println("Failed to publish binary sensor config: " + throwable.getMessage());
                    } else {
                        System.out.println("Binary sensor discovery config published to " + topic);
                    }
                });
    }


    private void createConfiguration() {
        publishDiscoveryConfig("mcs_speed", "speed", "", "", "sensor");
        publishDiscoveryConfig("mcs_weight", "weight", "g", "weight", "sensor");
        publishDiscoveryConfig("mcs_time", "time", "", "timestamp", "sensor");
        //publishDiscoveryConfig("mcs_data", "data", "", "");
        publishDiscoveryConfig("mcs_temp", "temp", "°C", "temperature", "sensor");
        publishBinarySensorDiscoveryConfig();
    }
}
