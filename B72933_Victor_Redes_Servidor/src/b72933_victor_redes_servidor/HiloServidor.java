/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_servidor;

import Utility.Utility;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jdom.JDOMException;

/**
 *
 * @author Victor
 */
public class HiloServidor extends Thread {

    private boolean execute;
    private String accion;
    private String filename;
    private String rutaCarpeta;
    private String archivoADescargar;
    private Socket socket;
    private DataOutputStream send;
    private DataInputStream receive;

    public HiloServidor(Socket socket) throws JDOMException, IOException {
        this.execute = true;
        this.accion = "";
        this.filename = "";
        this.rutaCarpeta = "";
        this.archivoADescargar = "";
        this.socket = socket;
        this.send = new DataOutputStream(this.socket.getOutputStream());
        this.receive = new DataInputStream(this.socket.getInputStream());
    }

    public HiloServidor() throws IOException {
        this.execute = true;
        this.accion = "";
        this.filename = "";
        this.rutaCarpeta = "";
        this.archivoADescargar = "";
        this.socket = new Socket();
        this.send = new DataOutputStream(this.socket.getOutputStream());
        this.receive = new DataInputStream(this.socket.getInputStream());
    }

    @Override
    public void run() {

        try {
            do {
                this.accion = this.receive.readUTF();
                System.out.println(this.accion);
                if (this.accion.equalsIgnoreCase(Utility.IDENTIFICAR)) {
                    this.rutaCarpeta = this.receive.readUTF();
                    System.out.println("Nombre es: " + this.rutaCarpeta);
                    this.accion = "";
                } else if (this.accion.equalsIgnoreCase(Utility.AVISOLISTAR)) {
                    listarArchivos();
                } else if (this.accion.equalsIgnoreCase(Utility.AVISODESCARGA)) {
                    enviarArchivo();
                } else if (this.accion.equalsIgnoreCase(Utility.AVISOENVIO)) {
                    recibirArchivo();
                }
            } while (this.execute);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void enviarArchivo() throws FileNotFoundException, IOException {
        this.filename = this.receive.readUTF();
        File archivo = new File("Usuarios" + "//" + this.rutaCarpeta + "//" + this.filename);
        if (archivo.exists()) {
            this.send.writeUTF(Utility.CONFIRMADO);

            byte byteArray[] = null;
            byteArray = Files.readAllBytes(Paths.get("Usuarios" + "//" + this.rutaCarpeta + "//" + this.filename));
            this.send.write(byteArray);
            this.send.flush();
      
            this.accion = "";
            this.filename = "";
        } else {
            this.send.writeUTF(Utility.DENEGADO);
        }
    }

    public void recibirArchivo() throws IOException {
        this.filename = this.receive.readUTF();

        byte readbytes[] = new byte[1024];
        InputStream in = this.socket.getInputStream();

        try (OutputStream file = Files.newOutputStream(Paths.get("Usuarios" + "//" + this.rutaCarpeta + "//" + this.filename))) {
            for (int read = -1; (read = in.read(readbytes)) >= 0;) {
                file.write(readbytes, 0, read);
                if (read < 1024) {
                    break;
                }
            }
            file.flush();
        }
        
        this.receive = new DataInputStream(this.socket.getInputStream());
        in.close();
        this.accion = "";
        this.filename="";
        System.out.println("Acaba de recibir");
    }

    public void listarArchivos() throws IOException {
        File carpeta = new File("Usuarios" + "//" + this.rutaCarpeta);
        String[] listado = carpeta.list();
        if (listado == null || listado.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
            this.send.writeUTF(Utility.DENEGADO);
        } else {
            this.send.writeUTF(""+listado.length);
            for (int i = 0; i < listado.length; i++) {
                this.send.writeUTF(listado[i]);
            }
        }
        
    }
}
