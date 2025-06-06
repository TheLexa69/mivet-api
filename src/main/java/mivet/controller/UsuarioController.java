package mivet.controller;

import mivet.dto.CambioContrasenaDTO;
import mivet.dto.CitaDTO;
import mivet.dto.MascotaDTO;
import mivet.dto.UsuarioDTO;
import mivet.model.Cita;
import mivet.model.Mascota;
import mivet.model.Usuario;
import mivet.service.CitaService;
import mivet.service.MascotaService;
import mivet.service.UsuarioService;
import mivet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MascotaService mascotaService;
    private final CitaService citaService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, MascotaService mascotaService,
                             CitaService citaService) {
        this.usuarioService = usuarioService;
        this.mascotaService = mascotaService;
        this.citaService = citaService;
    }

    // Aqui iran:
    // ME FALTA ALGO AQUI QUE NO ME ACUERDO QUE IBA [M3RD4444!!!!]
    // ✔ - GET /mascotas
    // ✔ - PUT /mascotas/{id}
    // ✔ - POST /mascotas CONTROLAR TIPO MASCOTA EN MINUSCULO
    // ✔ - GET /citas
    // ✔ - PUT /citas/{id}
    // ✔ - DELETE /citas/{id}
    // ✔ - POST /citas
    // - GASTOS
    // ---------------- AJUSTES
    // ✔ - GET /perfil
    // ✔ - PUT /perfil
    // ✔ - PUT /cambiar-contrasena -> antigua nueva
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

    @GetMapping("/mascotas")
    public ResponseEntity<?> listarMascotasDelUsuario(@RequestHeader("Authorization") String token) {
        try {
            validarPrivado(token);

            Long idUsuario = JwtUtil.extractUserId(token);
            List<Mascota> mascotas = mascotaService.findByUsuarioId(idUsuario);

            if (mascotas.isEmpty()) {
                return ResponseEntity.status(404).body("No se encontraron mascotas para este usuario");
            }

            return ResponseEntity.ok(mascotas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    @PutMapping("/mascotas/{id}")
    public ResponseEntity<?> actualizarMascotaDelUsuario(@RequestHeader("Authorization") String token,
                                                         @PathVariable Long id,
                                                         @RequestBody MascotaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Optional<Mascota> optionalMascota = mascotaService.findById(id);
        if (optionalMascota.isEmpty()) {
            return ResponseEntity.status(404).body("Mascota no encontrada");
        }

        Mascota mascota = optionalMascota.get();

        if (!mascota.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar esta mascota");
        }

        // Actualizar campos si vienen en el DTO
        if (dto.getNombre() != null) mascota.setNombre(dto.getNombre());
        if (dto.getTipo() != null) mascota.setTipo(dto.getTipo());
        if (dto.getRaza() != null) mascota.setRaza(dto.getRaza());
        if (dto.getFechaNac() != null) mascota.setFechaNac(dto.getFechaNac());
        if (dto.getDescripcion() != null) mascota.setDescripcion(dto.getDescripcion());

        mascotaService.guardar(mascota);

        return ResponseEntity.ok("Mascota actualizada correctamente");
    }

    @PostMapping("/mascotas")
    public ResponseEntity<?> darDeAltaMascota(@RequestHeader("Authorization") String token,
                                              @RequestBody MascotaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = new Mascota();
        mascota.setNombre(dto.getNombre());
        mascota.setTipo(dto.getTipo());
        mascota.setRaza(dto.getRaza());
        mascota.setFechaNac(dto.getFechaNac());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setUsuario(usuario);

        mascotaService.guardar(mascota);

        return ResponseEntity.ok("Mascota registrada correctamente");
    }

    @GetMapping("/citas")
    public ResponseEntity<List<Cita>> obtenerCitasDelUsuario(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).build();
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        List<Cita> citas = citaService.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(citas);
    }

    @PostMapping("/citas")
    public ResponseEntity<?> crearCita(@RequestHeader("Authorization") String token,
                                       @RequestBody CitaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = mascotaService.findById(dto.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (!mascota.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para asignar esta mascota");
        }

        Cita cita = new Cita();
        cita.setTipo(dto.getTipo().name());
        cita.setFecha(dto.getFecha());
        cita.setEmpresa(dto.getEmpresa());
        cita.setUsuario(usuario);
        cita.setMascota(mascota);

        citaService.save(cita);

        return ResponseEntity.ok("Cita creada correctamente");
    }

    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> eliminarCita(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Optional<Cita> optionalCita = citaService.findById(id);
        if (optionalCita.isEmpty()) {
            return ResponseEntity.status(404).body("Cita no encontrada");
        }

        Cita cita = optionalCita.get();

        if (!cita.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta cita");
        }

        citaService.delete(id);
        return ResponseEntity.ok("Cita eliminada correctamente");
    }

    @PutMapping("/citas/{id}")
    public ResponseEntity<?> actualizarCita(@RequestHeader("Authorization") String token,
                                            @PathVariable Long id,
                                            @RequestBody CitaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Optional<Cita> optionalCita = citaService.findById(id);
        if (optionalCita.isEmpty()) {
            return ResponseEntity.status(404).body("Cita no encontrada");
        }

        Cita cita = optionalCita.get();

        if (!cita.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar esta cita");
        }

        // Actualizar campos si vienen
        if (dto.getTipo() != null) cita.setTipo(dto.getTipo().name());
        if (dto.getFecha() != null) cita.setFecha(dto.getFecha());
        if (dto.getEmpresa() != null) cita.setEmpresa(dto.getEmpresa());

        citaService.save(cita);

        return ResponseEntity.ok("Cita actualizada correctamente");
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestHeader("Authorization") String token,
                                               @RequestBody CambioContrasenaDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Usuario usuario = usuarioService.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getContrasena().equals(dto.getActual())) {
            return ResponseEntity.status(403).body("Contraseña actual incorrecta");
        }

        usuario.setContrasena(dto.getNueva());
        usuarioService.save(usuario);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
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
