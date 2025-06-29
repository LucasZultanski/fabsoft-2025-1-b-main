package br.univille.projfabsofttotemmuseum.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = true, unique = true)
    private String email;

    private String telefone;

    @Column(nullable = true) // Nome pode ser nulo inicialmente ou preenchido depois
    private String nome;

    private boolean notificacoesExposicoes;
    private boolean notificacoesEventos;
    private boolean notificacoesNovidades;

    private Integer idade;
    private String genero;
    
    @Column(nullable = true)
    private String estado; // Novo campo para estado
    
    @Column(nullable = true)
    private String cidade; // Novo campo para cidade

    @Column(nullable = false)
    private String role = "ROLE_VISITOR";

    @ManyToMany(mappedBy = "usuariosNotificados")
    private List<Evento> eventosNotificados;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Avaliacao> avaliacoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Checkup> checkups;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Exposicao> exposicoes;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacao> notificacoes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public boolean isNotificacoesNovidades() {
        return notificacoesNovidades;
    }

    public void setNotificacoesNovidades(boolean notificacoesNovidades) {
        this.notificacoesNovidades = notificacoesNovidades;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Evento> getEventosNotificados() {
        return eventosNotificados;
    }

    public void setEventosNotificados(List<Evento> eventosNotificados) {
        this.eventosNotificados = eventosNotificados;
    }

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public List<Checkup> getCheckups() {
        return checkups;
    }

    public void setCheckups(List<Checkup> checkups) {
        this.checkups = checkups;
    }

    public List<Exposicao> getExposicoes() {
        return exposicoes;
    }

    public void setExposicoes(List<Exposicao> exposicoes) {
        this.exposicoes = exposicoes;
    }

    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }
}
