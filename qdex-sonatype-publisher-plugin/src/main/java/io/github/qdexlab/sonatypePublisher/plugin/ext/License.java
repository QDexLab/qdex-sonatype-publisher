package io.github.qdexlab.sonatypePublisher.plugin.ext;

import javax.inject.Inject;

public class License {
    private String name;
    private String licenseName;
    private String licenseUrl;

    @Inject
    public License(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    @Override
    public String toString() {
        return "License{" +
                "name='" + licenseName + '\'' +
                ", url='" + licenseUrl + '\'' +
                '}';
    }
}
