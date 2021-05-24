/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_servidor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.jdom.JDOMException;

/**
 *
 * @author Victor
 */
public class B72933_Victor_Redes_Servidor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            
            Server servidor = new Server();
            servidor.setVisible(true);
            servidor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            servidor.setLocationRelativeTo(null);
            servidor.setResizable(false);
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(B72933_Victor_Redes_Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

}
