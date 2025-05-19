package ui.screens;

import core.models.User;
import core.services.UserAccountService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SignupView extends JFrame {

    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nicknameField;
    private JButton registerButton;
    private JButton uploadPictureButton;
    private JButton backButton;
    private JLabel profilePictureLabel;
    private String profilePicturePath;

    // Theme colors
    private static final Color THEME_PRIMARY = new Color(59, 130, 246);       // Dark blue
    private static final Color THEME_SECONDARY = new Color(223, 227, 238);   // Light blue-gray
    private static final Color THEME_BACKGROUND = new Color(240, 242, 245);  // Very light gray
    private static final Color THEME_HEADER = new Color(59, 130, 246);         // Darker blue
    private static final Color THEME_TEXT = new Color(33, 33, 33);           // Dark gray

    public SignupView() {
        setTitle("Register");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_HEADER);
        headerPanel.setPreferredSize(new Dimension(900, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setOpaque(false); // So background matches header
        centerPanel.add(titleLabel);

        headerPanel.add(centerPanel, BorderLayout.CENTER);

        // Fields & Labels
        JLabel emailLabel = createLabel("Email");
        emailField = createTextField();

        JLabel usernameLabel = createLabel("Username");
        usernameField = createTextField();

        JLabel passwordLabel = createLabel("Password");
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(createTextFieldBorder());

        JLabel nicknameLabel = createLabel("Nickname");
        nicknameField = createTextField();

        JLabel profilePicLabel = createLabel("Profile Picture:");
        profilePictureLabel = new JLabel();
        profilePictureLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        uploadPictureButton = new JButton("Upload Picture");
        styleButton(uploadPictureButton);

        registerButton = new JButton("Sign Up");
        styleButton(registerButton);

        backButton = new JButton("Login");
        styleButton(backButton);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(THEME_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_PRIMARY, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.weightx = 1.0;

        int row = 0;

// Email
        gbc.gridy = row++;gbc.gridx = 0;
        gbc.gridwidth = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

// Username
        gbc.gridy = row++;
        gbc.gridx = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

// Password
        gbc.gridy = row++;
        gbc.gridx = 0;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

// Nickname
        gbc.gridy = row++;
        gbc.gridx = 0;
        formPanel.add(nicknameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nicknameField, gbc);

// Profile Pic Label
        gbc.gridy = row++;
        gbc.gridx = 0;
        formPanel.add(profilePicLabel, gbc);

// Profile Pic Upload Area
        JPanel picturePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        picturePanel.setBackground(THEME_BACKGROUND);
        picturePanel.add(profilePictureLabel);
        picturePanel.add(uploadPictureButton);
        gbc.gridx = 1;
        formPanel.add(picturePanel, gbc);

// Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(THEME_BACKGROUND);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        formPanel.add(buttonPanel, gbc);

// Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(THEME_BACKGROUND);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);


        // User service
        UserAccountService userAccountService = new UserAccountService();

        uploadPictureButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Profile Picture");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png",".icon"));

            int result = fileChooser.showOpenDialog(SignupView.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                profilePicturePath = selectedFile.getAbsolutePath();
//                profilePictureLabel.setText(selectedFile.getName());

                try {
                    ImageIcon icon = new ImageIcon(profilePicturePath);
                    Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                    profilePictureLabel.setIcon(new ImageIcon(img));
                } catch (Exception ex) {
                    profilePictureLabel.setIcon(null);
                    profilePictureLabel.setText("Error loading image: " + selectedFile);
                }
            }
        });

        registerButton.addActionListener(e -> {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String nickname = nicknameField.getText();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                JOptionPane.showMessageDialog(SignupView.this,
                        "Please fill in all required fields (Email, Username, Password, Nickname).",
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setProfilePicture(profilePicturePath);
            user.setRole("user");

            userAccountService.registerUser(user);
            JOptionPane.showMessageDialog(SignupView.this, "Registration successful!");
            dispose();
        });

        backButton.addActionListener(e -> dispose());
    }

    // Reusable label
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(THEME_TEXT);
        return label;
    }

    // Reusable text field
    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(createTextFieldBorder());
        return field;
    }

    // Border for text fields
    private Border createTextFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_PRIMARY, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }

    // Reusable button style
    private void styleButton(JButton button) {
        button.setBackground(THEME_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignupView().setVisible(true));
    }
}
