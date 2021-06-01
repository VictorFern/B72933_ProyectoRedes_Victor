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

    private JLabel jllIp;
    private JLabel jlCliente;
    private JLabel jlPassword;

    private JTextField jtfIp;
    private JTextField jtfNombreCliente;
    private JPasswordField jpfPassword;

    private JButton jbtnIniciar;

    private HiloClient cliente;

    public Client() throws JDOMException, IOException {
        super("Client");

        this.setLayout(null);
        this.setSize(600, 250);

        init();

    }

    private void init() throws JDOMException, IOException {
        this.jllIp = new JLabel("Ip:");
        this.jllIp.setBounds(10, 60, 75, 30);
        this.add(this.jllIp);

        this.jlCliente = new JLabel("Nombre:");
        this.jlCliente.setBounds(150, 60, 75, 30);
        this.add(this.jlCliente);

        this.jlPassword = new JLabel("Contraseña:");
        this.jlPassword.setBounds(330, 60, 100, 30);
        this.add(this.jlPassword);

        this.jtfIp = new JTextField();
        this.jtfIp.setBounds(30, 60, 100, 30);
        this.add(this.jtfIp);

        this.jtfNombreCliente = new JTextField();
        this.jtfNombreCliente.setBounds(205, 60, 100, 30);
        this.add(this.jtfNombreCliente);

        this.jpfPassword = new JPasswordField();
        this.jpfPassword.setBounds(405, 60, 100, 30);
        this.add(this.jpfPassword);

        this.jbtnIniciar = new JButton("Iniciar");
        this.jbtnIniciar.setBounds(230, 150, 100, 30);
        this.jbtnIniciar.addActionListener(this);
        this.add(this.jbtnIniciar);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == this.jbtnIniciar) {

            
            try {
                Conexion conect = new Conexion();
                Connection conectar = conect.getConexion();
                Statement pst = conectar.createStatement();
                ResultSet rs = pst.executeQuery("call sp_obtener_usuario_all('" + this.jtfNombreCliente.getText()+ "','"+this.jpfPassword.getText()+"')");
                String i = "";
                while (rs.next()) {

                    i = rs.getString("nombre");

                    System.out.println("nombre = " + i);
                }

                if (i.equals(this.jtfNombreCliente.getText())) {
                    this.dispose();

                    this.cliente = new HiloClient(this.jtfIp.getText().trim(),this.jtfNombreCliente.getText().trim());
                    
                    VentanaPrincipal ventana = new VentanaPrincipal(this.cliente);
                    ventana.setVisible(true);
                    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    ventana.setLocationRelativeTo(null);
                    ventana.setResizable(false);
                    System.out.println("exito");
                } else {
                    JOptionPane.showMessageDialog(null, "Datos incorrecto");
                    this.jtfIp.setText("");
                    this.jtfNombreCliente.setText("");
                    this.jpfPassword.setText("");
                }
                conectar.close();
            } catch (SQLException | IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
           

        }
    }
}
