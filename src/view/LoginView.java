package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JPanel {
    // Login form input components
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnLogin;
    private JLabel lblMessage;

    public LoginView() {
        // Main panel layout with consistent spacing
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Application title panel
        JLabel lblTitle = new JLabel("HMS Login System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Main form panel with GridBag layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // User ID input field
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.3;
        formPanel.add(new JLabel("User ID:"), gc);
        
        txtUserId = new JTextField(15);
        gc.gridx = 1; gc.weightx = 0.7;
        formPanel.add(txtUserId, gc);

        // Password input field (masked)
        gc.gridx = 0; gc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gc);
        
        txtPassword = new JPasswordField(15);
        gc.gridx = 1;
        formPanel.add(txtPassword, gc);

        // Role selection dropdown
        gc.gridx = 0; gc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gc);
        //Made By Misbah Al Rehman. SRN: 24173647
        cmbRole = new JComboBox<>(new String[]{"Select Role", "Patient", "Clinician", "Staff", "Admin"});
        gc.gridx = 1;
        formPanel.add(cmbRole, gc);

        // Login action button
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 102, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        formPanel.add(btnLogin, gc);

        // Status message display area
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setForeground(Color.RED);
        gc.gridy = 4;
        formPanel.add(lblMessage, gc);

        add(formPanel, BorderLayout.CENTER);

        // Informational footer for demo users
        JLabel lblFooter = new JLabel("Use ID as password for demo users", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 10));
        lblFooter.setForeground(Color.GRAY);
        add(lblFooter, BorderLayout.SOUTH);
    }

    // Returns cleaned user ID input
    public String getUserId() { 
        return txtUserId.getText().trim(); 
    }
    
    // Returns password as plain text
    public String getPassword() { 
        return new String(txtPassword.getPassword()); 
    }
    
    // Returns selected role in lowercase format
    public String getSelectedRole() { 
        String role = (String) cmbRole.getSelectedItem();
        return role.equals("Select Role") ? "" : role.toLowerCase();
    }
    
    // Sets login button action listener
    public void setLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
    
    // Displays status message with appropriate coloring
    public void showMessage(String message, boolean isError) {
        lblMessage.setForeground(isError ? Color.RED : Color.GREEN);
        lblMessage.setText(message);
    }
    
    // Clears all input fields and messages
    public void clearFields() {
        txtUserId.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        lblMessage.setText(" ");
    }
    
    // Defines preferred panel size for consistent display
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }
}