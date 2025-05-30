package io.github.qdexlab.sonatypePublisher.plugin.ext;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class Pom {
    private String name;
    private String description;
    private String url;
    private NamedDomainObjectContainer<License> licenses;
    private NamedDomainObjectContainer<Developer> developers;
    private Scm scm;

    @Inject
    public Pom(ObjectFactory objects) {
        licenses = objects.domainObjectContainer(License.class, name -> objects.newInstance(License.class, name));
        developers = objects.domainObjectContainer(Developer.class, name -> objects.newInstance(Developer.class, name));
        scm = objects.newInstance(Scm.class);
    }

    public void scm(Action<? super Scm> action) {
        action.execute(getScm());
    }

    public void licenses(Action<? super NamedDomainObjectContainer<License>> action) {
        action.execute(getLicenses());
    }

    public void developers(Action<? super NamedDomainObjectContainer<Developer>> action) {
        action.execute(getDevelopers());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public NamedDomainObjectContainer<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(NamedDomainObjectContainer<License> licenses) {
        this.licenses = licenses;
    }

    public NamedDomainObjectContainer<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(NamedDomainObjectContainer<Developer> developers) {
        this.developers = developers;
    }

    public Scm getScm() {
        return scm;
    }

    public void setScm(Scm scm) {
        this.scm = scm;
    }

    @Override
    public String toString() {
        return "Pom{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", licenses=" + licenses +
                ", developers=" + developers +
                ", scm=" + scm +
                '}';
    }
}
