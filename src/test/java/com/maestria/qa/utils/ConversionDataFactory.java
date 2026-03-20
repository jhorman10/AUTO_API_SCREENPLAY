package com.maestria.qa.utils;

import com.google.gson.JsonObject;
import java.util.UUID;
import java.util.Random;

public class ConversionDataFactory {
    
    private static final String[] CONVERSION_TYPES = {"venta", "descarga", "suscripcion", "otros"};
    private static final String[] CONVERSION_STATES = {"pendiente", "completada", "cancelada"};
    private static final Random random = new Random();
    
    /**
     * Genera un JSON válido para crear una conversión vía POST
     */
    public static JsonObject generateValidConversionData() {
        JsonObject conversion = new JsonObject();
        conversion.addProperty("user_id", generateUserId());
        conversion.addProperty("tipo", getRandomType());
        conversion.addProperty("valor", generateRandomValue());
        conversion.addProperty("estado", "pendiente");
        return conversion;
    }
    
    /**
     * Genera datos de conversión con tipo y valor específicos
     */
    public static JsonObject generateConversionWithTypeAndValue(String tipo, double valor) {
        JsonObject conversion = new JsonObject();
        conversion.addProperty("user_id", generateUserId());
        conversion.addProperty("tipo", tipo);
        conversion.addProperty("valor", valor);
        conversion.addProperty("estado", "pendiente");
        return conversion;
    }
    
    /**
     * Genera datos para actualizar una conversión
     */
    public static JsonObject generateConversionUpdate(String nuevoEstado, Double nuevoValor) {
        JsonObject update = new JsonObject();
        if (nuevoEstado != null) {
            update.addProperty("estado", nuevoEstado);
        }
        if (nuevoValor != null) {
            update.addProperty("valor", nuevoValor);
        }
        return update;
    }
    
    /**
     * Genera un user_id único
     */
    public static String generateUserId() {
        return "user_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Obtiene un tipo de conversión aleatorio
     */
    private static String getRandomType() {
        return CONVERSION_TYPES[random.nextInt(CONVERSION_TYPES.length)];
    }
    
    /**
     * Genera un valor aleatorio entre 1.0 y 1000.0
     */
    private static double generateRandomValue() {
        return 1.0 + (1000.0 - 1.0) * random.nextDouble();
    }
    
    /**
     * Genera un valor negativo para pruebas de validación
     */
    public static double generateNegativeValue() {
        return -(random.nextDouble() * 100);
    }
    
    /**
     * Genera un UUID inválido para pruebas de error
     */
    public static String generateInvalidId() {
        return "invalid-id-" + System.currentTimeMillis();
    }
}
