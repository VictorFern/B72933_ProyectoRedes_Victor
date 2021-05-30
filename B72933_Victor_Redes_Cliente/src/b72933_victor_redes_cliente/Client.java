/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_cliente;

/**
 *
 * @author Victor
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jdom.JDOMException;

public class Client extends JFrame implements ActionListener {

    private JLabel labelIp;
    private JLabel labelId;
    private JLabel labelPassword;

    private JTextField textIp;
    private JTextField textId;
    private JPasswordField textPassword;

    private JButton botonIniciar;

    private HiloClient cliente;

    public Client() throws JDOMException, IOException {
        super("Client");

        this.setLayout(null);
        this.setSize(250, 250);

        init();

    }

    private void init() throws JDOMException, IOException {
        this.labelIp = new JLabel("Ip:");
        this.labelIp.setBounds(10, 10, 75, 30);
        this.add(this.labelIp);

        this.labelId = new JLabel("Nombre:");
        this.labelId.setBounds(10, 60, 75, 30);
        this.add(this.labelId);

        this.labelPassword = new JLabel("Contraseña");
        this.labelPassword.setBounds(10, 110, 100, 30);
        this.add(this.labelPassword);

        this.textIp = new JTextField();
        this.textIp.setBounds(100, 10, 100, 30);
        this.add(this.textIp);

        this.textId = new JTextField();
        this.textId.setBounds(100, 60, 100, 30);
        this.add(this.textId);

        this.textPassword = new JPasswordField();
        this.textPassword.setBounds(100, 110, 100, 30);
        this.add(this.textPassword);

        this.botonIniciar = new JButton("Iniciar");
        this.botonIniciar.setBounds(50, 150, 100, 30);
        this.botonIniciar.addActionListener(this);
        this.add(this.botonIniciar);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == this.botonIniciar) {

            
            try {
                Conexion conect = new Conexion();
                Connection conectar = conect.getConexion();
                Statement pst = conectar.createStatement();
                ResultSet rs = pst.executeQuery("call sp_obtener_usuario('" + this.textId.getText()+"')");
                String i = "";
                while (rs.next()) {

                    i = rs.getString("nombre");

                    System.out.println("nombre = " + i);
                }

                if (i.equals(this.textId.getText())) {
                    this.dispose();

                    this.cliente = new HiloClient(this.textIp.getText().trim(),this.textId.getText().trim());
                    
                    VentanaPrincipal ventana = new VentanaPrincipal(this.cliente);
                    ventana.setVisible(true);
                    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    ventana.setLocationRelativeTo(null);
                    ventana.setResizable(false);
                    System.out.println("exito");
                } else {
                    JOptionPane.showMessageDialog(null, "Datos ingresados incorrecto");
                }
                conectar.close();
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
           

        }
    }
}
