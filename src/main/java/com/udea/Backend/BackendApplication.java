package com.udea.Backend;

/**
 * Clase principal de la aplicación Backend.
 * Esta clase arranca la aplicación Spring Boot.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    /**
     * Punto de entrada de la aplicación.
     * @param args argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
