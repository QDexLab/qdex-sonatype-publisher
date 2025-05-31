package io.github.qdexlab.sonatypePublisher.plugin.utils;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class SonatypeApi {
    private static final String UPLOAD_URL = "https://central.sonatype.com/api/v1/publisher/upload";
    private static final OkHttpClient client = new OkHttpClient();
    private final String authorization;
    private final Logger logger;

    public SonatypeApi(String username, String password, Logger logger) {
        this.authorization = "Bearer " +
                new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        this.logger = logger;
    }

    public void upload(File file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("bundle", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/octet-stream")))
                .build();

        Request request = new Request.Builder()
                .url(
                        Objects.requireNonNull(HttpUrl.parse(UPLOAD_URL))
                                .newBuilder()
                                .addQueryParameter("name", FileNameUtils.getBaseName(file.getName()))
                                .addQueryParameter("publishingType", "AUTOMATIC")
                                .build()
                )
                .header("Authorization", authorization)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                logger.lifecycle("上传成功！");
                logger.lifecycle("deployment ID: " + Objects.requireNonNull(response.body()).string());
            } else {
                logger.error("上传失败: {} - {}", response.code(), response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
