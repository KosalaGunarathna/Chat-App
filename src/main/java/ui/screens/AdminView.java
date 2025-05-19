package ui.screens;

import core.models.User;
import core.models.ChatMessage;
import core.network.ChatServer;
import core.services.ChatServiceManager;
import core.services.UserAccountService;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AdminView extends JFrame {

    private JButton createChatButton;
    private JButton viewUsersButton;
    private JButton viewChatsButton;
    private JButton removeUserButton;
    private JButton editUserButton;
    private JButton backButton;


    // Modern UI Theme Colors (matching ChatScreen)
    private static final Color THEME_PRIMARY = new Color(47, 128, 237);   // Vibrant blue (accent)
    private static final Color THEME_SECONDARY = new Color(244, 245, 248);  // Soft light gray
    private static final Color THEME_BACKGROUND = new Color(250, 250, 250);  // Almost white
    private static final Color THEME_HEADER = new Color(47, 128, 237);     // Same as primary for headers
    private static final Color THEME_TEXT = new Color(50, 50, 50);     // Rich dark gray for text


    public AdminView() {
        setTitle("Admin");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window, not the whole app
        setLocationRelativeTo(null);
        setResizable(true);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(THEME_BACKGROUND);

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(THEME_HEADER);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(4, 2, 15, 15));
        contentPanel.setBackground(THEME_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create buttons with modern styling
        createChatButton = createStyledButton("Chat Create");
        viewUsersButton = createStyledButton("View Users");
        viewChatsButton = createStyledButton("View Chats");
        removeUserButton = createStyledButton("Delete User");
        editUserButton = createStyledButton("Edit User");
        backButton = createStyledButton("Back");

        // Add buttons to content panel
        contentPanel.add(createChatButton);
        contentPanel.add(viewUsersButton);
        contentPanel.add(viewChatsButton);
        contentPanel.add(removeUserButton);
        contentPanel.add(editUserButton);
        contentPanel.add(backButton);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Initialize services
        ChatServiceManager chatServiceManager = new ChatServiceManager();
        UserAccountService userAccountService = new UserAccountService();
        User user=new User();





        // Add action listeners
        createChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatMessage chatMessage = chatServiceManager.startChat();
//                ChatScreen chatScreen=new ChatScreen();
//                chatScreen.saveChatToFile(LocalDateTime.now());


                if (chatMessage != null) {
                    JOptionPane.showMessageDialog(AdminView.this, "Chat created successfully!");
                } else {
                    JOptionPane.showMessageDialog(AdminView.this, "Failed to create chat.");
                }


            }
        });

        viewUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder userList = new StringBuilder("<html><div style='font-family:Segoe UI; font-size:12px; padding:10px;'>");
                userList.append("<h2 style='color:#2F80ED;'>Registered Users</h2>");
                userList.append("<table style='width:100%; border-collapse:collapse;'>");
                userList.append("<tr style='background-color:#2F80ED; color:white;'>" +
                        "<th style='padding:8px;'>ID</th><th style='padding:8px;'>Nickname</th>" +
                        "<th style='padding:8px;'>Email</th><th style='padding:8px;'>Role</th></tr>");

                boolean alternate = false;
                for (User user : userAccountService.getAllUsers()) {
                    String rowStyle = alternate ? "background-color:#F4F5F8;" : "background-color:#FFFFFF;";
                    userList.append("<tr style='").append(rowStyle).append("'>");
                    userList.append("<td style='padding:8px; text-align:center;'>").append(user.getId()).append("</td>");
                    userList.append("<td style='padding:8px;'>").append(user.getNickname()).append("</td>");
                    userList.append("<td style='padding:8px;'>").append(user.getEmail()).append("</td>");
                    userList.append("<td style='padding:8px;'>").append(user.getRole() != null ? user.getRole() : "user").append("</td>");
                    userList.append("</tr>");
                    alternate = !alternate;
                }

                userList.append("</table></div></html>");
                JOptionPane.showMessageDialog(AdminView.this, userList.toString(), "Users", JOptionPane.PLAIN_MESSAGE);
            }
    });

        // Add back button action listener
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close only this window
            }
        });

        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Remove User Button Clicked

                String userIdStr = JOptionPane.showInputDialog(AdminView.this, "Enter User ID");
                if (userIdStr == null || userIdStr.trim().isEmpty()) {
                    return;
                }

                try {
                    int userId = Integer.parseInt(userIdStr);
                    int confirm = JOptionPane.showConfirmDialog(AdminView.this,
                            " remove user with ID " + userId + "?",
                            "Confirm Remove User", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = userAccountService.deleteUser(userId);
                        if (success) {
                            JOptionPane.showMessageDialog(AdminView.this, "User removed successfully!");
                        } else {
                            JOptionPane.showMessageDialog(AdminView.this, "Failed to remove user.","Error",JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AdminView.this, "Enter a valid numeric ID");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminView.this, "Failed to remove user " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });


        viewChatsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JDialog dialog = new JDialog((Frame) null, "View Chats", true);
                    dialog.setSize(400, 300);
                    dialog.setLayout(new BorderLayout());

                    // Create components
                    JComboBox<String> chatComboBox = new JComboBox<>();
                    JTextArea chatTextArea = new JTextArea();
                    chatTextArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(chatTextArea);

                    // Populate dropdown
                    try {
                        chatComboBox.removeAllItems();
                        ChatServiceManager chatService = new ChatServiceManager();
                        List<ChatMessage> chatMessages = chatService.getAllChats(); // Assuming this is accessible

                        for (ChatMessage chatMessage : chatMessages) {
                            chatComboBox.addItem("Chat " + chatMessage.getId());
                        }

                        // Action listener for combo box selection
                        chatComboBox.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int selectedIndex = chatComboBox.getSelectedIndex();
                                if (selectedIndex >= 0) {
                                    ChatMessage selectedChat = chatMessages.get(selectedIndex);
                                    String filePath = "src/main/java/core/services/chat_logs/chat_" + selectedChat.getId() + ".txt";
                                    try {
                                        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
                                        chatTextArea.setText(content);
                                    } catch (IOException ex) {
                                        chatTextArea.setText("Error reading chat log: " + ex.getMessage());
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // Layout dialog
                    dialog.add(chatComboBox, BorderLayout.NORTH);
                    dialog.add(scrollPane, BorderLayout.CENTER);

                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                }



        });


        editUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch all users
                List<User> users = userAccountService.getAllUsers();

                if (users.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            AdminView.this,
                            "No users Registerd",
                            "User Management",
                            JOptionPane.PLAIN_MESSAGE
                    );
                    return;
                }

                // Prepare display options for users
                String[] userOptions = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    User user = users.get(i);
                    userOptions[i] = "ID: " + user.getId() + " : " + user.getNickname();
                }

                // Show selection dialog
                String selectedUserString = (String) JOptionPane.showInputDialog(
                        AdminView.this,
                        "Select a user to edit:",
                        "Edit User Profile",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        userOptions,
                        userOptions[0]
                );

                if (selectedUserString == null) {
                    return; // User canceled
                }
                // Parse selected user ID
                try {
                    int userId = Integer.parseInt(
                            selectedUserString.substring(
                                    selectedUserString.indexOf("ID: ") + 4,
                                    selectedUserString.indexOf(" : ")
                            )
                    );

                    // Retrieve user by ID
                    User selectedUser = userAccountService.getUserById(userId);

                    if (selectedUser != null) {
                        dispose(); // Close current view
                        new AdminProfileEditor(selectedUser).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(
                                AdminView.this,
                                "User not found.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            AdminView.this,
                            "Failed to parse selected user.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(THEME_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(THEME_PRIMARY.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(THEME_PRIMARY);
            }
        });

        return button;
    }

    //thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminView().setVisible(true));
    }
}
