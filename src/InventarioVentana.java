import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InventarioVentana extends JFrame {
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private JTextField nombreField, descripcionField, precioField, stockField;
    private JButton agregarButton, eliminarButton, actualizarButton;
    private Connection conexion; // Agrega la variable de conexión

    public InventarioVentana() {
        conectar(); // Conectar a la base de datos en el constructor
        setTitle("Ventana de Inventario");
        setSize(800, 600);
        setLocationRelativeTo(null); // Centrar la ventana
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Crear la tabla para mostrar los productos
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Precio", "Stock"}, 0);
        productosTable = new JTable(tableModel);
        cargarProductos();

        // Crear panel de formulario para agregar/editar productos
        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Nombre:"));
        nombreField = new JTextField();
        formPanel.add(nombreField);

        formPanel.add(new JLabel("Descripción:"));
        descripcionField = new JTextField();
        formPanel.add(descripcionField);

        formPanel.add(new JLabel("Precio:"));
        precioField = new JTextField();
        formPanel.add(precioField);

        formPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        formPanel.add(stockField);

        // Crear botones
        agregarButton = new JButton("Agregar Producto");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });

        eliminarButton = new JButton("Eliminar Producto");
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });

        actualizarButton = new JButton("Actualizar Producto");
        actualizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProducto();
            }
        });

        // Panel para los botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(agregarButton);
        buttonPanel.add(eliminarButton);
        buttonPanel.add(actualizarButton);

        // Añadir componentes a la ventana
        add(new JScrollPane(productosTable), BorderLayout.CENTER);
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarProductos() {
        // Limpiar la tabla antes de cargar los productos
        tableModel.setRowCount(0);
        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM PRODUCTOS")) {

            while (resultSet.next()) {
                int id = resultSet.getInt("ID_PRODUCTO");
                String nombre = resultSet.getString("NOMBRE");
                String descripcion = resultSet.getString("DESCRIPCION");
                double precio = resultSet.getDouble("PRECIO");
                int stock = resultSet.getInt("STOCK");
                tableModel.addRow(new Object[]{id, nombre, descripcion, precio, stock});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los productos: " + e.getMessage());
        }
    }

    private void agregarProducto() {
        String nombre = nombreField.getText();
        String descripcion = descripcionField.getText();
        double precio = Double.parseDouble(precioField.getText());
        int stock = Integer.parseInt(stockField.getText());

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement invStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");

            // Insertar en PRODUCTOS
            statement = connection.prepareStatement("INSERT INTO PRODUCTOS (NOMBRE, DESCRIPCION, PRECIO, STOCK) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, nombre);
            statement.setString(2, descripcion);
            statement.setDouble(3, precio);
            statement.setInt(4, stock);
            statement.executeUpdate();

            // Obtener el ID del producto recién agregado
            ResultSet generatedKeys = statement.getGeneratedKeys();
            int idProducto = 0;
            if (generatedKeys.next()) {
                idProducto = generatedKeys.getInt(1);
            }

            // Insertar en INVENTARIOS
            invStatement = connection.prepareStatement("INSERT INTO INVENTARIOS (ID_PRODUCTO, NOMBRE, CANTIDAD) VALUES (?, ?, ?)");
            invStatement.setInt(1, idProducto);
            invStatement.setString(2, nombre); // Insertar el nombre del producto
            invStatement.setInt(3, stock); // Usar el stock inicial como cantidad
            invStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Producto agregado con éxito.");
            cargarProductos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar el producto: " + e.getMessage());
        } finally {
            try {
                if (statement != null) statement.close();
                if (invStatement != null) invStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }



    private void eliminarProducto() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow != -1) {
            int idProducto = (int) tableModel.getValueAt(selectedRow, 0);
            try (PreparedStatement statement = conexion.prepareStatement("DELETE FROM PRODUCTOS WHERE ID_PRODUCTO = ?")) {
                statement.setInt(1, idProducto);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.");
                cargarProductos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar el producto: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
        }
    }

    private void actualizarProducto() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow != -1) {
            int idProducto = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = nombreField.getText();
            String descripcion = descripcionField.getText();
            double precio = Double.parseDouble(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());

            try (PreparedStatement statement = conexion.prepareStatement("UPDATE PRODUCTOS SET NOMBRE = ?, DESCRIPCION = ?, PRECIO = ?, STOCK = ? WHERE ID_PRODUCTO = ?")) {
                statement.setString(1, nombre);
                statement.setString(2, descripcion);
                statement.setDouble(3, precio);
                statement.setInt(4, stock);
                statement.setInt(5, idProducto);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Producto actualizado con éxito.");
                cargarProductos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar el producto: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar.");
        }
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
    }

    public static void main(String[] args) {
        InventarioVentana ventanaInventario = new InventarioVentana();
        ventanaInventario.setVisible(true);
    }
}
