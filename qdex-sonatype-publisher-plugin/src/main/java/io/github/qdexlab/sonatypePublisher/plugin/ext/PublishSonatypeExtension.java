package io.github.qdexlab.sonatypePublisher.plugin.ext;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;

public class PublishSonatypeExtension {

    private Pom pom;

    @Inject
    public PublishSonatypeExtension(ObjectFactory objectFactory) {
        pom = objectFactory.newInstance(Pom.class);
    }

    // 支持嵌套属性, 重要
    public void pom(Action<? super Pom> action) {
        action.execute(getPom());
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
