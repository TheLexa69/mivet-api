package mivet.controller;

import mivet.dto.AdopcionDTO;
import mivet.enums.EstadoAdopcion;
import mivet.model.Adopcion;
import mivet.model.Mascota;
import mivet.model.Usuario;
import mivet.repository.AdopcionRepository;
import mivet.service.AdopcionService;
import mivet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/adopciones")
public class AdopcionController {

    private final AdopcionRepository adopcionRepository;
    private final AdopcionService adopcionService;

    @Autowired
    public AdopcionController(AdopcionRepository adopcionRepository, AdopcionService adopcionService) {
        this.adopcionRepository = adopcionRepository;
        this.adopcionService = adopcionService;
    }

    // --- UTILIDAD PARA MAPEAR ENTIDAD A DTO --- //
    private AdopcionDTO mapToDTO(Adopcion adopcion) {
        AdopcionDTO dto = new AdopcionDTO();
        dto.setId(adopcion.getId());
        dto.setIdMascota(adopcion.getMascota().getId().longValue()); // Conversión a Long
        dto.setIdUsuario(adopcion.getUsuario().getId().longValue()); // Conversión a Long
        dto.setMensaje(adopcion.getMensaje());
        dto.setEstado(adopcion.getEstado().name());
        dto.setFechaSolicitud(adopcion.getFechaSolicitud());
        return dto;
    }

    // --- PROTECTORAS --- //

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoAdopcion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam("estado") String estado
    ) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String rol = JwtUtil.extractRol(token);
        if (!"admin".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo protectoras pueden modificar el estado");
        }

        if (!estado.equalsIgnoreCase("aceptada") && !estado.equalsIgnoreCase("rechazada")) {
            return ResponseEntity.badRequest().body("Estado no válido. Usa 'aceptada' o 'rechazada'.");
        }

        Optional<Adopcion> adopcionOpt = adopcionRepository.findById(id);
        if (adopcionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Adopcion adopcion = adopcionOpt.get();
        Long idProtectora = JwtUtil.extractUserId(token);
        Integer protectoraId = idProtectora.intValue();

        if (!adopcion.getMascota().getUsuario().getId().equals(protectoraId)) {
            return ResponseEntity.status(403).body("No tienes permiso para modificar esta solicitud");
        }

        adopcion.setEstado(EstadoAdopcion.valueOf(estado.toLowerCase()));
        adopcionRepository.save(adopcion);

        return ResponseEntity.ok(mapToDTO(adopcion));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> listarAdopcionesPendientes(@RequestHeader("Authorization") String token) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String rol = JwtUtil.extractRol(token);
        if (!"admin".equalsIgnoreCase(rol)) {
            return ResponseEntity.status(403).body("Acceso denegado: solo protectoras pueden ver las solicitudes");
        }

        Long idProtectora = JwtUtil.extractUserId(token);

        List<Adopcion> pendientes = adopcionRepository.findByEstado(EstadoAdopcion.pendiente);

        List<AdopcionDTO> resultado = pendientes.stream()
                .filter(adopcion -> adopcion.getMascota().getUsuario().getId().equals(idProtectora.intValue()))
                .map(this::mapToDTO)
                .toList();

        return ResponseEntity.ok(resultado);
    }

    // --- USUARIOS --- //

    @PostMapping("/{idMascota}/solicitar")
    public ResponseEntity<?> solicitarAdopcion(
            @RequestHeader("Authorization") String token,
            @PathVariable Long idMascota,
            @RequestParam String mensaje
    ) {
        if (!JwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        Long idUsuario = JwtUtil.extractUserId(token);
        String resultado = adopcionService.solicitarAdopcion(idUsuario, idMascota, mensaje);

        return switch (resultado) {
            case "not_found" -> ResponseEntity.status(404).body("Usuario o mascota no encontrados");
            case "not_protectora" -> ResponseEntity.badRequest().body("La mascota no pertenece a una protectora");
            case "duplicate" -> ResponseEntity.badRequest().body("Ya se ha solicitado adoptar esta mascota");
            case "ok" -> ResponseEntity.ok("Solicitud de adopción enviada correctamente");
            default -> ResponseEntity.status(500).body("Error inesperado");
        };
    }
}
