package io.github.qdexlab.sonatypePublisher.plugin.ext;

public class Scm {
    private String connection;
    private String developerConnection;
    private String url;

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getDeveloperConnection() {
        return developerConnection;
    }

    public void setDeveloperConnection(String developerConnection) {
        this.developerConnection = developerConnection;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Scm{" +
                "connection='" + connection + '\'' +
                ", developerConnection='" + developerConnection + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
