package dcom.user_service.configuration.jwt;

import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class RsaKeyProvider {

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    public RsaKeyProvider() throws Exception {
        this.privateKey = loadPrivateKey("jwt_certs/private_key.pem");  // Keys in the resources folder
        this.publicKey = loadPublicKey("jwt_certs/public_key.pem");
    }

    // Load RSA private key from PEM file in resources
    public PrivateKey loadPrivateKey(String resourcePath) throws Exception {
        // Load the resource from the classpath (resources folder)
        ClassPathResource resource = new ClassPathResource(resourcePath);

        // Create a temporary file to store the private key
        Path tempFile = Files.createTempFile("temp", ".pem");
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Read the private key content and remove the PEM headers
        String key = getFormattedKey(tempFile, "private");

        // Decode the key and create the PrivateKey instance
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private String getFormattedKey(Path keyPath ,String keyType) {
        try{
            return Files.readString(keyPath)
                    .replace(String.format("-----BEGIN %s KEY-----", keyType.toUpperCase()), "")
                    .replace(String.format("-----END %s KEY-----", keyType.toUpperCase()), "")
                    .replaceAll("\\s", "");
        }
        catch (IOException e){
            throw new RuntimeException("Could not read key file");
        }
    }

    // Load RSA public key from PEM file in resources
    public PublicKey loadPublicKey(String resourcePath) throws Exception {
        // Load the resource from the classpath (resources folder)
        ClassPathResource resource = new ClassPathResource(resourcePath);

        // Create a temporary file to store the public key
        Path tempFile = Files.createTempFile("temp", ".pem");
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // Read the public key content and remove the PEM headers
        String key = getFormattedKey(tempFile, "public");


        // Decode the key and create the PublicKey instance
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }
}