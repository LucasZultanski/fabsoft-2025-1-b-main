package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getUsuarios() {
        var listaUsuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(listaUsuarios, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postUsuario(@RequestBody Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Dados do usuário ou e-mail inválidos."), HttpStatus.BAD_REQUEST);
        }
        if (!usuario.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            return new ResponseEntity<>(Map.of("message", "Formato de e-mail inválido."), HttpStatus.BAD_REQUEST);
        }

        try {
            var savedUsuario = usuarioService.saveOrUpdateUsuarioForNotifications(usuario);
            return new ResponseEntity<>(savedUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao cadastrar/atualizar usuário: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> putUsuario(@PathVariable long id, @RequestBody Usuario usuario) {
        if (id <= 0 || usuario == null) {
            return ResponseEntity.badRequest().build();
        }
        var usuarioExistente = usuarioService.getUsuarioById(id);
        if (usuarioExistente == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setTelefone(usuario.getTelefone());
        usuarioExistente.setNotificacoesExposicoes(usuario.isNotificacoesExposicoes());
        usuarioExistente.setNotificacoesEventos(usuario.isNotificacoesEventos());
        usuarioExistente.setNotificacoesNovidades(usuario.isNotificacoesNovidades());
        usuarioExistente.setIdade(usuario.getIdade());
        usuarioExistente.setGenero(usuario.getGenero());

        var usuarioAtualizado = usuarioService.save(usuarioExistente);
        return new ResponseEntity<>(usuarioAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Usuario> deleteUsuario(@PathVariable long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        usuarioService.delete(id);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}
