package edu.campusmolndal;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // Kontrollera om applikationen startas med generate-key argument
        if (args.length > 0 && args[0].equals("generate-key")) {
            generateKey();
            return;
        }

        // Ladda miljövariabler från .env-filen
        Dotenv dotenv = Dotenv.load();

        // Kontrollera kritiska miljövariabler
        CheckProperty("JWT_SECRET", "your_jwt_secret");
        CheckProperty("GOOGLE_CLIENT_ID", "your_google_client_id");
        CheckProperty("GOOGLE_CLIENT_SECRET", "your_google_client_secret");

        // Skriv ut konfigurerade nycklar (endast för debugging)
        System.out.println("JWT_SECRET: " + System.getProperty("JWT_SECRET"));
        System.out.println("GOOGLE_CLIENT_ID: " + System.getProperty("GOOGLE_CLIENT_ID"));
        System.out.println("GOOGLE_CLIENT_SECRET: " + System.getProperty("GOOGLE_CLIENT_SECRET"));

        // Starta Spring Boot-applikationen
        SpringApplication.run(Application.class, args);
    }

    // Metod för att kontrollera kritiska miljövariabler
    private static void CheckProperty(String key, String defaultValue) {
        Dotenv dotenv = Dotenv.load();
        var secret = dotenv.get(key);
        if (secret == null || secret.equals(defaultValue)) {
            System.out.println("Please set " + key + " in .env file or as a system variable");
            System.exit(1);
        }
        System.setProperty(key, secret);
    }


    // Metod för att generera säker JWT-nyckel
    public static void generateKey() {
        // Använd SecureRandom för kryptografiskt säker slumpgenerering
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        // Koda nyckeln i Base64 för säker lagring
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println(encodedKey);
    }
}