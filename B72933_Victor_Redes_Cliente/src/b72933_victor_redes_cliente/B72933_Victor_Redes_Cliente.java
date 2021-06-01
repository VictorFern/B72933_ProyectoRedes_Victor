/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_cliente;

import java.io.IOException;
import java.lang.System.Logger;
import java.util.logging.Level;
import javax.swing.JFrame;
import org.jdom.JDOMException;

/**
 *
 * @author Victor
 */
public class B72933_Victor_Redes_Cliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            
            Client c = new Client();
            c.setVisible(true);
            c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            c.setLocationRelativeTo(null);
            c.setResizable(false);
        } catch (JDOMException | IOException ex) {
            java.util.logging.Logger.getLogger(B72933_Victor_Redes_Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
