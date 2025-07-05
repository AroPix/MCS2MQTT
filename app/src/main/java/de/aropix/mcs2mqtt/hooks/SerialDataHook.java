package de.aropix.mcs2mqtt.hooks;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import de.aropix.mcs2mqtt.MqttHandler;
import de.aropix.mcs2mqtt.SerialData;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SerialDataHook {

    private static SerialData lastDataMap = null;
    private static long lastTimeStamp = 0;
    private static long lastMillis = 0;

    public static void initHook(final XC_LoadPackage.LoadPackageParam lpparam, MqttHandler mqtt) {
        try {
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

        } catch (Throwable t) {
            XposedBridge.log("SerialDataHook init error: " + t.getMessage());
            XposedBridge.log(t);
        }
    }

    private static long getTimestamp(long input) {
        return input == 0 ? input : System.currentTimeMillis() + input;
    }

    private static void sendMQTT(SerialData data, MqttHandler mqtt) {
        String time;
        if (!Objects.equals(data.getTimeString(), "null")) {
            time = "\"" + data.getTimeString() + "\"";
        } else {
            time = data.getTimeString();
        }
        String payload = "{"
                + "\"temp\": " + data.getTemp() + ", "
                + "\"weight\": " + data.getWeight() + ", "
                + "\"time\": " + time + ", "
                + "\"speed\": " + data.getSpeed() + ", "
                + "\"running\": " + data.getIdleness()
                + "}";
        mqtt.sendPayload(payload, "mcs/data");
    }
}
