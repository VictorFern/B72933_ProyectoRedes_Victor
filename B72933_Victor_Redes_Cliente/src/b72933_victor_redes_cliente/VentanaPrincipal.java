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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipal extends JFrame implements ActionListener {

    private JMenuBar jmbBarra;
    private JMenu jmMenuCarga;
    private JMenu jmMenuActualizar;
    private JMenuItem jmiActualizar;
    private JMenuItem jmiCargar;

    private JTable tabla;
    private DefaultTableModel modelo;

    private HiloClient cliente;

    public VentanaPrincipal(HiloClient cliente) {
        super("Cliente TFTP");

        this.cliente = cliente;

        this.setLayout(null);
        this.setSize(800, 600);
        init();
    }

    private void init() {
        this.modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };

        this.tabla = new JTable();
        this.tabla.setBounds(0, 0, 800, 600);
        this.tabla.setPreferredScrollableViewportSize(new Dimension(500, 80));

        JScrollPane scrollpane = new JScrollPane(tabla);
        getContentPane().add(scrollpane, BorderLayout.CENTER);

        this.add(this.tabla);

        this.jmbBarra = new JMenuBar();
        this.jmMenuCarga = new JMenu("Imagen");
        this.jmMenuActualizar = new JMenu("Actualizar");

        this.jmiActualizar = new JMenuItem("Actualizar");
        this.jmiActualizar.addActionListener(this);

        this.jmiCargar = new JMenuItem("Cargar Imagen");
        this.jmiCargar.addActionListener(this);

        this.jmMenuActualizar.add(this.jmiActualizar);
        this.jmMenuCarga.add(this.jmiCargar);
        this.jmbBarra.add(this.jmMenuCarga);
        this.jmbBarra.add(this.jmMenuActualizar);

        setJMenuBar(this.jmbBarra);
    }

    private void actualizarJTable() {
        Object filas[] = this.cliente.getListaDeArchivos();
        if (filas == null) {
            return;
        }
        this.modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };

        this.modelo.addColumn("Nombres", filas);

        this.modelo.fireTableStructureChanged();
        this.modelo.fireTableDataChanged();

        this.tabla.setModel(this.modelo);
        this.revalidate();
    }

    private void delete(String file) {
        String[] split = file.split("\\.");
        for (int i = 0; i <= 15; i++) {
            File file1 = new File("src/img/" + i + "." + split[1]);
            file1.delete();
        }
    }
    
     public String tipo(String filename1){
        String aux = "";
        aux = filename1.replaceAll("^.*\\.(.*)$","$1");
         System.out.println(aux);
        return aux;
    }

    private void image(String file) throws IOException {
        File fileaux = new File(file);
        FileInputStream fileinput = new FileInputStream(fileaux);
        BufferedImage image = ImageIO.read(fileinput);
        // Split into 4 * 4 (16) small map
        int rows = 4;
        int cols = 4;
        int tamano = rows * cols;
        // Calculate the width and height of each thumbnail
        int acho = image.getWidth() / cols;
        int altura = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[tamano];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                // Set the size and type of the thumbnail
                imgs[count] = new BufferedImage(acho, altura, image.getType());

                // Write image content
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0,
                        acho, altura,
                        acho * y, altura * x,
                        acho * y + acho,
                        altura * x + altura, null);
                gr.dispose();
            }
        }
        String[] split = file.split("\\.");
        // output thumbnail
        for (int i = 0; i < imgs.length; i++) {
            File file1 = new File("src/img/" + i + "." + split[1]);
            ImageIO.write(imgs[i], tipo(file), file1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        File file1 = null; // imagenes cortanas
        File file = null; // file choser

        try {
            if (ae.getSource() == this.jmiActualizar) {
                this.cliente.listarArchivos();
                actualizarJTable();
            } else if (ae.getSource() == this.jmiCargar) {

                JFileChooser fileChooser = new JFileChooser();
                int seleccion = fileChooser.showSaveDialog(this);

                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    System.out.println(file.getAbsolutePath());
                    String[] split = file.getAbsolutePath().split("\\.");
                    for (int i = 0; i <= 15; i++) {
                        image(file.getAbsolutePath());
                        file1 = new File("src/img/" + i + "." + split[1]);
                        this.cliente.setFilename(file1.getName());
                        this.cliente.setPath(file1.getAbsolutePath());
                        this.cliente.enviarArchivo();
                    }
                    
                    this.cliente.setFilename("");
                    this.cliente.setPath("");
                    this.cliente.setFilename(file.getAbsolutePath());
                    this.cliente.setPath(file.getName());
                    System.out.println("path: " + this.cliente.getFilename());
                    this.cliente.image();
                }
                delete(file1.getAbsolutePath());

            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

}
