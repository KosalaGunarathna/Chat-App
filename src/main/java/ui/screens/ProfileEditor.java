package ui.screens;

import core.models.User;
import core.services.UserAccountService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class ProfileEditor extends JFrame {

    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected JTextField nicknameField;
    protected JButton updateButton;
    protected JButton uploadPictureButton;
    protected JButton backButton;
    protected JLabel profilePictureLabel;
    protected String profilePicturePath;
    protected User user;

    // Professional/Enterprise theme colors
    private static final Color THEME_PRIMARY = new Color(59, 130, 246);      // Modern blue
    private static final Color THEME_SECONDARY = new Color(223, 227, 238);  // Light blue-gray
    private static final Color THEME_BACKGROUND = new Color(245, 247, 250);  // Soft white-gray
    private static final Color THEME_HEADER = new Color(35, 53, 91);  // Darker blue
    private static final Color THEME_TEXT = new Color(44, 62, 80);

    public ProfileEditor(User user) {
        this.user = user;
        this.profilePicturePath = user.getProfilePicture();

        setTitle("Profile Update");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(THEME_PRIMARY);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        headerPanel.setPreferredSize(new Dimension(400, 60));

        JLabel titleLabel = new JLabel("Profile " + user.getNickname());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create components with professional styling
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        usernameLabel.setForeground(THEME_TEXT);

        usernameField = new JTextField(user.getUsername(), 20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 18));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_PRIMARY, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordLabel.setHorizontalAlignment(SwingConstants.LEFT);
        passwordLabel.setForeground(THEME_TEXT);

        passwordField = new JPasswordField(user.getPassword(), 20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_PRIMARY, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel nicknameLabel = new JLabel("Nickname");
        nicknameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nicknameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nicknameLabel.setForeground(THEME_TEXT);

        nicknameField = new JTextField(user.getNickname(), 20);
        nicknameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_PRIMARY, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));

        JLabel profilePicLabel = new JLabel("Profile Picture");
        profilePicLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        profilePicLabel.setHorizontalAlignment(SwingConstants.LEFT);
        profilePicLabel.setForeground(THEME_TEXT);

        profilePictureLabel = new JLabel(); // Start with an empty label
        profilePictureLabel.setFont(new Font("Arial", Font.PLAIN, 14));

// Display current profile picture if available
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(user.getProfilePicture());
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                profilePictureLabel.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                profilePictureLabel.setIcon(null);
                profilePictureLabel.setText("Error loading image");
            }
        } else {
            profilePictureLabel.setText("No image"); // Placeholder if no image
        }

        uploadPictureButton = new JButton("Change Picture");
        styleButton(uploadPictureButton);

        updateButton = new JButton("Update");
        styleButton(updateButton);

        backButton = new JButton("Back");
        styleButton(backButton);

        // Create profile picture panel
        JPanel profilePanel = new JPanel();
        profilePanel.setBackground(THEME_BACKGROUND);
        profilePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Add user profile picture
        JLabel userPicLabel = new JLabel();
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(user.getProfilePicture());
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                userPicLabel.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                userPicLabel.setText("👤");
                userPicLabel.setFont(new Font("Arial", Font.PLAIN, 48));
                userPicLabel.setForeground(THEME_PRIMARY);
            }
        } else {
            userPicLabel.setText("👤");
            userPicLabel.setFont(new Font("Arial", Font.PLAIN, 48));
            userPicLabel.setForeground(THEME_PRIMARY);
        }
        profilePanel.add(userPicLabel);

        // Create form panel

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(THEME_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.2;

// Row 0 - Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

// Row 1 - Password
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

// Row 2 - Nickname
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        formPanel.add(nicknameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(nicknameField, gbc);

// Row 3 - Profile Picture Label
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        formPanel.add(profilePicLabel, gbc);

// Row 4 - Picture Upload Area
        JPanel picturePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        picturePanel.setBackground(THEME_BACKGROUND);
        picturePanel.add(profilePictureLabel);
        picturePanel.add(uploadPictureButton);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(picturePanel, gbc);

// Row 5 - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(THEME_BACKGROUND);

        updateButton.setPreferredSize(new Dimension(200, 40));
        backButton.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

// Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(THEME_BACKGROUND);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);



        // Button actions
        UserAccountService userAccountService = new UserAccountService();

        // Add action listener for upload button
        uploadPictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select Profile Picture");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(ProfileEditor.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    profilePicturePath = selectedFile.getAbsolutePath();
                    profilePictureLabel.setText(selectedFile.getName());

                    // Display a preview of the image
                    try {
                        ImageIcon icon = new ImageIcon(profilePicturePath);
                        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                        profilePictureLabel.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        profilePictureLabel.setIcon(null);
                        profilePictureLabel.setText("Error loading image: " + selectedFile.getName());
                    }
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String nickname = nicknameField.getText();

                // Validate input
                if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(ProfileEditor.this,
                            "Please fill in all required fields (Username, Password, Nickname).",
                            "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update user details
                user.setUsername(username);
                user.setPassword(password);
                user.setNickname(nickname);
                user.setProfilePicture(profilePicturePath);

                userAccountService.updateUser(user);

                JOptionPane.showMessageDialog(ProfileEditor.this, "Profile updated successfully!");
                dispose(); // Close profile update screen
                new ChatScreen(user).setVisible(true); // Return to chat window
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close profile update screen
                new ChatScreen(user).setVisible(true); // Return to chat window
            }
        });
    }

    // Helper method to style buttons with professional theme
    private void styleButton(JButton button) {
        button.setBackground(THEME_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }
}
