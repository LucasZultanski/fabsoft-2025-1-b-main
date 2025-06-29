package br.univille.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.univille.controller.ControllerCliente;

import java.awt.GridLayout;

public class FrmMain extends JFrame {
    private JLabel lblId, lblNome, lblEndereco;
    private JTextField txtId, txtNome, txtEndereco;

    private JButton btnSalvar, btnSair;

    public FrmMain() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setTitle("Meu primeiro Swing");
        //var texto = new JLabel("Meu texto");
        //getContentPane().add(texto);
        painelCampos();
        painelBotoes();
        setVisible(true);
    }
    private void painelBotoes(){
        ControllerCliente controller = new ControllerCliente();

        JPanel painel = new JPanel();
        btnSalvar = new JButton("Salvar");
        painel.add(btnSalvar);
        btnSalvar.addActionListener(controller);

        btnSair = new JButton("Sair");
        painel.add(btnSair);
        btnSair.addActionListener(controller);

        getContentPane().add(painel,"South");
    }
    private void painelCampos(){
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(3,2));
        lblId = new JLabel("ID");
        painel.add(lblId);
        txtId = new JTextField(5);
        painel.add(txtId);

        lblNome = new JLabel("Nome");
        painel.add(lblNome);
        txtNome = new JTextField(15);
        painel.add(txtNome);

        lblEndereco = new JLabel("Endereço");
        painel.add(lblEndereco);
        txtEndereco = new JTextField(15);
        painel.add(txtEndereco);

        getContentPane().add(painel,"North");
    }



    public static void main(String[] args) {
        new FrmMain();
    }
}
