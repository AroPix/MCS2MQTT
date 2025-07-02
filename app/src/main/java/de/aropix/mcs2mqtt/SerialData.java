package de.aropix.mcs2mqtt;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SerialData {
    long timestamp;
    int speed;
    int temp;
    int weight;


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerialData)) return false;
        SerialData that = (SerialData) o;
        return weight == that.weight &&
                speed == that.speed &&
                temp == that.temp &&
                timestamp == that.timestamp;
    }

    public String output() {
        String output = "";
        output += "Temp: " + temp;
        output += "| Weight: " + weight;
        output += "| Speed: " + speed;
        output += "| Time: " + timestamp;

        return output;
    }

    public String getIdleness() {
        if (temp == 0 && speed == 0 && timestamp == 0) {
            return "false";
        } else {
            return "true";
        }
    }

    public String getTimeString() {
        String isoTime;
        if (timestamp == 0) {
            isoTime = "null";
        } else {
            isoTime = Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.of("Europe/Berlin"))
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return isoTime;
    }
}
