package br.univille;

import br.univille.entity.Cliente;
import br.univille.entity.Pokemon;
import br.univille.entity.Cidade;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Hello world!");

        Cidade joinville = new Cidade();
        joinville.setNome("Joinville");
        joinville.setEstado("Santa Catarina");
        
        Pokemon jigglypuff = new Pokemon("Jigglypuff");
        Pokemon porygon = new Pokemon("Porygon");

        Cliente zezinho = new Cliente("Zezinho");
        zezinho.setNome("Zezinho da silva Sauro");
        zezinho.setCidade(joinville);

        zezinho.getListaPokemon().add(jigglypuff);
        zezinho.getListaPokemon().add(porygon);
        //zezinho.getListaPokemon().add(123);
        //zezinho.getListaPokemon().add(2.2);
        //zezinho.getListaPokemon().add("nao sou um pokemon");
        //zezinho.getListaPokemon().add(true);

        for(int i=0;i<zezinho.getListaPokemon().size();i++){
            var umPokemon = zezinho.getListaPokemon().get(i);
            System.out.println(umPokemon);
        }
        for(var umPokemon : zezinho.getListaPokemon()){
            System.out.println(umPokemon);
        }
        //gere um codigo para listar os pokemons utilizando o método stream
        zezinho.getListaPokemon().stream().forEach(System.out::println);


        Cliente mariazinha = new Cliente();
        mariazinha.setNome("Mariazinha");


        System.out.println(zezinho);
        System.out.println(mariazinha);
    }
}