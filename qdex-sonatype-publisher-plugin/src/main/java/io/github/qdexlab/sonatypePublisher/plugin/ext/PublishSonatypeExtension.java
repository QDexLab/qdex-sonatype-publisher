package io.github.qdexlab.sonatypePublisher.plugin.ext;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class PublishSonatypeExtension {

    private Sonatype sonatype;
    private Signing signing;
    private Pom pom;

    @Inject
    public PublishSonatypeExtension(ObjectFactory objectFactory) {
        sonatype = objectFactory.newInstance(Sonatype.class);
        signing = objectFactory.newInstance(Signing.class);
        pom = objectFactory.newInstance(Pom.class);
    }

    // 支持嵌套属性, 重要
    public void sonatype(Action<? super Sonatype> action) {
        action.execute(getSonatype());
    }

    public void signing(Action<? super Signing> action) {
        action.execute(getSigning());
    }

    public void pom(Action<? super Pom> action) {
        action.execute(getPom());
    }

    public Sonatype getSonatype() {
        return sonatype;
    }

    public void setSonatype(Sonatype sonatype) {
        this.sonatype = sonatype;
    }

    public Signing getSigning() {
        return signing;
    }

    public void setSigning(Signing signing) {
        this.signing = signing;
    }

    public Pom getPom() {
        return pom;
    }

    public void setPom(Pom pom) {
        this.pom = pom;
    }

    @Override
    public String toString() {
        return "PublishConfigExtension{" +
                "pom=" + pom +
                '}';
    }
}
