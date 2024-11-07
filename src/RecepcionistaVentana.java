import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RecepcionistaVentana extends JFrame {
    private JTable clientesTable; // Tabla para mostrar información de clientes
    private JButton verHistorialCitasButton; // Botón para acceder al historial de citas
    private JButton verHistorialFacturasButton; // Botón para acceder al historial de facturas
    private JButton cerrarSesionButton; // Botón para cerrar sesión
    private Connection conexion; // Conexión a la base de datos

    public RecepcionistaVentana() {
        // Conectar a la base de datos
        conectar();

        // Configuración de la ventana
        setTitle("Recepcionista - Gestión de Clientes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Inicializar y configurar la tabla de clientes
        clientesTable = new JTable(); // Aquí deberías configurar el modelo con los datos de los clientes
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Botón para acceder al historial de citas
        verHistorialCitasButton = new JButton("Ver Historial de Citas");
        verHistorialCitasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialCitas();
            }
        });

        // Botón para acceder al historial de facturas
        verHistorialFacturasButton = new JButton("Ver Historial de Facturas");
        verHistorialFacturasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirHistorialFacturas();
            }
        });

        // Botón para cerrar sesión
        cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(verHistorialCitasButton);
        buttonPanel.add(verHistorialFacturasButton);
        buttonPanel.add(cerrarSesionButton); // Agregar botón de cerrar sesión
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Método para conectar a la base de datos
    private void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void abrirHistorialCitas() {
        // Abre la ventana de historial de citas pasando el rol "RECEPCIONISTA"
        HistorialCitasVentana historialCitasVentana = new HistorialCitasVentana("RECEPCIONISTA", 1);
        historialCitasVentana.setVisible(true);
    }

    private void abrirHistorialFacturas() {
        // Abre la ventana de historial de facturas pasando el rol "RECEPCIONISTA"
        HistorialFacturaVentana historialFacturasVentana = new HistorialFacturaVentana("RECEPCIONISTA");
        historialFacturasVentana.setVisible(true);
    }

    private void cerrarSesion() {
        // Cerrar la ventana actual y volver a la clase LOGIN
        dispose(); // Cierra la ventana actual
        LOGIN loginVentana = new LOGIN(); // Crear una instancia de la clase LOGIN
        loginVentana.setVisible(true); // Mostrar la ventana de LOGIN
    }

    public static void main(String[] args) {
        // Crear y mostrar la ventana de recepcionista
        SwingUtilities.invokeLater(() -> {
            RecepcionistaVentana ventana = new RecepcionistaVentana();
            ventana.setVisible(true);
        });
    }
}
