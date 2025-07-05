package de.aropix.mcs2mqtt;

public class Settings {
    String host = "localhost";
    String port = "1883";
    String user = "";
    String pass = "";
    Boolean showWelcomePopup = true;

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
}
