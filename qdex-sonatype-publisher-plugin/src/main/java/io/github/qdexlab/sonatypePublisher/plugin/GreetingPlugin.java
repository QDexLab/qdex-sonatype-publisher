package io.github.qdexlab.sonatypePublisher.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 *
 */
public class GreetingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getTasks().register("greeting", task -> {
            task.setGroup("qdexlab");
            System.out.println("GreetingPlugin Hello World!");
        });
    }
}
