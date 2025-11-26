package com.arquitectura.vehiculos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.Random;

@Service
public class VehiculoService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Simulación de llamada a SRI (RUC)
    public boolean validarRuc(String ruc) {
        // Aquí iría la llamada real REST al SRI
        // Simulamos que si el RUC termina en '001' es válido
        return ruc.endsWith("001");
    }

    // Lógica ANT con Patrón Cache-Aside
    public String obtenerPuntosAnt(String cedula) {
        String cacheKey = "ant_puntos_" + cedula;
        String puntos;

        try {
            // 1. Intentamos consultar la fuente original (ANT)
            puntos = invocarAntReal(cedula);

            // 2. Si responde bien, guardamos en caché (TTL 1 día)
            redisTemplate.opsForValue().set(cacheKey, puntos, 24, TimeUnit.HOURS);
            return puntos + " (Fuente: ANT en vivo)";

        } catch (RuntimeException e) {
            // 3. FALLO: La ANT está caída. Consultamos Caché.
            System.out.println("ANT caída. Buscando en caché...");
            puntos = redisTemplate.opsForValue().get(cacheKey);

            if (puntos != null) {
                return puntos + " (Fuente: Caché Resiliente)";
            } else {
                return "Servicio no disponible y sin datos históricos.";
            }
        }
    }

    // Simula la llamada inestable a la web de la ANT
    private String invocarAntReal(String cedula) {
        // Simulamos una falla aleatoria del 50% para probar tu arquitectura
        if (new Random().nextBoolean()) {
            throw new RuntimeException("Timeout conexión ANT");
        }
        return "30/30";
    }
}