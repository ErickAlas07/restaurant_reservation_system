package com.ealas.restaurant_reservation_system;

import com.ealas.restaurant_reservation_system.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Datos {
    private static Map<String, User> usuariosDB = new HashMap<>();

    static {
        // Simular un usuario con todos los campos relevantes
        User usuarioTest = new User();
        usuarioTest.setUsername("usuarioTest");
        usuarioTest.setPassword("password");
        usuarioTest.setEmail("test@example.com");
        usuarioTest.setEnabled(true);
        usuarioTest.setRoles(new ArrayList<>());  // Simular roles si es necesario
        usuariosDB.put(usuarioTest.getUsername(), usuarioTest);
    }

    // Método para simular la búsqueda de un usuario por su nombre de usuario
    public static Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usuariosDB.get(username));
    }
}
