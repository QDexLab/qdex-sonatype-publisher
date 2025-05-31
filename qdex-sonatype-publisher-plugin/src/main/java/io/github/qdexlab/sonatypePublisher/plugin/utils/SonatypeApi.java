package io.github.qdexlab.sonatypePublisher.plugin.utils;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import io.github.qdexlab.sonatypePublisher.plugin.ext.Sonatype;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class SonatypeApi {
    private static final String UPLOAD_URL = "https://central.sonatype.com/api/v1/publisher/upload";
    private static final OkHttpClient client = new OkHttpClient();
    private final String authorization;
    private final Logger logger;

    public SonatypeApi(Sonatype sonatype, Logger logger) {
        String username = sonatype.getUsername();
        String password = sonatype.getPassword();
        this.authorization = "Bearer " +
                new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        this.logger = logger;
    }

    public void upload(File file) {
        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("bundle", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .build();

        Request request = new Request.Builder()
                .url(
                        HttpUrl.parse(UPLOAD_URL)
                                .newBuilder()
                                .addQueryParameter("name", FileNameUtils.getBaseName(file.getName()))
                                .addQueryParameter("publishingType", "AUTOMATIC")
                                .build()
                )
                .header("Authorization", authorization)
                .post(requestBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                logger.lifecycle("上传成功！");
                try (ResponseBody body = response.body()) {
                    logger.lifecycle("deployment ID: " + body.string());
                }
            } else {
                logger.error("上传失败: {} - {}", response.code(), response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
