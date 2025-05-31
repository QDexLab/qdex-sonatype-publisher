package io.github.qdexlab.sonatypePublisher.plugin.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP压缩工具类(由deepseek生成)
 * 功能：
 * 1. 压缩单个文件
 * 2. 压缩目录（包含子目录）
 */
public class ZipUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * 压缩单个文件
     *
     * @param sourceFile 源文件
     * @param zipFile    目标ZIP文件
     * @throws IOException 压缩异常
     */
    public static void zipFile(File sourceFile, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            addToZip("", sourceFile, zos);
        }
    }

    /**
     * 压缩目录（保留顶层目录）
     *
     * @param sourceDir 源目录
     * @param zipFile   目标ZIP文件
     * @throws IOException 压缩异常
     */
    public static void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile.toPath()))) {
            addToZip("", sourceDir, zos);
        }
    }

    // ================ 私有方法 ================ //

    /**
     * 添加文件/目录到ZIP流
     *
     * @param basePath 基础路径
     * @param file     当前文件
     * @param zos      ZIP输出流
     */
    private static void addToZip(String basePath, File file, ZipOutputStream zos) throws IOException {
        String entryName = basePath + (basePath.isEmpty() || basePath.endsWith("/") ? "" : "/") + file.getName();

        if (file.isDirectory()) {
            // 处理目录（包括空目录）
            if (!entryName.endsWith("/")) entryName += "/";
            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry();

            // 递归处理子文件
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addToZip(entryName, child, zos);
                }
            }
        } else {
            // 处理文件
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("D:\\IdeaProjects\\qdex-sonatype-publisher\\qdex-sonatype-publisher-example\\build\\repos\\releases\\io");
        File zip = new File("D:\\IdeaProjects\\qdex-sonatype-publisher\\qdex-sonatype-publisher-example\\build\\repos\\releases\\zip.zip");
        zipDirectory(dir, zip);
    }
}