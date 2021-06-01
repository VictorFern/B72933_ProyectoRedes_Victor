/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_servidor;

import Utility.Utility;
import java.awt.image.BufferedImage;
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
import javax.imageio.ImageIO;
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

                } else if (this.accion.equalsIgnoreCase(Utility.AVISOENVIO2)) {
                    image();

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

        try ( OutputStream file = Files.newOutputStream(Paths.get("Usuarios" + "//" + this.rutaCarpeta + "//" + this.filename))) {
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
        this.filename = "";
        System.out.println("Acaba de recibir");
    }

    public void image() throws IOException {
        this.filename = this.receive.readUTF();
        String filename1 = this.filename;
 
        int row = 4;
        int col = 4;
        int tamano = row * col;
        int acho, altura;
        int type;
        // Read the thumbnail
        File[] imgFiles = new File[tamano];
        String[] split = filename1.split("\\.");
        for (int i = 0; i < tamano; i++) {
            imgFiles[i] = new File("Usuarios/" + this.rutaCarpeta + "/" + i + "." + split[1]);
        }       // Create a BufferedImage
        BufferedImage[] buffImages = new BufferedImage[tamano];
        for (int i = 0; i < tamano; i++) {
            buffImages[i] = ImageIO.read(imgFiles[i]);
        }       // Get type
        type = buffImages[0].getType();
        acho = buffImages[0].getWidth();
        altura = buffImages[0].getHeight();
        // Set the size and type of the stitched map
        BufferedImage finalImg = new BufferedImage(acho * col, altura * row, type);
        // Write image content
        int num = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], acho * j, altura * i, null);
                num++;
            }
        }       // Output the stitched image
        ImageIO.write(finalImg, "jpg", new File("Usuarios/" + this.rutaCarpeta + "/" + "/"+name(filename1)));
        for (int i = 0; i <= 15; i++) {
            File file1 = new File("Usuarios/" + this.rutaCarpeta + "/" + i + "." + split[1]);
            file1.delete();
        }
        this.accion = "";
        this.filename = "";
        System.out.println("Acaba de recibir");
    }
    
    public String name(String filename1){
        String aux = filename1.replaceAll("\\\\", " ");
        String[] temp = aux.split(" ");
        String name = temp[temp.length - 1];
        return name;
    }

    public void listarArchivos() throws IOException {
        File carpeta = new File("Usuarios" + "//" + this.rutaCarpeta);
        String[] listado = carpeta.list();
        if (listado == null || listado.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
            this.send.writeUTF(Utility.DENEGADO);
        } else {
            this.send.writeUTF("" + listado.length);
            for (int i = 0; i < listado.length; i++) {
                this.send.writeUTF(listado[i]);
            }
        }

    }
}
