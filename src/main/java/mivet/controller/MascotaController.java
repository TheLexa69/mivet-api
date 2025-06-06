package mivet.controller;

import mivet.enums.TipoUsuario;
import mivet.model.Mascota;
import mivet.service.MascotaService;
import mivet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    @Autowired
    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public ResponseEntity<?> listarMascotas(@RequestHeader("Authorization") String token) {
        try {
            validarTipoUsuario(token);
            List<Mascota> mascotas = mascotaService.findAll();
            if (!mascotas.isEmpty()) {
                return ResponseEntity.ok(mascotas);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMascotaPorId(@RequestHeader("Authorization") String token,
                                                 @PathVariable Long id) {
        try {
            validarTipoUsuario(token);
            Optional<Mascota> mascota = mascotaService.findById(id);
            return mascota.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/mascotas/{id}")
    public ResponseEntity<?> eliminarMascota(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id) {
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
            return ResponseEntity.status(403).body("No tienes permiso para eliminar esta mascota");
        }

        mascotaService.eliminar(id);

        return ResponseEntity.ok("Mascota eliminada correctamente");
    }


    private void validarTipoUsuario(String token) {
        if (!JwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Token expirado o inválido");
        }

        String tipoUsuarioRaw = JwtUtil.extractTipoUsuario(token).trim().toLowerCase();
        JwtUtil.printTokenData(token);

        if (!tipoUsuarioRaw.equals("privado") && !tipoUsuarioRaw.equals("protectora")) {
            throw new RuntimeException("No tiene permisos para acceder a este recurso");
        }
    }
}
