package br.univille.projfabsofttotemmuseum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class WhatsAppService {

    @Value("${ultramsg.instanceId}")
    private String instanceId;

    @Value("${ultramsg.token}")
    private String token;

    public void enviarMensagem(String numero, String mensagem) {
        String url = "https://api.ultramsg.com/" + instanceId + "/messages/chat";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("to", numero);
        params.put("body", mensagem);

        StringBuilder body = new StringBuilder();
        params.forEach((k, v) -> body.append(k).append("=").append(v).append("&"));
        String requestBody = body.toString();

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
} 