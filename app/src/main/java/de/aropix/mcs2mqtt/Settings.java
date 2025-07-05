package de.aropix.mcs2mqtt;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {
    String host = "localhost";
    String port = "1883";
    String user = "";
    String pass = "";
    Boolean showWelcomePopup = true;
    Boolean sendRecipeToMQTT = true;

    public Boolean getSendRecipeToMQTT() {
        return sendRecipeToMQTT;
    }

    public void setSendRecipeToMQTT(Boolean sendRecipeToMQTT) {
        this.sendRecipeToMQTT = sendRecipeToMQTT;
    }


    public Boolean getShowWelcomePopup() {
        return showWelcomePopup;
    }

    public void setShowWelcomePopup(Boolean showWelcomePopup) {
        this.showWelcomePopup = showWelcomePopup;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String toJsonString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("host", host);
            obj.put("port", port);
            obj.put("user", user);
            obj.put("pass", pass);
            obj.put("showWelcomePopup", showWelcomePopup);
            obj.put("sendRecipeToMQTT", sendRecipeToMQTT);
            return obj.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
