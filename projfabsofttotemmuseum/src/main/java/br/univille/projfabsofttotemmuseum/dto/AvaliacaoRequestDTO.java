package br.univille.projfabsofttotemmuseum.dto;

public class AvaliacaoRequestDTO {
    private int nota;
    private String usuarioIdentifier;
    private String dataHora;

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

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
}
