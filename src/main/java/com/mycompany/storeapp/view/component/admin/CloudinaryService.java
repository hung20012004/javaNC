/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storeapp.view.component.admin;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
            
            // Tạo parameters cho upload
            Map<String, String> params = new HashMap<>();
            params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            params.put("upload_preset", "category_images"); // Hoặc sử dụng signed upload
            
            // Tạo signature (nếu sử dụng signed upload)
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
                
                // Add file
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                      .append(imageFile.getName()).append("\"\r\n");
                writer.append("Content-Type: ").append(getContentType(imageFile)).append("\r\n");
                writer.append("\r\n");
                writer.flush();
                
                // Write file data with progress tracking
                try (FileInputStream inputStream = new FileInputStream(imageFile)) {
                    byte[] buffer = new byte[4096];
                    long totalBytes = imageFile.length();
                    long uploadedBytes = 0;
                    int bytesRead;
                    
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        uploadedBytes += bytesRead;
                        
                        if (progressCallback != null) {
                            int progress = (int) ((uploadedBytes * 100) / totalBytes);
                            SwingUtilities.invokeLater(() -> progressCallback.onProgress(progress));
                        }
                    }
                }
                
                writer.append("\r\n");
                writer.append("--").append(boundary).append("--\r\n");
                writer.flush();
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
                    return parseImageUrl(response.toString());
                } else {
                    System.err.println("Upload failed: " + response.toString());
                    return null;
                }
            }
            
        } catch (Exception e) {
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
            // Sort parameters
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
                    return jsonResponse.substring(startIndex, endIndex);
                }
            }
            
            // Fallback: tìm "url" field
            searchKey = "\"url\":\"";
            startIndex = jsonResponse.indexOf(searchKey);
            if (startIndex != -1) {
                startIndex += searchKey.length();
                int endIndex = jsonResponse.indexOf("\"", startIndex);
                if (endIndex != -1) {
                    return jsonResponse.substring(startIndex, endIndex);
                }
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Interface cho progress callback
     */
    public interface ProgressCallback {
        void onProgress(int percentage);
    }
}