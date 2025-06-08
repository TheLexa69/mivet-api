package mivet.controller;
import mivet.dto.*;
import mivet.enums.TipoGasto;
import mivet.model.*;
import mivet.service.*;
import mivet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
    * Controlador para manejar las operaciones relacionadas con el usuario.
    * Incluye gestión de perfil, mascotas, citas, gastos y mensajes.
    * Rutas:
     ✔ - GET /mascotas
     ✔ - PUT /mascotas/{id}
     ✔ - POST /mascotas CONTROLAR TIPO MASCOTA EN MINUSCULO
     ✔ - GET /citas
     ✔ - PUT /citas/{id}
     ✔ - DELETE /citas/{id}
     ✔ - POST /citas
     ✔ - GASTOS
     ---------------- AJUSTES
     ✔ - GET /perfil
     ✔ - PUT /perfil
     ✔ - PUT /cambiar-contrasena -> antigua nueva
     ✔ - GET/PUT /mensaje
 */

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MascotaService mascotaService;
    private final CitaService citaService;
    private final GastoService gastoService;
    private final MensajeService mensajeService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, MascotaService mascotaService,
                             CitaService citaService, GastoService gastoService, MensajeService mensajeService) {
        this.usuarioService = usuarioService;
        this.mascotaService = mascotaService;
        this.citaService = citaService;
        this.gastoService = gastoService;
        this.mensajeService = mensajeService;
    }



    // ------------------- PERFIL DE USUARIO ------------------- //
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

    // ------------------- MASCOTAS ------------------- //
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

    // ------------------- CITAS ------------------- //
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

    // ------------------- AJUSTES ------------------- //
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

    // ------------------- GASTOS ------------------- //
//    @GetMapping("/gastos")
//    public ResponseEntity<?> listarGastos(@RequestHeader("Authorization") String token) {
//        if (!JwtUtil.isTokenValid(token)) {
//            return ResponseEntity.status(401).body("Token inválido");
//        }
//
//        Long idUsuario = JwtUtil.extractUserId(token);
//
//        List<Mascota> mascotas = mascotaService.findByUsuarioId(idUsuario);
//        List<Gasto> todosLosGastos = mascotas.stream()
//                .flatMap(mascota -> gastoService.findByMascotaId(mascota.getId().longValue()).stream()).toList();
//
//        return ResponseEntity.ok(todosLosGastos);
//    }

    @PostMapping("/gastos")
    public ResponseEntity<?> crearGasto(@RequestHeader("Authorization") String token,
                                        @RequestBody GastoDTO dto) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Mascota mascota = mascotaService.findById(dto.getIdMascota())
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (!mascota.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para registrar un gasto para esta mascota");
        }

        Gasto gasto = new Gasto();
        gasto.setDescripcion(dto.getDescripcion());
        gasto.setCantidad(dto.getCantidad());
        gasto.setFecha(dto.getFecha());
        gasto.setMascota(mascota);

        gastoService.save(gasto);

        return ResponseEntity.ok("Gasto registrado correctamente");
    }

    @DeleteMapping("/gastos/{id}")
    public ResponseEntity<?> eliminarGasto(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Optional<Gasto> optionalGasto = gastoService.findById(id);
        if (optionalGasto.isEmpty()) {
            return ResponseEntity.status(404).body("Gasto no encontrado");
        }

        Gasto gasto = optionalGasto.get();

        if (!gasto.getMascota().getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar este gasto");
        }

        gastoService.delete(id);
        return ResponseEntity.ok("Gasto eliminado correctamente");
    }

    // ------------------- FILTRAR GASTOS ------------------- //
    //GET /api/usuario/gastos?tipo=veterinaria&desde=2024-04-01&hasta=2024-05-31
    @GetMapping("/gastos")
    public ResponseEntity<?> filtrarGastos(@RequestHeader("Authorization") String token,
                                           @RequestParam(required = false) String tipo,
                                           @RequestParam(required = false) String dia,
                                           @RequestParam(required = false) Integer mes,
                                           @RequestParam(required = false) Integer anio,
                                           @RequestParam(required = false) String desde,
                                           @RequestParam(required = false) String hasta) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        List<Mascota> mascotas = mascotaService.findByUsuarioId(idUsuario);

        List<Gasto> gastos = mascotas.stream()
                .flatMap(mascota -> gastoService.findByMascotaId(mascota.getId().longValue()).stream())
                .toList();

        // Filtro por tipo
        if (tipo != null) {
            try {
                TipoGasto tipoEnum = TipoGasto.valueOf(tipo);
                gastos = gastos.stream()
                        .filter(g -> g.getTipo() == tipoEnum)
                        .toList();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Tipo de gasto no válido");
            }
        }

        // Filtro por día exacto
        if (dia != null) {
            LocalDate fecha = LocalDate.parse(dia);
            gastos = gastos.stream()
                    .filter(g -> g.getFecha().equals(fecha))
                    .toList();
        }

        // Filtro por mes y/o año
        if (mes != null || anio != null) {
            gastos = gastos.stream()
                    .filter(g -> {
                        LocalDate f = g.getFecha();
                        boolean coincideMes = mes == null || f.getMonthValue() == mes;
                        boolean coincideAnio = anio == null || f.getYear() == anio;
                        return coincideMes && coincideAnio;
                    })
                    .toList();
        }

        // Filtro por rango de fechas
        if (desde != null || hasta != null) {
            LocalDate desdeFecha = (desde != null) ? LocalDate.parse(desde) : LocalDate.MIN;
            LocalDate hastaFecha = (hasta != null) ? LocalDate.parse(hasta) : LocalDate.MAX;

            gastos = gastos.stream()
                    .filter(g -> !g.getFecha().isBefore(desdeFecha) && !g.getFecha().isAfter(hastaFecha))
                    .toList();
        }

        return ResponseEntity.ok(gastos);
    }

    //-- ------------------- MENSAJES ------------------- //
    @GetMapping("/mensajes")
    public ResponseEntity<?> obtenerMensajes(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        List<Mensaje> mensajes = mensajeService.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(mensajes);
    }

    @PutMapping("/mensajes/{id}/leido")
    public ResponseEntity<?> marcarComoLeido(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);

        Optional<Mensaje> optionalMensaje = mensajeService.findById(id);
        if (optionalMensaje.isEmpty()) {
            return ResponseEntity.status(404).body("Mensaje no encontrado");
        }

        Mensaje mensaje = optionalMensaje.get();

        if (!mensaje.getUsuario().getId().equals(idUsuario.intValue())) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar este mensaje");
        }

        mensaje.setLeido(true);
        mensajeService.save(mensaje);

        return ResponseEntity.ok("Mensaje marcado como leído");
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
