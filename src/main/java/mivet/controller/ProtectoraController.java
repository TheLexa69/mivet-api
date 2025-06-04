package mivet.controller;

import mivet.dto.MascotaDTO;
import mivet.dto.ProtectoraDTO;
import mivet.model.Mascota;
import mivet.model.Protectora;
import mivet.model.Usuario;
import mivet.service.MascotaService;
import mivet.service.ProtectoraService;
import mivet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mivet.util.JwtUtil;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/protectora")
public class ProtectoraController {

    private final MascotaService mascotaService;
    private final ProtectoraService protectoraService;
    private final UsuarioService usuarioService;

    @Autowired
    public ProtectoraController(MascotaService mascotaService, ProtectoraService protectoraService, UsuarioService usuarioService) {
        this.mascotaService = mascotaService;
        this.protectoraService = protectoraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/mascotas")
    public ResponseEntity<List<Mascota>> listarMascotasDeProtectora(@RequestHeader("Authorization") String token) {
        try {
            validarProtectora(token); // Validar token y tipo de usuario

            Long idProtectora = JwtUtil.extractUserId(token);
            List<Mascota> mascotas = mascotaService.findByUsuarioId(idProtectora);
            return ResponseEntity.ok(mascotas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 403 Forbidden
        }
    }


    @PostMapping("/mascotas")
    public ResponseEntity<Mascota> darDeAltaMascota(@RequestHeader("Authorization") String token,
                                                    @RequestBody MascotaDTO dto) {
        try {
            validarProtectora(token); // Validar token y tipo de usuario

            Long idProtectora = JwtUtil.extractUserId(token);
            Usuario usuario = new Usuario();
            usuario.setId(Math.toIntExact(idProtectora));

            Mascota nuevaMascota = new Mascota();
            nuevaMascota.setNombre(dto.getNombre());
            nuevaMascota.setRaza(dto.getRaza());
            nuevaMascota.setTipo(dto.getTipo());
            nuevaMascota.setFechaNac(dto.getFechaNac());
            nuevaMascota.setDescripcion(dto.getDescripcion());
            nuevaMascota.setUsuario(usuario);

            Mascota guardada = mascotaService.guardar(nuevaMascota);
            return ResponseEntity.ok(guardada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(null); // 403 Forbidden
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfilProtectora(@RequestHeader("Authorization") String token,
                                                        @RequestBody ProtectoraDTO dto) {
        try {
            validarProtectora(token); // Validate token and user type

            Long idUsuario = JwtUtil.extractUserId(token);
            Optional<Protectora> optionalProtectora = protectoraService.findByUsuarioId(idUsuario.intValue());

            // Retrieve the user from the database to avoid "detached entity"
            Usuario usuario = usuarioService.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Protectora protectora;
            if (optionalProtectora.isPresent()) {
                protectora = optionalProtectora.get();
            } else {
                protectora = new Protectora();
                protectora.setUsuario(usuario);
            }

            // Update fields
            if (dto.getNombre() != null) {
                usuario.setNombre(dto.getNombre());
            }

            if (dto.getCif() != null) protectora.setCif(dto.getCif());
            if (dto.getTelefono() != null) protectora.setTelefono(dto.getTelefono());
            if (dto.getWeb() != null) protectora.setWeb(dto.getWeb());
            if (dto.getCodigoONG() != null) protectora.setCodigoONG(dto.getCodigoONG());
            if (dto.getDireccion() != null) protectora.setDireccion(dto.getDireccion());
            if (dto.getLogo() != null) protectora.setLogo(dto.getLogo());
            if (dto.getFacebook() != null) protectora.setFacebook(dto.getFacebook());
            if (dto.getInstagram() != null) protectora.setInstagram(dto.getInstagram());
            if (dto.getTiktok() != null) protectora.setTiktok(dto.getTiktok());
            if (dto.getLinkedin() != null) protectora.setLinkedin(dto.getLinkedin());

            // Save changes
            usuarioService.save(usuario);
            protectoraService.save(protectora);

            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(@RequestHeader("Authorization") String token) {
        try {
            validarProtectora(token); // Validar token y tipo de usuario

            Long idUsuario = JwtUtil.extractUserId(token);
            Optional<Protectora> optionalProtectora = protectoraService.findByUsuarioId(idUsuario.intValue());

            if (optionalProtectora.isEmpty()) {
                return ResponseEntity.status(404).build(); // 404 Not Found
            }

            ProtectoraDTO dto = convertToDTO(optionalProtectora.get());
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage()); // 403 Forbidden
        }
    }

    private ProtectoraDTO convertToDTO(Protectora protectora) {
        ProtectoraDTO dto = new ProtectoraDTO();
        dto.setId(protectora.getId());
        dto.setCif(protectora.getCif());
        dto.setTelefono(protectora.getTelefono());
        dto.setWeb(protectora.getWeb());
        dto.setCodigoONG(protectora.getCodigoONG());
        dto.setDireccion(protectora.getDireccion());
        dto.setLogo(protectora.getLogo());
        dto.setFacebook(protectora.getFacebook());
        dto.setInstagram(protectora.getInstagram());
        dto.setTiktok(protectora.getTiktok());
        dto.setLinkedin(protectora.getLinkedin());

        if (protectora.getUsuario() != null) {
            dto.setIdUsuario(protectora.getUsuario().getId());
            dto.setNombre(protectora.getUsuario().getNombre());
        }

        return dto;
    }

    private void validarProtectora(String token) {
        try {
            if (!JwtUtil.isTokenValid(token)) {
                throw new RuntimeException("Token expirado o inválido");
            }

            String tipoUsuario = JwtUtil.extractTipoUsuario(token);
            if (!"protectora".equalsIgnoreCase(tipoUsuario)) {
                throw new RuntimeException("No tiene permisos para acceder a este recurso");
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new RuntimeException("Token expirado", e);
        }
    }
}
