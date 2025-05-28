import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrmMain extends JFrame {
    private JTextField txtFileName;
    private JTextField txtNClusters;
    private JTextField txtCostoSolucion;
    private JTextField txtInitTime;
    private JTextField txtFinalTime;
    private JTextField txtFileResult;
    private JButton cmdAbrir;
    private JButton cmdClasificar;
    private JButton cmdSalir;

    private modImpExp modImpExp = new modImpExp();
    private modPam modPam = new modPam();

    public FrmMain() {
        setTitle("PAM");
        setSize(1020, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Etiquetas
        JLabel lblFileName = new JLabel("Matriz de disimilitud:");
        lblFileName.setBounds(50, 50, 150, 30);
        add(lblFileName);

        JLabel lblNClusters = new JLabel("Número de grupos:");
        lblNClusters.setBounds(50, 100, 150, 30);
        add(lblNClusters);

        JLabel lblFileResult = new JLabel("Archivo de resultado:");
        lblFileResult.setBounds(50, 150, 150, 30);
        add(lblFileResult);

        JLabel lblCostoSolucion = new JLabel("Costo de la solución:");
        lblCostoSolucion.setBounds(50, 200, 150, 30);
        add(lblCostoSolucion);

        JLabel lblInitTime = new JLabel("Hora inicial:");
        lblInitTime.setBounds(500, 100, 100, 30);
        add(lblInitTime);

        JLabel lblFinalTime = new JLabel("Hora final:");
        lblFinalTime.setBounds(500, 150, 100, 30);
        add(lblFinalTime);

        // Campos de texto
        txtFileName = new JTextField();
        txtFileName.setBounds(200, 50, 600, 30);
        txtFileName.setEnabled(false);
        add(txtFileName);

        txtNClusters = new JTextField();
        txtNClusters.setBounds(200, 100, 100, 30);
        add(txtNClusters);

        txtFileResult = new JTextField();
        txtFileResult.setBounds(200, 150, 200, 30);
        add(txtFileResult);

        txtCostoSolucion = new JTextField();
        txtCostoSolucion.setBounds(200, 200, 200, 30);
        add(txtCostoSolucion);

        txtInitTime = new JTextField();
        txtInitTime.setBounds(600, 100, 200, 30);
        add(txtInitTime);

        txtFinalTime = new JTextField();
        txtFinalTime.setBounds(600, 150, 200, 30);
        add(txtFinalTime);

        // Botones
        cmdAbrir = new JButton("Abrir");
        cmdAbrir.setBounds(820, 50, 100, 30);
        add(cmdAbrir);

        cmdClasificar = new JButton("Clasificar");
        cmdClasificar.setBounds(820, 100, 100, 30);
        add(cmdClasificar);

        cmdSalir = new JButton("Salir");
        cmdSalir.setBounds(820, 150, 100, 30);
        add(cmdSalir);

        // Acciones de los botones
        cmdAbrir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    txtFileName.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        cmdClasificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (txtNClusters.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese el número de clusters.");
                        return;
                    }

                    if (txtFileName.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, seleccione un archivo.");
                        return;
                    }

                    if (txtFileResult.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingrese el nombre del archivo de resultado.");
                        return;
                    }

                    // Importar la matriz de costos
                    modImpExp.importMatrixCost(txtFileName.getText());
                    modPam.d = modImpExp.d;
                    modPam.nObjects = modImpExp.nObjects;

                    // Registrar hora inicial
                    txtInitTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));

                    // Ejecutar el algoritmo PAM
                    modPam.pam(Integer.parseInt(txtNClusters.getText()));
                    txtCostoSolucion.setText(String.valueOf(modPam.dCostoSolucion));

                    // Registrar hora final
                    txtFinalTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));

                    // Guardar los resultados en un archivo
                    sendClustersToFile(txtFileResult.getText());

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Error: El número de clusters debe ser un valor entero.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error al leer el archivo: " + ex.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error inesperado: " + ex.getMessage());
                }
            }
        });

        cmdSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // Método para guardar los clusters en un archivo
    private void sendClustersToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < modPam.kClusters.length; i++) {
                writer.write("Cluster " + (i + 1) + ":\n");
                for (int j = 0; j < modPam.kClusters[i].items.length; j++) {
                    writer.write(modImpExp.nameObjects[modPam.kClusters[i].items[j]] + "\n");
                }
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(null, "Resultados guardados en " + fileName);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar el archivo: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new FrmMain().setVisible(true);
    }
}