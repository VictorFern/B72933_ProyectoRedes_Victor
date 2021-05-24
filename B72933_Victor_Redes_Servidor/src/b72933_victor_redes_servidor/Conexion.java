/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Victor
 */
public class Conexion {

    public static final String URL = "jdbc:mysql://localhost:3306/usuario";
    public static final String USER = "root";
    public static final String CLAVE = "Kurama1727x.";
     
    public Connection getConexion(){
        Connection con = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, CLAVE);
        }catch(ClassNotFoundException | SQLException e){
            System.out.println("Error: " + e.getMessage());
        }
        return con;
    }
}