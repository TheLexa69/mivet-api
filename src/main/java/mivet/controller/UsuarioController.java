package mivet.controller;

import mivet.dto.UsuarioDTO;
import mivet.model.Usuario;
import mivet.service.UsuarioService;
import mivet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Aqui iran:
    // ME FALTA ALGO AQUI QUE NO ME ACUERDO QUE IBA [M3RD4444!!!!]
    // - GET /mascotas
    // - POST /mascotas
    // - GET /citas
    // - POST /citas
    // - GASTOS
    // AJUSTES
    // - GET /perfil
    // - PUT /perfil
    // - PUT /cambiar-contrasena
    // - GET/PUT /notificaciones


    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfilUsuario(@RequestHeader("Authorization") String token) {
        try {
            validarPrivado(token); // Validate token and user type

            Long idUsuario = JwtUtil.extractUserId(token);
            Optional<Usuario> optionalUsuario = usuarioService.findById(idUsuario);

            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();

            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(usuario.getId());
            dto.setNombre(usuario.getNombre());
            dto.setCorreo(usuario.getCorreo());
            dto.setRol(usuario.getRol());
            dto.setTipoUsuario(usuario.getTipoUsuario().name());

            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfilUsuario(@RequestHeader("Authorization") String token,
                                                     @RequestBody UsuarioDTO dto) {
        try {
            validarPrivado(token); // Validate token and user type

            Long idUsuario = JwtUtil.extractUserId(token);
            Optional<Usuario> optionalUsuario = usuarioService.findById(idUsuario);

            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();

            if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
            if (dto.getCorreo() != null) usuario.setCorreo(dto.getCorreo());
            // Note: Changing password, role, or tipo_usuario is not allowed here

            usuarioService.save(usuario);

            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    private void validarPrivado(String token) {
        if (!JwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Token expirado o inválido");
        }

        String tipoUsuario = JwtUtil.extractTipoUsuario(token);
        if (!"privado".equalsIgnoreCase(tipoUsuario)) {
            throw new RuntimeException("No tiene permisos para acceder a este recurso");
        }
    }

}
