/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package b72933_victor_redes_cliente;

import Utility.Utility;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author Victor
 */
public class HiloClient {

    private boolean execute;

    private String filename;
    private String path;
    private String listaDeArchivos[];
    private String nombrelog;
    private Socket socket;

    private DataOutputStream send;
    private DataInputStream receive;
    private InetAddress address;

    public HiloClient(String ip, String nombre) throws IOException {
        this.execute = true;
        this.filename = "";
        this.path = "";
        this.listaDeArchivos = null;
        this.nombrelog = nombre;

        this.address = InetAddress.getByName(ip);

        this.socket = new Socket(address, Utility.SOCKETNUMBER);

        this.send = new DataOutputStream(this.socket.getOutputStream());
        this.receive = new DataInputStream(this.socket.getInputStream());
    }

    public void listarArchivos() throws IOException {
        identificarse();
        this.send.writeUTF(Utility.AVISOLISTAR);

        String mensaje = this.receive.readUTF();

        if (mensaje.equalsIgnoreCase(Utility.DENEGADO)) {
            System.err.println("No se encontraron archivos para listar");
            return;
        }

        try {
            int cantidadArchivos = Integer.parseInt(mensaje);
            this.listaDeArchivos = new String[cantidadArchivos];

            for (int i = 0; i < cantidadArchivos; i++) {
                this.listaDeArchivos[i] = this.receive.readUTF();
            }
        } catch (NumberFormatException e) {
            System.err.println(e);
        }
    }

    public void enviarArchivo() throws FileNotFoundException, IOException {
        identificarse();
        if (!this.filename.equalsIgnoreCase("")) {

            /* Avisa al servidor que se le enviara un archivo */
            this.send.writeUTF(Utility.AVISOENVIO);

            /* Se envia el nombre del archivo */
            this.send.writeUTF(this.filename);

            byte byteArray[] = null;
            byteArray = Files.readAllBytes(Paths.get(this.path));
            this.send.write(byteArray);
            this.send.flush();

            this.send.close();

            this.socket = new Socket(this.address, Utility.SOCKETNUMBER);
            this.send = new DataOutputStream(this.socket.getOutputStream());
            this.receive = new DataInputStream(this.socket.getInputStream());
            identificarse();
            this.filename = "";
        }
    }

    public void image() throws FileNotFoundException, IOException {
        if (!this.filename.equalsIgnoreCase("")) {
            this.send.writeUTF(Utility.AVISOENVIO2);
            this.send.writeUTF(this.filename);
            this.send.close();

            this.socket = new Socket(this.address, Utility.SOCKETNUMBER);
            this.send = new DataOutputStream(this.socket.getOutputStream());
            this.receive = new DataInputStream(this.socket.getInputStream());
             this.filename = "";
        }
    }

    public void descargarArchivo() throws IOException {
        identificarse();
        this.send.writeUTF(Utility.AVISODESCARGA);
        this.send.writeUTF(this.filename);
        String mensaje = this.receive.readUTF();
        if (mensaje.equalsIgnoreCase(Utility.CONFIRMADO)) {
            byte readbytes[] = new byte[4096];
            InputStream in = this.socket.getInputStream();
            try ( OutputStream file = Files.newOutputStream(Paths.get(this.filename))) {
                for (int read = -1; (read = in.read(readbytes)) > 0;) {
                    file.write(readbytes, 0, read);
                    if (read < 4096) {
                        break;
                    }
                }
                file.flush();
                file.close();
            }
            this.receive.close();
            this.socket = new Socket(this.address, Utility.SOCKETNUMBER);
            this.receive = new DataInputStream(this.socket.getInputStream());
            this.send = new DataOutputStream(this.socket.getOutputStream());
            in.close();
            identificarse();
        }
        this.filename = "";
    }

    public void identificarse() throws IOException {
        this.send.writeUTF(Utility.IDENTIFICAR);
        this.send.writeUTF(this.nombrelog);
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String[] getListaDeArchivos() {
        return listaDeArchivos;
    }

    public void setListaDeArchivos(String[] listaDeArchivos) {
        this.listaDeArchivos = listaDeArchivos;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNombrelog() {
        return nombrelog;
    }

    public void setNombrelog(String nombrelog) {
        this.nombrelog = nombrelog;
    }
}
