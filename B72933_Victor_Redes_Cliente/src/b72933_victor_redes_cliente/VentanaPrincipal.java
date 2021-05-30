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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipal extends JFrame implements ActionListener, MouseListener {

    private JMenuBar jmbBarra;
    private JMenu jmMenu;
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
        /* Hace que las casillas  */
        this.modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };

        this.tabla = new JTable();
        this.tabla.setBounds(0, 0, 800, 600);
        this.tabla.setPreferredScrollableViewportSize(new Dimension(500, 80));
        this.tabla.addMouseListener(this);

        JScrollPane scrollpane = new JScrollPane(tabla);
        getContentPane().add(scrollpane, BorderLayout.CENTER);

        this.add(this.tabla);

        this.jmbBarra = new JMenuBar();
        this.jmMenu = new JMenu("Menú");

        this.jmiActualizar = new JMenuItem("Actualizar");
        this.jmiActualizar.addActionListener(this);

        this.jmiCargar = new JMenuItem("Cargar");
        this.jmiCargar.addActionListener(this);

        this.jmMenu.add(this.jmiActualizar);
        this.jmMenu.add(this.jmiCargar);
        this.jmbBarra.add(this.jmMenu);

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
        this.tabla.addMouseListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == this.jmiActualizar) {
                this.cliente.descargarListaArchivos();
                actualizarJTable();
            } else if (ae.getSource() == this.jmiCargar) {

                JFileChooser fileChooser = new JFileChooser();
                int seleccion = fileChooser.showSaveDialog(this);

                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();

                    this.cliente.setFilename(file.getName());
                    this.cliente.setPath(file.getAbsolutePath());
                    this.cliente.enviarArchivo();
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            /* Hace que solo funncione con doble click */
            if (e.getClickCount() == 2) {
                JTable temp = (JTable) e.getSource();
                int r = temp.getSelectedRow();
                int c = temp.getSelectedColumn();

                String mensaje = (String) this.modelo.getValueAt(r, c);

                if (mensaje.equalsIgnoreCase("")) {
                    return;
                }

                this.cliente.setFilename(mensaje);
                this.cliente.descargarArchivo();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
