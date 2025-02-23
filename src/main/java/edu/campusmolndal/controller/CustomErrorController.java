package edu.campusmolndal.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<Map<String, String>> handleError(HttpServletRequest request) {
        // Hämta HTTP-statuskod
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Map<String, String> response = new HashMap<>();

        // Standardstatus om ingen specifik status hittas
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (status != null) {
            // Konvertera statuskod
            int statusCode = Integer.parseInt(status.toString());
            httpStatus = HttpStatus.valueOf(statusCode);

            // Anpassade felmeddelanden baserat på statuskod
            switch (statusCode) {
                case 404:
                    response.put("error", "Resurs inte hittad");
                    break;
                case 401:
                    response.put("error", "Obehörig");
                    break;
                case 403:
                    response.put("error", "Förbjuden");
                    break;
                default:
                    response.put("error", "Ett oväntat fel inträffade");
                    break;
            }
        } else {
            response.put("error", "Ett oväntat fel inträffade");
        }

        // Returnera ResponseEntity med anpassad statuskod och felmeddelande
        return ResponseEntity.status(httpStatus).body(response);
    }
}