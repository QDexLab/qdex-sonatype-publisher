package io.github.qdexlab.sonatypePublisher.plugin;

import io.github.qdexlab.sonatypePublisher.plugin.ext.Developer;
import io.github.qdexlab.sonatypePublisher.plugin.ext.License;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Pom;
import io.github.qdexlab.sonatypePublisher.plugin.ext.PublishSonatypeExtension;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Scm;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.plugins.signing.SigningExtension;

import java.io.File;
import java.net.MalformedURLException;

public class PublishSonatypePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        applyPlugin(project, "java");
        applyPlugin(project, "java-library");
        applyPlugin(project, "maven-publish");
        applyPlugin(project, "signing");
        // 创建扩展配置
        project.getExtensions().create("publishSonatype", PublishSonatypeExtension.class, project.getObjects());

        project.afterEvaluate(p -> {
            configureJavaPluginExtension(project);
            configurePublishingExtension(project);
            configureSigningExtension(project);
            configureJavadocTask(project);

            project.getTasks().register("bundleProducts", task -> {
                task.setGroup("qdex-sonatype-publisher");
                task.dependsOn("publish");
            });
            project.getTasks().register("uploadSonatype", task -> {
                task.setGroup("qdex-sonatype-publisher");
            });
        });
    }

    private void configureJavadocTask(Project project) {
        if (JavaVersion.current().isJava9Compatible()) {
            Javadoc javadoc = (Javadoc) project.getTasks().getByName("javadoc");
            javadoc.options(options -> {
                ((StandardJavadocDocletOptions) options).addBooleanOption("html5", true);
            });
        }
    }

    private void configureSigningExtension(Project project) {
        project.getExtensions().configure(SigningExtension.class, signing -> {
            signing.useGpgCmd();
            PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
            signing.sign(publishing.getPublications().getByName("mavenJava"));
        });
    }

    private void configureJavaPluginExtension(Project project) {
        project.getExtensions().configure(JavaPluginExtension.class, java -> {
            java.withSourcesJar();
            java.withJavadocJar();
        });
    }

    private void configurePublishingExtension(Project project) {
        project.getExtensions().configure(PublishingExtension.class, publishing -> {
            PublishSonatypeExtension config = getPublishConfigExtension(project);
            // 配置发布仓库
            publishing.repositories(repositories -> {
                // 本地仓库（测试用）
                repositories.mavenLocal();
                // 项目目录（示例）
                repositories.maven(maven -> {
                    String version = (String) project.getVersion();
                    Provider<Directory> releases = project.getLayout().getBuildDirectory().dir("repos/releases");
                    Provider<Directory> snapshots = project.getLayout().getBuildDirectory().dir("repos/snapshots");
                    File file = version.endsWith("SNAPSHOT") ? snapshots.get().getAsFile() : releases.get().getAsFile();
                    try {
                        maven.setUrl(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
            // 配置发布内容
            publishing.publications(publications -> {
                // 创建 Maven 发布
                publications.create("mavenJava", MavenPublication.class, pub -> {
                    // 从 Java 组件发布
                    pub.from(project.getComponents().findByName("java"));
                    // 自定义 artifactId（可选）
                    pub.setArtifactId(project.getName());
                    // 添加 POM 信息
                    pub.pom(pom -> {
                        Pom p = config.getPom();
                        pom.getName().set(p.getName());
                        pom.getDescription().set(p.getDescription());
                        pom.getUrl().set(p.getUrl());
                        for (License license : p.getLicenses()) {
                            pom.licenses(licenses -> {
                                licenses.license(l -> {
                                    l.getName().set(license.getLicenseName());
                                    l.getUrl().set(license.getLicenseUrl());
                                });
                            });
                        }
                        for (Developer developer : p.getDevelopers()) {
                            pom.developers(devs -> {
                                devs.developer(dev -> {
                                    dev.getId().set(developer.getDeveloperId());
                                    dev.getName().set(developer.getDeveloperName());
                                    dev.getEmail().set(developer.getDeveloperEmail());
                                });
                            });
                        }
                        pom.scm(scm -> {
                            Scm s = p.getScm();
                            scm.getConnection().set(s.getConnection());
                            scm.getDeveloperConnection().set(s.getDeveloperConnection());
                            scm.getUrl().set(s.getUrl());
                        });
                    });
                });
            });
        });
    }

    private PublishSonatypeExtension getPublishConfigExtension(Project project) {
        return project.getExtensions().getByType(PublishSonatypeExtension.class);
    }

    private void applyPlugin(Project project, String pluginId) {
        PluginManager pluginManager = project.getPluginManager();
        if (!pluginManager.hasPlugin(pluginId)) {
            pluginManager.apply(pluginId);
        }
    }
}
