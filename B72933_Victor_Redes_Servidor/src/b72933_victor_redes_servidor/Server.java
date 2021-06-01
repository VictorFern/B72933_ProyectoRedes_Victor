/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_servidor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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

/**
 *
 * @author Victor
 */
public class Server extends JFrame implements Runnable, ActionListener {

    private JLabel labelIpServidor;
    private JLabel labelCliente;
    private JLabel labelContrasena;
    
    private JTextField textCliente;
    private JPasswordField textContrasena;
    private JButton buttonRegistrar;
    
    private Thread hilo;

    public Server() throws JDOMException, IOException {
        super("Server");
        this.setLayout(null);
        this.setSize(500, 300);
        init();
        this.hilo = new Thread(this);
        this.hilo.start();
    }

    public void init() throws JDOMException, IOException {
        this.labelCliente = new JLabel("Nombre cliente");
        this.labelCliente.setBounds(10, 70, 150, 30);
        this.add(labelCliente);

        this.labelContrasena = new JLabel("Contraseña cliente ");
        this.labelContrasena.setBounds(220, 70, 150, 30);
        this.add(labelContrasena);

        this.textContrasena = new JPasswordField();
        this.textContrasena.setBounds(330, 70, 100, 30);
        this.add(textContrasena);

        this.textCliente = new JTextField();
        this.textCliente.setBounds(100, 70, 100, 30);
        this.add(textCliente);

        this.buttonRegistrar = new JButton("Registrar Cliente");
        this.buttonRegistrar.setBounds(160, 120, 150, 30);
        this.buttonRegistrar.addActionListener(this);
        this.add(buttonRegistrar);

        this.labelIpServidor = new JLabel();
        this.labelIpServidor.setBounds(170, 150, 500, 100);
        this.add(this.labelIpServidor);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Utility.Utility.SOCKETNUMBER);
            
            InetAddress address = InetAddress.getLocalHost();

            this.labelIpServidor.setText(String.valueOf(address));
            do {
                Socket socket = serverSocket.accept();
                HiloServidor hiloServer = new HiloServidor(socket);
                hiloServer.start();
                System.out.println("Nueva conexion");
            } while (true);

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == this.buttonRegistrar) {
            try {
                Conexion conect = new Conexion();
                Connection conectar = conect.getConexion();
                Statement pst = conectar.createStatement();
                ResultSet rs = pst.executeQuery("call sp_obtener_usuario('" + this.textCliente.getText() + "')");
                String name = "";
                while (rs.next()) {

                    name = rs.getString("nombre");

                    System.out.println("nombre = " + name + " contrasena");
                }

                if (name.equals("")) {
                    pst.executeQuery("call sp_insertar_usuario('" + this.textCliente.getText() + "','" 
                            + this.textContrasena.getText() + "')");
                    File directorio = new File("Usuarios\\" + this.textCliente.getText());
                    if (!directorio.exists()) {
                        if (directorio.mkdirs()) {
                            System.out.println("Directorio creado");
                            JOptionPane.showMessageDialog(null, "Usuario creado");
                        } else {
                            System.out.println("Error al crear directorio");
                        }
                    }
                    this.textCliente.setText("");
                    this.textContrasena.setText("");
                    System.out.println("exito");
                } else {
                    System.out.println("El usuario ya existe");
                }
                conectar.close();
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

