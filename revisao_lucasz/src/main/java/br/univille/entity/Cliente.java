package br.univille.entity;

import java.util.ArrayList;

public class Cliente {
    //variável - atributo
    private long id;
    private String nome;
    private String endereco;
    
    private ArrayList<Pokemon> listaPokemon = new ArrayList<Pokemon>();

    public ArrayList<Pokemon> getListaPokemon() {
        return listaPokemon;
    }
    public void setListaPokemon(ArrayList<Pokemon> listaPokemon) {
        this.listaPokemon = listaPokemon;
    }

    private Cidade cidade;

    public Cidade getCidade() {
        return cidade;
    }
    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    //construtor - mesmo nome da classe e não te retorno
    // inicializar atributos
    // obrigar a passagem de valores
    public Cliente(String nome) {
        this.nome = nome;
    }
    //polimorfismo - várias formas de existir a mesma coisa
    public Cliente() {
        
    }
    //método
    //sobreescrita de método (meu pai 
    //me deu pronto e eu zuera reescrevi)
    @Override
    public String toString(){
        return getNome();
    }

    //método
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    //encapsulamento = esconder a 
    // implementação dentro do objeto
}
