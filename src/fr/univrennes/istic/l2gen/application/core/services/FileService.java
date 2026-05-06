package fr.univrennes.istic.l2gen.application.core.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class FileService {

    public static File getAppDataDir() {
        File dir = new File(System.getProperty("user.home"), ".Pangol1");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getExtension(File file) {
        String name = file.getName().toLowerCase().trim();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }

    public static String getRemoteFileName(HttpURLConnection connection, URL url) {
        String disposition = connection.getHeaderField("Content-Disposition");

        if (disposition != null) {
            for (String part : disposition.split(";")) {
                String trimmedPart = part.trim();
                if (trimmedPart.startsWith("filename*=")) {
                    String encodedValue = trimmedPart.substring("filename*=".length()).trim();
                    if (encodedValue.toUpperCase().startsWith("UTF-8''")) {
                        try {
                            return URLDecoder.decode(encodedValue.substring(7), StandardCharsets.UTF_8);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                if (trimmedPart.startsWith("filename=")) {
                    return trimmedPart.substring("filename=".length())
                            .replace("\"", "")
                            .trim();
                }
            }
        }

        String urlPath = url.getPath();
        String fileName = urlPath.substring(urlPath.lastIndexOf('/') + 1);

        if (!fileName.contains(".")) {
            String extensionFromContentType = getExtensionFromContentType(connection.getContentType());
            if (extensionFromContentType != null) {
                return fileName.isEmpty()
                        ? "file_" + System.currentTimeMillis() + extensionFromContentType
                        : fileName + extensionFromContentType;
            }
        }

        return fileName.isEmpty() ? "file_" + System.currentTimeMillis() + ".csv" : fileName;
    }

    private static String getExtensionFromContentType(String contentType) {
        if (contentType == null)
            return null;

        Map<String, String> extensionByMimeType = Map.of(
                "text/csv", ".csv",
                "text/tsv", ".tsv",
                "application/csv", ".csv",
                "text/plain", ".txt",
                "application/octet-stream", ".parquet",
                "application/vnd.apache.parquet", ".parquet",
                "application/zip", ".zip",
                "application/x-zip-compressed", ".zip");

        String normalizedContentType = contentType.split(";")[0].trim().toLowerCase();

        return extensionByMimeType.get(normalizedContentType);
    }
}