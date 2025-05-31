package io.github.qdexlab.sonatypePublisher.plugin.utils;

public class FileNameUtils {

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 完整文件名
     * @return 不带扩展名的文件名
     */
    public static String getBaseName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        // 处理隐藏文件（以点开头）
        if (fileName.startsWith(".")) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot == 0) {
                return fileName; // 只有开头的点（如 ".gitignore"）
            }
            return fileName.substring(0, lastDot);
        }

        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex <= 0) ? fileName : fileName.substring(0, dotIndex);
    }
}
