import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VeterinarioVentana extends JFrame {
    private JButton cerrarSesionButton; // Botón para cerrar sesión

    public VeterinarioVentana() {
        setTitle("Ventana de Veterinario");
        setSize(600, 400);
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear la barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Crear el menú "Historial"
        JMenu historialMenu = new JMenu("Menu");

        // Crear la opción del menú para el historial de citas
        JMenuItem historialCitasItem = new JMenuItem("Historial de Citas");
        historialCitasItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialCitas();
            }
        });

        // Agregar la opción al menú
        historialMenu.add(historialCitasItem);

        // Agregar el menú a la barra de menú
        menuBar.add(historialMenu);

        // Configurar la barra de menú en la ventana
        setJMenuBar(menuBar);

        // Botón para cerrar sesión
        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        // Panel para el botón de cerrar sesión
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cerrarSesionButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void abrirHistorialCitas() {
        // Crear e instanciar la ventana de historial de citas pasando el rol "VETERINARIO"
        HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("VETERINARIO", 1);
        historialCitasVentana.setVisible(true);
    }

    private void cerrarSesion() {
        // Cerrar la ventana actual y volver a la clase LOGIN
        dispose(); // Cierra la ventana actual
        LOGIN loginVentana = new LOGIN(); // Crear una instancia de la clase LOGIN
        loginVentana.setVisible(true); // Mostrar la ventana de LOGIN
    }

    public static void main(String[] args) {
        VeterinarioVentana ventanaVeterinario = new VeterinarioVentana();
        ventanaVeterinario.setVisible(true);
    }
}
