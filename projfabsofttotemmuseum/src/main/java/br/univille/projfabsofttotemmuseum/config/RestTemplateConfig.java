package br.univille.projfabsofttotemmuseum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Indica que esta classe contém métodos de configuração de beans
public class RestTemplateConfig {

    @Bean // Marca este método como um produtor de bean do Spring
    public RestTemplate restTemplate() {
        // Cria e retorna uma nova instância de RestTemplate.
        // RestTemplate é uma classe central para fazer chamadas HTTP para serviços RESTful.
        return new RestTemplate();
    }
}
