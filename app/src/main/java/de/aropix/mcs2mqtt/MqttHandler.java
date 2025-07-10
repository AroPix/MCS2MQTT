package de.aropix.mcs2mqtt;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.nio.charset.StandardCharsets;

import de.aropix.mcs2mqtt.utils.BinarySensor;
import de.aropix.mcs2mqtt.utils.HassSensors;
import de.aropix.mcs2mqtt.utils.Sensor;

public class MqttHandler {

    private Mqtt3AsyncClient client;

    public MqttHandler(String hostname, String port, String username, String password) {
        rebuildClient(hostname, port, username, password);
    }

    public void reconnect(String hostname, String port, String username, String password) {
        if (client != null) {
            client.disconnect()
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            System.err.println("Disconnect failed: " + throwable.getMessage());
                        } else {
                            System.out.println("Disconnected. Reconnecting...");
                            rebuildClient(hostname, port, username, password);
                        }
                    });
        } else {
            rebuildClient(hostname, port, username, password);
        }
    }

    private void rebuildClient(String hostname, String port, String username, String password) {
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

    public void sendPayload(String payload, String topic) {
        client.publishWith()
                .topic(topic)
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

    private void createConfiguration() {
        HassSensors sensors = new HassSensors(client);
        sensors.addSensor(new Sensor("Speed", "mcs_speed", "speed"));
        sensors.addSensor(new Sensor("Time", "timestamp", "mcs_time", "", "time"));
        sensors.addSensor(new Sensor("Weight", "weight", "mcs_weight", "g", "weight"));
        sensors.addSensor(new Sensor("Temperature", "temp", "mcs_temp", "°C", "temperature"));
        sensors.addSensor(new Sensor("Recipe Name", "mcs/recipe", null, "mcs_recipe_name", "", "{{ value_json.recipe_name }}"));

        sensors.addBinarySensor(new BinarySensor("Running", "mcs/data", "running", "mcs_running", "{{ 'on' if value_json.running else 'off' }}", "on", "off"));

        sensors.generateDiscoveryConfigs();
    }
}
