package com.arquitectura.vehiculos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Para permitir React local
public class VehiculoController {

    @Autowired
    private VehiculoService service;

    @GetMapping("/consultar")
    public Map<String, Object> consultar(@RequestParam String ruc, @RequestParam String cedula, @RequestParam String placa) {
        Map<String, Object> respuesta = new HashMap<>();

        // 1. Validar SRI
        if (!service.validarRuc(ruc)) {
            respuesta.put("error", "RUC Inválido o no es contribuyente");
            return respuesta;
        }

        respuesta.put("contribuyente", "Persona Natural (Validado SRI)");
        respuesta.put("vehiculo", "Toyota Corolla - " + placa); // Simulado SRI Vehículos

        // 2. Obtener Puntos (Con resiliencia)
        String puntos = service.obtenerPuntosAnt(cedula);
        respuesta.put("licencia_puntos", puntos);

        return respuesta;
    }
}