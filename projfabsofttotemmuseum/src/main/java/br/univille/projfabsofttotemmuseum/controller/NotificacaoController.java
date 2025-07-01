package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import br.univille.projfabsofttotemmuseum.entity.Notificacao;
import br.univille.projfabsofttotemmuseum.service.NotificacaoService;
import br.univille.projfabsofttotemmuseum.repository.UsuarioRepository;
import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.service.WhatsAppService;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private WhatsAppService whatsAppService;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @GetMapping
    public ResponseEntity<List<Notificacao>> getNotificacoes() {
        var listaNotificacoes = notificacaoService.getAllNotificacoes();
        return new ResponseEntity<>(listaNotificacoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> getNotificacaoById(@PathVariable Long id) {
        var notificacao = notificacaoService.getNotificacaoById(id);
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(notificacao, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Notificacao> postNotificacao(@RequestBody Notificacao notificacao) {
        if (notificacao == null || notificacao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var novaNotificacao = notificacaoService.save(notificacao);
        return new ResponseEntity<>(novaNotificacao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> putNotificacao(@PathVariable Long id, @RequestBody Notificacao notificacao) {
        if (id <= 0 || notificacao == null || notificacao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var notificacaoExistente = notificacaoService.getNotificacaoById(id);
        if (notificacaoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        notificacaoExistente.setDataHora(notificacao.getDataHora());
        notificacaoExistente.setDescricao(notificacao.getDescricao());
        notificacaoExistente.setTipo(notificacao.getTipo());
        notificacaoExistente.setUsuario(notificacao.getUsuario());
        notificacaoExistente.setEvento(notificacao.getEvento());
        notificacaoExistente.setExposicao(notificacao.getExposicao());

        var notificacaoAtualizada = notificacaoService.save(notificacaoExistente);
        return new ResponseEntity<>(notificacaoAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Notificacao> deleteNotificacao(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var notificacao = notificacaoService.getNotificacaoById(id);
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        notificacaoService.delete(id);
        return new ResponseEntity<>(notificacao, HttpStatus.OK);
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarNotificacao(
            @RequestParam("notificationTitle") String notificationTitle,
            @RequestParam("notificationTextOnly") String notificationTextOnly,
            @RequestParam(value = "notificationImage", required = false) MultipartFile notificationImage) {
        try {
            String fileName = null;
            if (notificationImage != null && !notificationImage.isEmpty()) {
                fileName = System.currentTimeMillis() + "_" + notificationImage.getOriginalFilename();
                Path imagePath = Paths.get("src/main/resources/static/notificacoes/" + fileName);
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, notificationImage.getBytes());
            }
            // Notificar usuários por WhatsApp
            try {
                java.util.List<Usuario> usuariosWhatsapp = usuarioRepository.findByNotificaWhatsappTrue();
                for (Usuario usuario : usuariosWhatsapp) {
                    if (usuario.getTelefone() != null && !usuario.getTelefone().isEmpty()) {
                        String numero = usuario.getTelefone();
                        if (!numero.startsWith("+")) {
                            numero = "+55" + numero.replaceAll("[^0-9]", "");
                        }
                        String mensagem = notificationTitle + ": " + notificationTextOnly;
                        whatsAppService.enviarMensagem(numero, mensagem);
                    }
                }
            } catch (Exception e) {
                // Log ou tratamento de erro
            }
            // Notificar usuários por e-mail
            try {
                java.util.List<Usuario> usuariosEmail = usuarioRepository.findByNotificaEmailTrue();
                for (Usuario usuario : usuariosEmail) {
                    if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                        SimpleMailMessage mail = new SimpleMailMessage();
                        mail.setTo(usuario.getEmail());
                        mail.setSubject(notificationTitle);
                        mail.setText(notificationTextOnly);
                        mailSender.send(mail);
                    }
                }
            } catch (Exception e) {
                // Log ou tratamento de erro
            }
            return ResponseEntity.ok("Notificação enviada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao enviar notificação: " + e.getMessage());
        }
    }
}
