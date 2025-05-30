package io.github.qdexlab.sonatypePublisher.plugin.ext;

import javax.inject.Inject;

public class Developer {
    private String name;
    private String developerId;
    private String developerName;
    private String developerEmail;

    @Inject
    public Developer(String containerName) {
        this.name = containerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDeveloperEmail() {
        return developerEmail;
    }

    public void setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
    }

    @Override
    public String toString() {
        return "Developer{" +
                "id='" + developerId + '\'' +
                ", name='" + developerName + '\'' +
                ", email='" + developerEmail + '\'' +
                '}';
    }
}
