package core.ui.screens;

import core.models.User;
import core.network.ChatNetworkServerHandler;
import core.network.ChatNetworkService;
import core.services.UserAccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private static boolean isServerStarted = false;

    private static final Color THEME_PRIMARY = new Color(59, 130, 246);
    private static final Color THEME_BACKGROUND = new Color(245, 247, 250);
    private static final Color THEME_TEXT = new Color(44, 62, 80);

    private void startChatServer() {
        if (!isServerStarted) {
            try {
                ChatNetworkService chatNetworkService = new ChatNetworkServerHandler();
                Registry registry = LocateRegistry.createRegistry(1099);
                registry.rebind("ChatService", chatNetworkService);
                System.out.println("Chat server is running...");
                isServerStarted = true;
            } catch (Exception e) {
                System.err.println("Failed to start chat server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public LoginView() {
        startChatServer();

        setTitle("Sign In");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(THEME_BACKGROUND);

        // Left side panel (image/branding)
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(450, 600));
        leftPanel.setBackground(THEME_PRIMARY);

        JLabel leftLabel = new JLabel("YoChat");
        leftLabel.setForeground(Color.WHITE);
        leftLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(leftLabel);

        // Right side panel (form)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(THEME_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        rightPanel.add(titleLabel, gbc);

        // Add Email label
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        emailLabel.setForeground(THEME_TEXT);
        gbc.gridy = 1;
        rightPanel.add(emailLabel, gbc);

        // Add the email text field
        emailField = createTextField("Email");
        gbc.gridy = 2;
        rightPanel.add(emailField, gbc);

        // Add Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordLabel.setForeground(THEME_TEXT);
        gbc.gridy = 3;
        rightPanel.add(passwordLabel, gbc);

        // Add the password field
        passwordField = createPasswordField("Password");
        gbc.gridy = 4;
        rightPanel.add(passwordField, gbc);

        loginButton = createButton("Sign In");
        gbc.gridy = 5;
        rightPanel.add(loginButton, gbc);

        registerButton = createTextButton("Create an Account");
        gbc.gridy = 6;
        rightPanel.add(registerButton, gbc);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);

        UserAccountService userAccountService = new UserAccountService();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                User user = userAccountService.loginUser(email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(LoginView.this, "Login successful!");
                    emailField.setText("");
                    passwordField.setText("");

                    // All users go to ChatScreen, including admins
                    new ChatScreen(user).setVisible(true);
                    setVisible(false); // Hide the LoginView window
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "Invalid email or password.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignupView().setVisible(true);
            }
        });
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(THEME_TEXT);
        field.setCaretColor(THEME_PRIMARY);
        field.setToolTipText(placeholder);
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(THEME_TEXT);
        field.setCaretColor(THEME_PRIMARY);
        field.setToolTipText(placeholder);
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(THEME_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        return button;
    }

    private JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(THEME_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setContentAreaFilled(false);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
