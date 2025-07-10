package com.mycompany.storeapp.service;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.*;

/**
 * Service để upload hình ảnh lên Cloudinary
 * @author Hi
 */
public class CloudinaryService {
    
    // Thay đổi các giá trị này theo config Cloudinary của bạn
    private static final String CLOUD_NAME = "deczn9jtq"; // Thay bằng cloud name của bạn
    private static final String API_KEY = "144834732335636"; // Thay bằng API key của bạn
    private static final String API_SECRET = "NcYKBF9NAcJO7cFfth7UGM0e4I0"; // Thay bằng API secret của bạn
    private static final String UPLOAD_URL = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";
    
    // Timeout settings
    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds
    
    /**
     * Upload file ảnh lên Cloudinary
     * @param imageFile File ảnh cần upload
     * @return URL của ảnh đã upload, null nếu thất bại
     */
    public static String uploadImage(File imageFile) {
        return uploadImage(imageFile, null);
    }
    
    /**
     * Upload file ảnh lên Cloudinary với progress callback
     * @param imageFile File ảnh cần upload
     * @param progressCallback Callback để cập nhật progress (có thể null)
     * @return URL của ảnh đã upload, null nếu thất bại
     */
    public static String uploadImage(File imageFile, ProgressCallback progressCallback) {
        try {
            // Validate file
            if (!isValidImageFile(imageFile)) {
                throw new IllegalArgumentException("File không phải là ảnh hợp lệ hoặc quá lớn (>10MB)");
            }
            
            // Update progress to 0%
            if (progressCallback != null) {
                SwingUtilities.invokeLater(() -> progressCallback.onProgress(0));
            }
            
            // Tạo parameters cho upload
            Map<String, String> params = new HashMap<>();
            params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            params.put("folder", "products"); // Organize images in folder
            
            // Tạo signature cho signed upload
            String signature = createSignature(params, API_SECRET);
            params.put("api_key", API_KEY);
            params.put("signature", signature);
            
            // Tạo multipart request
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            
            URL url = new URL(UPLOAD_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            
            // Update progress to 10%
            if (progressCallback != null) {
                SwingUtilities.invokeLater(() -> progressCallback.onProgress(10));
            }
            
            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {
                
                // Add parameters
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    writer.append("--").append(boundary).append("\r\n");
                    writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n");
                    writer.append("\r\n");
                    writer.append(entry.getValue()).append("\r\n");
                    writer.flush();
                }
                
                // Update progress to 20%
                if (progressCallback != null) {
                    SwingUtilities.invokeLater(() -> progressCallback.onProgress(20));
                }
                
                // Add file
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                      .append(imageFile.getName()).append("\"\r\n");
                writer.append("Content-Type: ").append(getContentType(imageFile)).append("\r\n");
                writer.append("\r\n");
                writer.flush();
                
                // Write file data with progress tracking
                try (FileInputStream inputStream = new FileInputStream(imageFile);
                     BufferedInputStream bufferedInput = new BufferedInputStream(inputStream)) {
                    
                    byte[] buffer = new byte[8192]; // Increased buffer size
                    long totalBytes = imageFile.length();
                    long uploadedBytes = 0;
                    int bytesRead;
                    
                    while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;
                        
                        if (progressCallback != null) {
                            // Progress from 20% to 90% during file upload
                            int progress = 20 + (int) ((uploadedBytes * 70) / totalBytes);
                            SwingUtilities.invokeLater(() -> progressCallback.onProgress(progress));
                        }
                    }
                }
                
                writer.append("\r\n");
                writer.append("--").append(boundary).append("--\r\n");
                writer.flush();
            }
            
            // Update progress to 95%
            if (progressCallback != null) {
                SwingUtilities.invokeLater(() -> progressCallback.onProgress(95));
            }
            
            // Read response
            int responseCode = connection.getResponseCode();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    responseCode >= 200 && responseCode < 300 ? 
                    connection.getInputStream() : connection.getErrorStream()))) {
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                if (responseCode >= 200 && responseCode < 300) {
                    String imageUrl = parseImageUrl(response.toString());
                    
                    // Update progress to 100%
                    if (progressCallback != null) {
                        SwingUtilities.invokeLater(() -> progressCallback.onProgress(100));
                    }
                    
                    return imageUrl;
                } else {
                    System.err.println("Upload failed with response code: " + responseCode);
                    System.err.println("Response: " + response.toString());
                    return null;
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error uploading image: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Kiểm tra file có phải là ảnh hợp lệ không
     */
    private static boolean isValidImageFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        
        // Check file size (max 10MB)
        if (file.length() > 10 * 1024 * 1024) {
            return false;
        }
        
        // Check file extension
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || fileName.endsWith(".gif") || 
               fileName.endsWith(".bmp") || fileName.endsWith(".webp");
    }
    
    /**
     * Lấy content type của file
     */
    private static String getContentType(File file) {
        try {
            String contentType = Files.probeContentType(file.toPath());
            return contentType != null ? contentType : "image/jpeg";
        } catch (Exception e) {
            return "image/jpeg";
        }
    }
    
    /**
     * Tạo signature cho Cloudinary API
     */
    private static String createSignature(Map<String, String> params, String apiSecret) {
        try {
            // Sort parameters (exclude api_key and signature)
            TreeMap<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.remove("api_key");
            sortedParams.remove("signature");
            
            // Create query string
            StringBuilder queryString = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }
            queryString.append(apiSecret);
            
            // Create SHA1 hash
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(queryString.toString().getBytes("UTF-8"));
            
            // Convert to hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            System.err.println("Error creating signature: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
    
    /**
     * Parse URL từ response JSON của Cloudinary
     */
    private static String parseImageUrl(String jsonResponse) {
        try {
            // Simple JSON parsing - tìm "secure_url" field
            String searchKey = "\"secure_url\":\"";
            int startIndex = jsonResponse.indexOf(searchKey);
            if (startIndex != -1) {
                startIndex += searchKey.length();
                int endIndex = jsonResponse.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    String url = jsonResponse.substring(startIndex, endIndex);
                    // Unescape the URL
                    return url.replace("\\/", "/");
                }
            }
            
            // Fallback: tìm "url" field
            searchKey = "\"url\":\"";
            startIndex = jsonResponse.indexOf(searchKey);
            if (startIndex != -1) {
                startIndex += searchKey.length();
                int endIndex = jsonResponse.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    String url = jsonResponse.substring(startIndex, endIndex);
                    // Unescape the URL
                    return url.replace("\\/", "/");
                }
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing image URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Delete image from Cloudinary
     * @param publicId Public ID của ảnh cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public static boolean deleteImage(String publicId) {
        try {
            String deleteUrl = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/destroy";
            
            Map<String, String> params = new HashMap<>();
            params.put("public_id", publicId);
            params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            
            String signature = createSignature(params, API_SECRET);
            params.put("api_key", API_KEY);
            params.put("signature", signature);
            
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            
            URL url = new URL(deleteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            
            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true)) {
                
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    writer.append("--").append(boundary).append("\r\n");
                    writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n");
                    writer.append("\r\n");
                    writer.append(entry.getValue()).append("\r\n");
                    writer.flush();
                }
                
                writer.append("--").append(boundary).append("--\r\n");
                writer.flush();
            }
            
            int responseCode = connection.getResponseCode();
            return responseCode >= 200 && responseCode < 300;
            
        } catch (Exception e) {
            System.err.println("Error deleting image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extract public ID from Cloudinary URL
     * @param cloudinaryUrl URL của ảnh trên Cloudinary
     * @return Public ID, null nếu không tìm thấy
     */
    public static String extractPublicId(String cloudinaryUrl) {
        try {
            if (cloudinaryUrl == null || !cloudinaryUrl.contains("cloudinary.com")) {
                return null;
            }
            
            // Format: https://res.cloudinary.com/[cloud_name]/image/upload/v[version]/[public_id].[format]
            String[] parts = cloudinaryUrl.split("/");
            if (parts.length >= 7) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension
                int dotIndex = lastPart.lastIndexOf('.');
                if (dotIndex > 0) {
                    return "products/" + lastPart.substring(0, dotIndex);
                }
                return "products/" + lastPart;
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error extracting public ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate transformation URL for image resizing
     * @param originalUrl URL gốc của ảnh
     * @param width Chiều rộng mong muốn
     * @param height Chiều cao mong muốn
     * @return URL với transformation, null nếu thất bại
     */
    public static String getResizedImageUrl(String originalUrl, int width, int height) {
        try {
            if (originalUrl == null || !originalUrl.contains("cloudinary.com")) {
                return originalUrl;
            }
            
            // Insert transformation parameters
            String transformation = String.format("w_%d,h_%d,c_fill", width, height);
            return originalUrl.replace("/upload/", "/upload/" + transformation + "/");
            
        } catch (Exception e) {
            System.err.println("Error generating resized URL: " + e.getMessage());
            return originalUrl;
        }
    }
    
    /**
     * Interface cho progress callback
     */
    public interface ProgressCallback {
        void onProgress(int percentage);
    }
}