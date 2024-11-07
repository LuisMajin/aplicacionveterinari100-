import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet; // Asegúrate de importar ResultSet
import java.sql.SQLException;
import java.sql.Statement; // Importa Statement aquí


public class RegistroUsuario extends JFrame {
    private JPanel panelRegistro;
    private JTextField textNombreUsuario;
    private JPasswordField textContraseña;
    private JButton registrarButton;
    private JLabel REGISTRARSE;
    private JLabel NOMBRE;
    private JLabel CONTRASEÑA;
    private JTextField textNit;
    private JTextField textTelefono;
    private JTextField textCorreo;
    private JLabel NIT;
    private JLabel TELEFONO;
    private JLabel CORREO;

    public RegistroUsuario() {
        initComponents();

        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });

        setContentPane(panelRegistro);
        setTitle("Registro de Usuario");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Este método debería ser generado automáticamente si usas IntelliJ para cargar el formulario .form
        // Si no se genera, asegúrate de que el archivo .form esté correctamente enlazado a la clase.
    }

    private void registrarUsuario() {
        String nombreUsuario = textNombreUsuario.getText();
        String contrasena = new String(textContraseña.getPassword());
        String NIT = textNit.getText();
        String TELEFONO = textTelefono.getText();
        String CORREO = textCorreo.getText();

        if (nombreUsuario.isEmpty() || contrasena.isEmpty() || NIT.isEmpty() || TELEFONO.isEmpty() || CORREO.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmtLogin = null;
        PreparedStatement pstmtRegistro = null;
        PreparedStatement pstmtCliente = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/veterinaria", "root", "12345");

            // 1. Registrar en la tabla LOGIN
            String sqlLogin = "INSERT INTO LOGIN (USUARIO, PASS, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?, 'CLIENTE')";
            pstmtLogin = conn.prepareStatement(sqlLogin, Statement.RETURN_GENERATED_KEYS);
            pstmtLogin.setString(1, nombreUsuario);
            pstmtLogin.setString(2, contrasena); // Considera hashear esta contraseña
            pstmtLogin.setString(3, NIT);
            pstmtLogin.setString(4, TELEFONO);
            pstmtLogin.setString(5, CORREO);

            int filasAfectadasLogin = pstmtLogin.executeUpdate();

            if (filasAfectadasLogin > 0) {
                // Obtener el ID_USUARIO generado para la tabla LOGIN
                ResultSet generatedKeys = pstmtLogin.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idUsuario = generatedKeys.getInt(1);  // ID_USUARIO obtenido de LOGIN

                    // 2. Registrar en la tabla REGISTRO (usamos el ID_USUARIO aquí)
                    String sqlRegistro = "INSERT INTO REGISTRO (ID_USUARIO, USUARIO, PASS, NIT, TELEFONO, CORREO, ROL) VALUES (?, ?, ?, ?, ?, ?, 'CLIENTE')";
                    pstmtRegistro = conn.prepareStatement(sqlRegistro, Statement.RETURN_GENERATED_KEYS);
                    pstmtRegistro.setInt(1, idUsuario);  // Insertamos el ID_USUARIO en REGISTRO
                    pstmtRegistro.setString(2, nombreUsuario);
                    pstmtRegistro.setString(3, contrasena); // Considera hashear esta contraseña
                    pstmtRegistro.setString(4, NIT);
                    pstmtRegistro.setString(5, TELEFONO);
                    pstmtRegistro.setString(6, CORREO);
                    int filasAfectadasRegistro = pstmtRegistro.executeUpdate();

                    if (filasAfectadasRegistro > 0) {
                        // Obtener el ID_REGISTRO generado para la tabla REGISTRO
                        ResultSet generatedKeysRegistro = pstmtRegistro.getGeneratedKeys();
                        if (generatedKeysRegistro.next()) {
                            int idRegistro = generatedKeysRegistro.getInt(1);  // ID_REGISTRO obtenido de REGISTRO

                            // 3. Registrar en la tabla CLIENTES (usamos el ID_REGISTRO aquí)
                            String sqlCliente = "INSERT INTO CLIENTES (ID_REGISTRO, USUARIO, TELEFONO, CORREO) VALUES (?, ?, ?, ?)";
                            pstmtCliente = conn.prepareStatement(sqlCliente);
                            pstmtCliente.setInt(1, idRegistro);  // Usamos ID_REGISTRO de REGISTRO para CLIENTES
                            pstmtCliente.setString(2, nombreUsuario); // Almacena el nombre de usuario
                            pstmtCliente.setString(3, TELEFONO);
                            pstmtCliente.setString(4, CORREO); // Almacena el correo
                            pstmtCliente.executeUpdate();

                            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");

                            // Limpiar campos después del registro
                            textNombreUsuario.setText("");
                            textContraseña.setText("");
                            textNit.setText("");
                            textTelefono.setText("");
                            textCorreo.setText("");

                            LOGIN login = new LOGIN();
                            login.setVisible(true);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al registrar en REGISTRO.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario en LOGIN.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario: " + e.getMessage());
        } finally {
            try {
                if (pstmtLogin != null) pstmtLogin.close();
                if (pstmtRegistro != null) pstmtRegistro.close();
                if (pstmtCliente != null) pstmtCliente.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegistroUsuario ventana = new RegistroUsuario();
            ventana.setVisible(true);
        });
    }
}
