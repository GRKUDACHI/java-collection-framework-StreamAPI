import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

/**
 * Java 8 Base64 Encoding and Decoding Tutorial
 * 
 * Base64 is a binary-to-text encoding scheme that represents binary data
 * in an ASCII string format using 64 different characters (A-Z, a-z, 0-9, +, /)
 * 
 * Main Theme: Converting binary data to text format for safe transmission/storage
 * 
 * When to use Base64:
 * 1. Email attachments (MIME)
 * 2. Data URLs in web applications
 * 3. Storing binary data in JSON/XML
 * 4. HTTP Basic Authentication
 * 5. Cryptographic operations
 * 6. Database storage of binary data
 */
public class Base64Tutorial {
    
    public static void main(String[] args) {
        System.out.println("=== Java 8 Base64 Encoding & Decoding Tutorial ===\n");
        
        // Basic String Encoding/Decoding
        basicStringEncoding();
        
        // Different Base64 Types
        differentBase64Types();
        
        // URL and Filename Safe Encoding
        urlSafeEncoding();
        
        // MIME Type Encoding
        mimeEncoding();
        
        // Practical Examples
        practicalExamples();
        
        // Performance Considerations
        performanceConsiderations();
    }
    
    /**
     * Basic String Encoding and Decoding
     */
    public static void basicStringEncoding() {
        System.out.println("1. BASIC STRING ENCODING/DECODING");
        System.out.println("=================================");
        
        String originalText = "Hello, Java 8 Base64!";
        System.out.println("Original Text: " + originalText);
        
        // Encode to Base64
        String encodedText = Base64.getEncoder().encodeToString(originalText.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encoded Text: " + encodedText);
        
        // Decode from Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedText);
        String decodedText = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("Decoded Text: " + decodedText);
        
        System.out.println("Match: " + originalText.equals(decodedText));
        System.out.println();
    }
    
    /**
     * Different Types of Base64 Encoding
     */
    public static void differentBase64Types() {
        System.out.println("2. DIFFERENT BASE64 TYPES");
        System.out.println("=========================");
        
        String data = "Java 8 Base64 Tutorial";
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        
        // Standard Base64 Encoder
        Base64.Encoder standardEncoder = Base64.getEncoder();
        String standardEncoded = standardEncoder.encodeToString(dataBytes);
        System.out.println("Standard Encoded: " + standardEncoded);
        
        // URL and Filename Safe Encoder
        Base64.Encoder urlSafeEncoder = Base64.getUrlEncoder();
        String urlSafeEncoded = urlSafeEncoder.encodeToString(dataBytes);
        System.out.println("URL Safe Encoded: " + urlSafeEncoded);
        
        // MIME Encoder (with line breaks)
        Base64.Encoder mimeEncoder = Base64.getMimeEncoder();
        String mimeEncoded = mimeEncoder.encodeToString(dataBytes);
        System.out.println("MIME Encoded: " + mimeEncoded);
        
        System.out.println();
    }
    
    /**
     * URL and Filename Safe Encoding
     */
    public static void urlSafeEncoding() {
        System.out.println("3. URL AND FILENAME SAFE ENCODING");
        System.out.println("==================================");
        
        String urlData = "https://example.com/path?param=value&other=data";
        System.out.println("Original URL: " + urlData);
        
        // URL Safe encoding (replaces + with -, / with _, removes padding =)
        String urlSafeEncoded = Base64.getUrlEncoder().encodeToString(urlData.getBytes(StandardCharsets.UTF_8));
        System.out.println("URL Safe Encoded: " + urlSafeEncoded);
        
        // Decode URL Safe
        byte[] decodedBytes = Base64.getUrlDecoder().decode(urlSafeEncoded);
        String decodedUrl = new String(decodedBytes, StandardCharsets.UTF_8);
        System.out.println("Decoded URL: " + decodedUrl);
        
        System.out.println();
    }
    
    /**
     * MIME Type Encoding with Line Breaks
     */
    public static void mimeEncoding() {
        System.out.println("4. MIME TYPE ENCODING");
        System.out.println("=====================");
        
        String longText = "This is a very long text that will be encoded using MIME Base64 " +
                         "which automatically adds line breaks every 76 characters for " +
                         "better readability and email compatibility.";
        
        System.out.println("Original Text Length: " + longText.length());
        
        // MIME encoding with default line length (76)
        String mimeEncoded = Base64.getMimeEncoder().encodeToString(longText.getBytes(StandardCharsets.UTF_8));
        System.out.println("MIME Encoded (76 chars per line):");
        System.out.println(mimeEncoded);
        
        // MIME encoding with custom line length
        String mimeEncodedCustom = Base64.getMimeEncoder(50, new byte[]{'\n'})
                                        .encodeToString(longText.getBytes(StandardCharsets.UTF_8));
        System.out.println("\nMIME Encoded (50 chars per line):");
        System.out.println(mimeEncodedCustom);
        
        System.out.println();
    }
    
    /**
     * Practical Examples
     */
    public static void practicalExamples() {
        System.out.println("5. PRACTICAL EXAMPLES");
        System.out.println("=====================");
        
        // Example 1: HTTP Basic Authentication
        httpBasicAuthExample();
        
        // Example 2: Data URL for Images
        dataUrlExample();
        
        // Example 3: JSON with Binary Data
        jsonWithBinaryDataExample();
        
        // Example 4: File Content Encoding
        fileContentExample();
    }
    
    /**
     * HTTP Basic Authentication Example
     */
    public static void httpBasicAuthExample() {
        System.out.println("a) HTTP Basic Authentication:");
        String username = "admin";
        String password = "secret123";
        String credentials = username + ":" + password;
        
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Encoded Credentials: " + encodedCredentials);
        System.out.println("Authorization Header: Basic " + encodedCredentials);
        
        // Decode to verify
        String decodedCredentials = new String(Base64.getDecoder().decode(encodedCredentials), StandardCharsets.UTF_8);
        System.out.println("Decoded Credentials: " + decodedCredentials);
        System.out.println();
    }
    
    /**
     * Data URL Example
     */
    public static void dataUrlExample() {
        System.out.println("b) Data URL Example:");
        String imageData = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        String dataUrl = "data:image/png;base64," + imageData;
        System.out.println("Data URL: " + dataUrl);
        System.out.println("This can be used directly in HTML: <img src='" + dataUrl + "'>");
        System.out.println();
    }
    
    /**
     * JSON with Binary Data Example
     */
    public static void jsonWithBinaryDataExample() {
        System.out.println("c) JSON with Binary Data:");
        String binaryData = "Hello, this is binary data!";
        String encodedData = Base64.getEncoder().encodeToString(binaryData.getBytes(StandardCharsets.UTF_8));
        
        String json = "{\n" +
                     "  \"name\": \"Document\",\n" +
                     "  \"type\": \"pdf\",\n" +
                     "  \"content\": \"" + encodedData + "\"\n" +
                     "}";
        System.out.println("JSON with Base64 encoded content:");
        System.out.println(json);
        System.out.println();
    }
    
    /**
     * File Content Example
     */
    public static void fileContentExample() {
        System.out.println("d) File Content Encoding:");
        String fileContent = "This is the content of a file.\nIt has multiple lines.\nAnd special characters: @#$%^&*()";
        System.out.println("Original File Content:");
        System.out.println(fileContent);
        
        String encodedContent = Base64.getEncoder().encodeToString(fileContent.getBytes(StandardCharsets.UTF_8));
        System.out.println("\nEncoded File Content:");
        System.out.println(encodedContent);
        
        // Simulate storing in database or sending over network
        System.out.println("\nSimulating storage/transmission...");
        
        // Decode when retrieving
        String decodedContent = new String(Base64.getDecoder().decode(encodedContent), StandardCharsets.UTF_8);
        System.out.println("Decoded File Content:");
        System.out.println(decodedContent);
        System.out.println();
    }
    
    /**
     * Performance Considerations
     */
    public static void performanceConsiderations() {
        System.out.println("6. PERFORMANCE CONSIDERATIONS");
        System.out.println("=============================");
        
        String largeData = "This is a large string that we want to encode. ".repeat(1000);
        System.out.println("Large data size: " + largeData.length() + " characters");
        
        // Measure encoding time
        long startTime = System.nanoTime();
        String encoded = Base64.getEncoder().encodeToString(largeData.getBytes(StandardCharsets.UTF_8));
        long encodingTime = System.nanoTime() - startTime;
        
        // Measure decoding time
        startTime = System.nanoTime();
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        long decodingTime = System.nanoTime() - startTime;
        
        System.out.println("Encoding time: " + encodingTime / 1_000_000.0 + " ms");
        System.out.println("Decoding time: " + decodingTime / 1_000_000.0 + " ms");
        System.out.println("Encoded size: " + encoded.length() + " characters");
        System.out.println("Size increase: " + String.format("%.1f", (encoded.length() * 100.0 / largeData.length())) + "%");
        
        System.out.println("\nBest Practices:");
        System.out.println("- Use URL-safe encoding for URLs and filenames");
        System.out.println("- Use MIME encoding for email attachments");
        System.out.println("- Consider performance for large data sets");
        System.out.println("- Always specify charset (UTF-8) for text data");
        System.out.println("- Base64 increases data size by ~33%");
    }
    
    /**
     * Utility method to demonstrate error handling
     */
    public static void demonstrateErrorHandling() {
        System.out.println("\n7. ERROR HANDLING");
        System.out.println("=================");
        
        try {
            // Invalid Base64 string
            String invalidBase64 = "This is not valid Base64!";
            Base64.getDecoder().decode(invalidBase64);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
            System.out.println("Always wrap Base64 operations in try-catch blocks!");
        }
    }
}

/**
 * Key Points Summary:
 * 
 * 1. WHAT IS BASE64?
 *    - Binary-to-text encoding scheme
 *    - Uses 64 characters: A-Z, a-z, 0-9, +, /
 *    - Padding with '=' characters
 * 
 * 2. WHEN TO USE BASE64?
 *    - Email attachments (MIME)
 *    - Data URLs in web applications
 *    - Storing binary data in JSON/XML
 *    - HTTP Basic Authentication
 *    - Cryptographic operations
 *    - Database storage of binary data
 * 
 * 3. JAVA 8 BASE64 CLASSES:
 *    - Base64.getEncoder() - Standard encoding
 *    - Base64.getUrlEncoder() - URL-safe encoding
 *    - Base64.getMimeEncoder() - MIME encoding with line breaks
 * 
 * 4. MAIN THEME:
 *    - Converting binary data to text format for safe transmission/storage
 *    - Ensuring data integrity across different systems
 *    - Making binary data compatible with text-based protocols
 */
