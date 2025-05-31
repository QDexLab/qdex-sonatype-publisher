package io.github.qdexlab.sonatypePublisher.plugin;

import io.github.qdexlab.sonatypePublisher.plugin.ext.Developer;
import io.github.qdexlab.sonatypePublisher.plugin.ext.License;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Pom;
import io.github.qdexlab.sonatypePublisher.plugin.ext.PublishSonatypeExtension;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Scm;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Signing;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Sonatype;
import io.github.qdexlab.sonatypePublisher.plugin.utils.SonatypeApi;
import io.github.qdexlab.sonatypePublisher.plugin.utils.ZipUtils;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

public class PublishSonatypePlugin implements Plugin<Project> {
    private static final String TASK_GROUP = "qdex sonatype publisher";
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

            project.getTasks().register("publishSonatype", task -> {
                task.setGroup(TASK_GROUP);
                task.dependsOn("clean", "publish");
                task.doLast(this::doPublish);
            });
        });
    }

    private void doPublish(Task task) {
        Project project = task.getProject();
        File zip = doCompress(project);
        doUpload(project, zip);
    }

    private void doUpload(Project project, File zip) {
        PublishSonatypeExtension extension = getPublishConfigExtension(project);
        Sonatype sonatype = extension.getSonatype();
        new SonatypeApi(sonatype, project.getLogger()).upload(zip);
    }

    private File doCompress(Project project) {
        File outputDir = getPublishOutputDir(project);
        // has and only has one sub dir
        File sourceFile = Objects.requireNonNull(outputDir.listFiles())[0];
        String zipFileName = project.getName() + "-" + project.getVersion();
        File zipFile = new File(outputDir.getPath() + "/" + zipFileName + ".zip");
        try {
            ZipUtils.zipDirectory(sourceFile, zipFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return zipFile;
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
            // signing.useGpgCmd();
            Signing signingExt = getPublishConfigExtension(project).getSigning();
            signing.useInMemoryPgpKeys(signingExt.getSecretKey(), signingExt.getPassword());
            PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
            signing.sign(publishing.getPublications().getByName("mavenJava"))
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
                    File file = getPublishOutputDir(project);
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

    private static File getPublishOutputDir(Project project) {
        String version = (String) project.getVersion();
        Provider<Directory> releases = project.getLayout().getBuildDirectory().dir("repos/releases");
        Provider<Directory> snapshots = project.getLayout().getBuildDirectory().dir("repos/snapshots");
        return version.endsWith("SNAPSHOT") ? snapshots.get().getAsFile() : releases.get().getAsFile();
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
