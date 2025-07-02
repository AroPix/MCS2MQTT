package de.aropix.mcs2mqtt;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private static SerialData lastDataMap;
    private long lastTimeStamp = 0;
    private long lastMillis = 0;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tecpal.device.mc30")) return;

        XSharedPreferences prefs = new XSharedPreferences("de.aropix.mcs2mqtt", "mqtt_settings");
        prefs.reload();

        String hostname = prefs.getString("mqtt_host", "192.168.178.2");
        String port = prefs.getString("mqtt_port", "1883");
        String username = prefs.getString("mqtt_username", "");
        String password = prefs.getString("mqtt_password", "");
        XposedBridge.log("Hostname: " + hostname + " | Username: " + username);

        MqttHandler mqtt = new MqttHandler(hostname, port, username, password);

        Class<?> listenerServiceClass = XposedHelpers.findClass("a.g.a.t.g.m", lpparam.classLoader);
        Object listenerServiceInstance = XposedHelpers.callStaticMethod(listenerServiceClass, "r");

        Class<?> interfaceClass = Class.forName("a.g.a.t.g.g", false, lpparam.classLoader);

        Object myListener = Proxy.newProxyInstance(
                lpparam.classLoader,
                new Class[]{interfaceClass},
                (proxy, method, args) -> {
                    if (args != null && args.length >= 1 && args[0] != null) {
                        Object output = args[0];
                        try {
                            SerialData currentData = new SerialData();

                            Method getWeight = output.getClass().getDeclaredMethod("getWeight");
                            getWeight.setAccessible(true);
                            Number weight = (Number) getWeight.invoke(output);
                            currentData.setWeight(weight.intValue());

                            Method getSpeed = output.getClass().getDeclaredMethod("getSpeed");
                            getSpeed.setAccessible(true);
                            Number speed = (Number) getSpeed.invoke(output);
                            currentData.setSpeed(speed.intValue());

                            Method getTemperature = output.getClass().getDeclaredMethod("getTemperature");
                            getTemperature.setAccessible(true);
                            Number temp = (Number) getTemperature.invoke(output);
                            currentData.setTemp(temp.intValue());

                            Method getTime = output.getClass().getDeclaredMethod("getTime");
                            getTime.setAccessible(true);
                            long timestamp = ((Number) getTime.invoke(output)).longValue();

                            if (lastTimeStamp != timestamp) {
                                currentData.setTimestamp(getTimestamp(timestamp));
                                lastTimeStamp = timestamp;
                                lastMillis = currentData.getTimestamp();
                            } else {
                                currentData.setTimestamp(lastMillis);
                            }

                            if (!currentData.equals(lastDataMap)) {
                                XposedBridge.log("Serial Update: " + currentData.output());


                                // @todo scale deviates and it may update very often
                                //if (lastDataMap == null || currentData.getWeight() == 0 || (currentData.getWeight() > 10 || currentData.timestamp != lastDataMap.timestamp)) {
                                //    sendMQTT(currentData, mqtt);
                                //}
                                sendMQTT(currentData, mqtt);

                                lastDataMap = currentData;
                            }

                        } catch (Throwable e) {
                            XposedBridge.log("Failed to extract data from SerialPortOutputEntity: " + e.getMessage());
                            XposedBridge.log(e);
                        }
                    }
                    return null;
                }
        );

        XposedHelpers.callMethod(listenerServiceInstance, "a", myListener);
    }

    public long getTimestamp(long input) {
        return input == 0 ? input : System.currentTimeMillis() + input;
    }

    private void sendMQTT(SerialData data, MqttHandler mqtt) {
        mqtt.sendPayload(data);
    }
}
