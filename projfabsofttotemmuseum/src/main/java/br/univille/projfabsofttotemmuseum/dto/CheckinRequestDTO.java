package br.univille.projfabsofttotemmuseum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CheckinRequestDTO {
    @NotBlank(message = "O identificador do usuário (e-mail ou telefone) é obrigatório.")
    @Email(message = "O e-mail informado não é válido.", regexp = ".+@.+\\..+")
    private String usuarioIdentifier;
    private String dataHora;
    private String local;
    @NotBlank(message = "O nome é obrigatório.")
    private String nome; // Novo campo
    @NotBlank(message = "O gênero é obrigatório.")
    private String genero; // Novo campo
    @NotNull(message = "A idade é obrigatória.")
    @Min(value = 0, message = "A idade mínima permitida é 0.")
    @Max(value = 120, message = "A idade máxima permitida é 120.")
    private Integer idade; // Novo campo
    @NotBlank(message = "O estado é obrigatório.")
    private String estado; // Novo campo
    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade; // Novo campo
    private boolean notificacoesExposicoes;
    private boolean notificacoesEventos;

    public String getUsuarioIdentifier() {
        return usuarioIdentifier;
    }

    public void setUsuarioIdentifier(String usuarioIdentifier) {
        this.usuarioIdentifier = usuarioIdentifier;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public boolean isNotificacoesExposicoes() {
        return notificacoesExposicoes;
    }

    public void setNotificacoesExposicoes(boolean notificacoesExposicoes) {
        this.notificacoesExposicoes = notificacoesExposicoes;
    }

    public boolean isNotificacoesEventos() {
        return notificacoesEventos;
    }

    public void setNotificacoesEventos(boolean notificacoesEventos) {
        this.notificacoesEventos = notificacoesEventos;
    }
}
