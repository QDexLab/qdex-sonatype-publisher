package io.github.qdexlab.sonatypePublisher.plugin.ext;

public class Signing {
    private String secretKey;
    private String password;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
