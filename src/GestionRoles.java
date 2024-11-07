import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionRoles extends JFrame {
    private JTextField idText;
    private JComboBox<String> rolComboBox;
    private JTextField usuarioText;
    private JTextField nitText;
    private JTextField telefonoText;
    private JTextField correoText;
    private JPasswordField passText;
    private JButton consultarBoton;
    private JButton agregarBoton;
    private JButton actualizarBoton;
    private JButton eliminarBoton;
    private JTable tabla;
    private JPanel panelRoles;
    private JList<String> lista; // Declaración de la lista si es necesaria
    private DefaultTableModel modTabla;
    private Connection conexion;
    private PreparedStatement ps;
    private Statement st;
    private ResultSet rs;
    private Integer idEliminado = null; // Variable para guardar el ID eliminado

    public void conectar() {
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");
            System.out.println("Conexión exitosa a la base de datos");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }


    public GestionRoles() {
        setTitle("Gestión de Roles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());



        // Crear panel principal
        panelRoles = new JPanel(new GridBagLayout());
        panelRoles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelRoles.setBackground(new Color(240, 240, 255));

        // Configuración de GridBagConstraints para organizar los componentes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiquetas y campos de texto
        JLabel idLabel = new JLabel("ID Usuario:");
        idText = new JTextField(10);
        JLabel rolLabel = new JLabel("Rol:");
        rolComboBox = new JComboBox<>(new String[]{"ADMINISTRADOR", "VETERINARIO", "RECEPCIONISTA", "CLIENTE"});
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioText = new JTextField(10);
        JLabel nitLabel = new JLabel("NIT:");
        nitText = new JTextField(10);
        JLabel telefonoLabel = new JLabel("Teléfono:");
        telefonoText = new JTextField(10);
        JLabel correoLabel = new JLabel("Correo:");
        correoText = new JTextField(10);
        JLabel passLabel = new JLabel("Contraseña:");
        passText = new JPasswordField(10);

        // Botones con color y estilo
        consultarBoton = crearBoton("Consultar", new Color(0, 102, 204));
        agregarBoton = crearBoton("Agregar", new Color(0, 153, 51));
        actualizarBoton = crearBoton("Actualizar", new Color(255, 153, 0));
        eliminarBoton = crearBoton("Eliminar", new Color(204, 0, 0));

        // Añadir componentes al panel
        int row = 0;
        panelRoles.add(idLabel, gbcPos(gbc, 0, row));
        panelRoles.add(idText, gbcPos(gbc, 1, row++));
        panelRoles.add(rolLabel, gbcPos(gbc, 0, row));
        panelRoles.add(rolComboBox, gbcPos(gbc, 1, row++));
        panelRoles.add(usuarioLabel, gbcPos(gbc, 0, row));
        panelRoles.add(usuarioText, gbcPos(gbc, 1, row++));
        panelRoles.add(nitLabel, gbcPos(gbc, 0, row));
        panelRoles.add(nitText, gbcPos(gbc, 1, row++));
        panelRoles.add(telefonoLabel, gbcPos(gbc, 0, row));
        panelRoles.add(telefonoText, gbcPos(gbc, 1, row++));
        panelRoles.add(correoLabel, gbcPos(gbc, 0, row));
        panelRoles.add(correoText, gbcPos(gbc, 1, row++));
        panelRoles.add(passLabel, gbcPos(gbc, 0, row));
        panelRoles.add(passText, gbcPos(gbc, 1, row++));

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botonesPanel.setBackground(new Color(240, 240, 255));
        botonesPanel.add(consultarBoton);
        botonesPanel.add(agregarBoton);
        botonesPanel.add(actualizarBoton);
        botonesPanel.add(eliminarBoton);

        // Tabla para mostrar los datos
        String[] columnas = {"ID Usuario", "Usuario", "Contraseña", "NIT", "Teléfono", "Correo", "Rol"};
        modTabla = new DefaultTableModel(null, columnas);
        tabla = new JTable(modTabla);
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Añadir paneles al frame
        add(panelRoles, BorderLayout.NORTH);
        add(botonesPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Configurar ActionListeners para los botones
        configurarListeners();

        pack();
        setLocationRelativeTo(null); // Centrar ventana
        setVisible(true);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(120, 30));
        return boton;
    }

    private GridBagConstraints gbcPos(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    // Método de configuración de listeners
    private void configurarListeners() {
        consultarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    consultar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        agregarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    insertar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        actualizarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    actualizar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
        eliminarBoton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    eliminar();
                } catch (SQLException ex) {
                    mostrarError(ex);
                }
            }
        });
    }

    // Métodos de operaciones en la base de datos

    public void consultar() throws SQLException {
        conectar();
        String sql = "SELECT ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL FROM LOGIN ";

        st = conexion.createStatement();
        rs = st.executeQuery(sql);
        modTabla.setRowCount(0); // Limpiar la tabla antes de cargar nuevos datos

        while (rs.next()) {
            Object[] fila = new Object[7]; // Cambia a 7 columnas
            fila[0] = rs.getInt("ID_USUARIO");
            fila[1] = rs.getString("USUARIO");
            fila[2] = rs.getString("PASS");
            fila[3] = rs.getString("NIT");
            fila[4] = rs.getString("TELEFONO");
            fila[5] = rs.getString("CORREO");
            fila[6] = rs.getString("ROL"); // Agrega el rol aquí
            modTabla.addRow(fila);
        }

        // Cerrar recursos
        rs.close();
        st.close();
        conexion.close();
    }

    public void eliminarUsuario(int id) throws SQLException {
        conectar();

        // Guardar el ID eliminado
        idEliminado = id;

        // Eliminar de la tabla REGISTRO
        String sqlDeleteRegistro = "DELETE FROM REGISTRO WHERE ID_REGISTRO = ?";
        ps = conexion.prepareStatement(sqlDeleteRegistro);
        ps.setInt(1, id);
        ps.executeUpdate();

        // Eliminar de la tabla LOGIN
        String sqlDeleteLogin = "DELETE FROM LOGIN WHERE ID_USUARIO = ?";
        ps = conexion.prepareStatement(sqlDeleteLogin);
        ps.setInt(1, id);
        ps.executeUpdate();

        JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
        ps.close();
        conexion.close();
    }

    public void insertar() throws SQLException {
        conectar();
        String rolSeleccionado = rolComboBox.getSelectedItem().toString();

        // Verificar que el rol seleccionado sea válido
        if (!esRolValido(rolSeleccionado)) {
            JOptionPane.showMessageDialog(this, "El rol seleccionado no es válido.");
            return;
        }

        try {
            // Verificar duplicados en LOGIN y REGISTRO
            String sqlCheck = "SELECT 1 FROM LOGIN WHERE USUARIO = ? OR CORREO = ? UNION SELECT 1 FROM REGISTRO WHERE USUARIO = ? OR CORREO = ?";
            ps = conexion.prepareStatement(sqlCheck);
            ps.setString(1, usuarioText.getText());
            ps.setString(2, correoText.getText());
            ps.setString(3, usuarioText.getText());
            ps.setString(4, correoText.getText());
            rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "El usuario o correo ya existen.");
                return;
            }

            // Obtener el ID disponible más bajo si existe un ID eliminado
            Integer idDisponible = idEliminado != null ? idEliminado : obtenerSiguienteId();
            if (idDisponible == null) {
                String sqlBuscarID = "SELECT MIN(ID_USUARIO + 1) AS id FROM LOGIN WHERE (ID_USUARIO + 1) NOT IN (SELECT ID_USUARIO FROM LOGIN)";
                st = conexion.createStatement();
                rs = st.executeQuery(sqlBuscarID);
                if (rs.next()) {
                    idDisponible = rs.getInt("id");
                }
            }

            // Inserción en LOGIN para todos los roles
            String sqlLogin = "INSERT INTO LOGIN (ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conexion.prepareStatement(sqlLogin);
            ps.setInt(1, idDisponible);
            ps.setString(2, usuarioText.getText());
            ps.setString(3, new String(passText.getPassword()));
            ps.setString(4, nitText.getText());
            ps.setString(5, telefonoText.getText());
            ps.setString(6, correoText.getText());
            ps.setString(7, rolSeleccionado);
            ps.executeUpdate();

            // Inserción en REGISTRO y CLIENTES solo si el rol es CLIENTE
            if (rolSeleccionado.equals("CLIENTE")) {
                String sqlRegistro = "INSERT INTO REGISTRO (ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = conexion.prepareStatement(sqlRegistro, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, idDisponible);
                ps.setString(2, usuarioText.getText());
                ps.setString(3, new String(passText.getPassword()));
                ps.setString(4, nitText.getText());
                ps.setString(5, telefonoText.getText());
                ps.setString(6, correoText.getText());
                ps.setString(7, rolSeleccionado);
                ps.executeUpdate();

                ResultSet generatedKeysRegistro = ps.getGeneratedKeys();
                int idRegistro = 0;
                if (generatedKeysRegistro.next()) {
                    idRegistro = generatedKeysRegistro.getInt(1);
                }

                // Inserción en CLIENTES
                String sqlClientes = "INSERT INTO CLIENTES (ID_REGISTRO, USUARIO, TELEFONO, CORREO) VALUES (?, ?, ?, ?)";
                ps = conexion.prepareStatement(sqlClientes);
                ps.setInt(1, idRegistro);
                ps.setString(2, usuarioText.getText());
                ps.setString(3, telefonoText.getText());
                ps.setString(4, correoText.getText());
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Usuario agregado exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al insertar usuario: " + e.getMessage());
        } finally {
            // Cerrar recursos
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (ps != null) ps.close();
            if (conexion != null) conexion.close();
            idEliminado = null; // Restablecer el ID eliminado
        }
    }

    // Método para obtener el siguiente ID disponible
    private int obtenerSiguienteId() throws SQLException {
        String sql = "SELECT IFNULL(MAX(ID_USUARIO), 0) + 1 AS siguiente_id FROM LOGIN";
        st = conexion.createStatement();
        rs = st.executeQuery(sql);
        int siguienteId = 1; // Por defecto si no hay IDs existentes

        if (rs.next()) {
            siguienteId = rs.getInt("siguiente_id");
        }

        // Cerrar recursos
        rs.close();
        st.close();
        return siguienteId;
    }


    public void actualizar() throws SQLException {
        conectar();

        // Asegurarse de que el campo ID no esté vacío
        if (idText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID válido.");
            return;
        }

        // Verificar si el usuario con ese ID existe
        String rolSeleccionado = rolComboBox.getSelectedItem().toString();
        String sqlCheck = (rolSeleccionado.equals("CLIENTE"))
                ? "SELECT COUNT(*) FROM REGISTRO WHERE ID_REGISTRO = ?"
                : "SELECT COUNT(*) FROM LOGIN WHERE ID_USUARIO = ?";

        ps = conexion.prepareStatement(sqlCheck);
        ps.setInt(1, Integer.parseInt(idText.getText()));
        ResultSet rs = ps.executeQuery();

        // Si no existe el ID, informar y salir
        if (rs.next() && rs.getInt(1) == 0) {
            JOptionPane.showMessageDialog(this, "El ID proporcionado no existe.");
            return;
        }

        // Preparar la consulta de actualización
        String sqlUpdate = (rolSeleccionado.equals("CLIENTE"))
                ? "UPDATE REGISTRO SET USUARIO = ?, PASS = ?, NIT = ?, TELEFONO = ?, CORREO = ? WHERE ID_REGISTRO = ?"
                : "UPDATE LOGIN SET USUARIO = ?, PASS = ?, NIT = ?, TELEFONO = ?, CORREO = ? WHERE ID_USUARIO = ?";

        ps = conexion.prepareStatement(sqlUpdate);
        ps.setString(1, usuarioText.getText());
        ps.setString(2, new String(passText.getPassword()));
        ps.setString(3, nitText.getText());
        ps.setString(4, telefonoText.getText());
        ps.setString(5, correoText.getText());
        ps.setInt(6, Integer.parseInt(idText.getText())); // Usar el ID del campo de texto

        // Ejecutar la actualización
        int filasAfectadas = ps.executeUpdate();
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(this, "Registro actualizado exitosamente.");
            consultar(); // Actualiza la tabla después de actualizar
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar el registro. Asegúrese de que el ID exista.");
        }

        // Cerrar recursos
        ps.close();
        rs.close();
        conexion.close();
    }

    public void eliminar() throws SQLException {
        int idUsuario = Integer.parseInt(idText.getText());
        if (idUsuario <= 0) {
            JOptionPane.showMessageDialog(this, "ID de usuario inválido.");
            return;
        }

        conectar();
        try {
            idEliminado = idUsuario; // Guardar el ID eliminado

            // Eliminar de CLIENTES si el rol es CLIENTE
            String sqlDeleteClientes = "DELETE FROM CLIENTES WHERE ID_REGISTRO = (SELECT ID_REGISTRO FROM REGISTRO WHERE ID_USUARIO = ?)";
            ps = conexion.prepareStatement(sqlDeleteClientes);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();

            // Eliminar de REGISTRO
            String sqlDeleteRegistro = "DELETE FROM REGISTRO WHERE ID_USUARIO = ?";
            ps = conexion.prepareStatement(sqlDeleteRegistro);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();

            // Eliminar de LOGIN
            String sqlDeleteLogin = "DELETE FROM LOGIN WHERE ID_USUARIO = ?";
            ps = conexion.prepareStatement(sqlDeleteLogin);
            ps.setInt(1, idUsuario);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente.");
        } finally {
            if (ps != null) ps.close();
            if (conexion != null) conexion.close();
        }
    }


    private boolean esRolValido(String rol) {
        return rol.equals("ADMINISTRADOR") || rol.equals("VETERINARIO") || rol.equals("RECEPCIONISTA") || rol.equals("CLIENTE");
    }

    // Método para mostrar errores
    private void mostrarError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(GestionRoles::new);
    }

}
