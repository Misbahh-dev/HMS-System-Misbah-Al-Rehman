package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginView() {
        // JPanel setup
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title Panel
        JLabel lblTitle = new JLabel("HMS Login System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // User ID
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.3;
        formPanel.add(new JLabel("User ID:"), gc);
        
        txtUserId = new JTextField(15);
        gc.gridx = 1; gc.weightx = 0.7;
        formPanel.add(txtUserId, gc);

        // Password
        gc.gridx = 0; gc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gc);
        
        txtPassword = new JPasswordField(15);
        gc.gridx = 1;
        formPanel.add(txtPassword, gc);

        // Role
        gc.gridx = 0; gc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gc);
        
        cmbRole = new JComboBox<>(new String[]{"Select Role", "Patient", "Clinician", "Staff", "Admin"});
        gc.gridx = 1;
        formPanel.add(cmbRole, gc);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gc);

        // Message Label
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setForeground(Color.RED);
        gc.gridy = 4;
        formPanel.add(lblMessage, gc);

        add(formPanel, BorderLayout.CENTER);

        // Footer
        JLabel lblFooter = new JLabel("Use ID as password for demo users", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 10));
        lblFooter.setForeground(Color.GRAY);
        add(lblFooter, BorderLayout.SOUTH);
    }

    public String getUserId() { 
        return txtUserId.getText().trim(); 
    }
    
    public String getPassword() { 
        return new String(txtPassword.getPassword()); 
    }
    
    public String getSelectedRole() { 
        String role = (String) cmbRole.getSelectedItem();
        return role.equals("Select Role") ? "" : role.toLowerCase();
    }
    
    public void setLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
    
    public void showMessage(String message, boolean isError) {
        lblMessage.setForeground(isError ? Color.RED : Color.GREEN);
        lblMessage.setText(message);
    }
    
    public void clearFields() {
        txtUserId.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        lblMessage.setText(" ");
    }
    
    // Optional: Set preferred size for the panel
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }
}