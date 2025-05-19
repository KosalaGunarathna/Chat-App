package ui.screens;

import core.models.User;
import core.models.ChatMessage;
import core.models.UserSubscription;
import core.network.ChatClientCallback;
import core.network.ChatClientCallbackImpl;
import core.network.ChatNetworkService;
import core.services.ChatServiceManager;
import core.services.UserSubscriptionService;
import core.services.UserAccountService;
import org.hibernate.Session;
import org.hibernate.query.Query;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatScreen extends JFrame {

    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton leaveChatButton;
    private JButton subscribeButton;
    private JButton unsubscribeButton;
    private JButton updateProfileButton;
    private JButton backButton;
    private JButton adminPanelButton; //only visible for admin users
    private JComboBox<String> chatComboBox;
    private List<String> connectedUsers;

    private User user;
    private boolean isChatActive = true;
    private ChatNetworkService chatNetworkService;
    private UserSubscriptionService userSubscriptionService;
    private ChatScreenObserver observer;
    private ChatClientCallback callback;
    private String callbackId;
    private UserAccountService userAccountService;
    private ChatMessage currentChatMessage;

    // Professional/Enterprise theme colors
// 🎨 Modern UI Theme Colors (Flat Design Inspired)
    private static final Color THEME_PRIMARY = new Color(47, 128, 237);
    private static final Color THEME_SECONDARY= new Color(244, 245, 248);
    private static final Color THEME_BACKGROUND= new Color(250, 250, 250);
    private static final Color THEME_HEADER = new Color(47, 128, 237);
    private static final Color THEME_TEXT = new Color(50, 50, 50);


    public ChatScreen(User user) {

        this.user = user;
        this.connectedUsers = new ArrayList<>();
        this.userSubscriptionService = new UserSubscriptionService();
        this.userAccountService = new UserAccountService();

        setTitle("Chat Screen");
        setSize(900, 600); // More phone-like dimensions
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(THEME_BACKGROUND);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));

        messageArea.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    int pos = messageArea.viewToModel(e.getPoint());
                    int line  = messageArea.getLineOfOffset(pos);
                    int start = messageArea.getLineStartOffset(line);
                    int end   = messageArea.getLineEndOffset(line);
                    String text = messageArea.getText(start, end - start);

                    if (text.contains(" ")) {
                        String nickname;
                        if (text.contains("You")) {
                            nickname = user.getNickname();
                        } else {
                            nickname = text
                                    .substring(text.indexOf(" ") + 2, text.lastIndexOf(" - "))
                                    .trim();
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("Error handling mouse click: " + ex.getMessage());
                }
            }

        });


// ✨ Modern JTextField
        messageField = new JTextField(20);
        messageField.setBackground(Color.WHITE);
        messageField.setForeground(THEME_TEXT);
        messageField.setCaretColor(THEME_PRIMARY);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));  // Modern font
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_SECONDARY, 1),        // Softer border
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));           // Inner padding

// 🔘 Modern JButton - Send
        sendButton = new JButton("Send");
        sendButton.setBackground(THEME_PRIMARY);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));  // Flat pill-style
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Pointer effect

// 🔘 Modern JButton - Leave
        leaveChatButton = new JButton("Leave");
        leaveChatButton.setBackground(THEME_PRIMARY);
        leaveChatButton.setForeground(Color.WHITE);
        leaveChatButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        leaveChatButton.setFocusPainted(false);
        leaveChatButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        leaveChatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

// 🔘 Modern JButton - Profile
        updateProfileButton = new JButton("Profile");
        updateProfileButton.setBackground(THEME_PRIMARY);
        updateProfileButton.setForeground(Color.WHITE);
        updateProfileButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updateProfileButton.setFocusPainted(false);
        updateProfileButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        updateProfileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Modern JButton - Back
        backButton = new JButton("Login");
        backButton.setBackground(THEME_PRIMARY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Modern JButton - Admin Panel (only for admin users)
        adminPanelButton = new JButton("Admin Panel");
        adminPanelButton.setBackground(THEME_PRIMARY);
        adminPanelButton.setForeground(Color.WHITE);
        adminPanelButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        adminPanelButton.setFocusPainted(false);
        adminPanelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        adminPanelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Only show the admin panel button for admin users
        adminPanelButton.setVisible(user.getRole() != null && user.getRole().equalsIgnoreCase("admin"));


        // Create and register the observer
        this.observer = new ChatScreenObserver(this);
        userSubscriptionService.addObserver(this.observer);

        boolean isConnected = false;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            chatNetworkService = (ChatNetworkService) registry.lookup("ChatService");
            callback = new ChatClientCallbackImpl(this);
            callbackId = chatNetworkService.registerCallbackWithId(user.getNickname(), callback);
            chatNetworkService.notifyUserJoined(user.getNickname());
            connectedUsers.add(user.getNickname());

            System.out.println("Successfully connected to chat server");
            isConnected = true;
        } catch (Exception e) {
            // Inform the user about the connection failure but allow them to continue in local mode
            String errorMsg = "Failed to connect to chat server: " + e.getMessage() +
                    "\n\n messages will not be sent to other users.";
            JOptionPane.showMessageDialog(this, errorMsg, "Connection Error", JOptionPane.WARNING_MESSAGE);

            connectedUsers.add(user.getNickname());

            // Log the error for debugging
            System.err.println("Failed to connect to chat server: " + e.getMessage());
            e.printStackTrace();
        }

        // Add window closing handler to leave chat properly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                leaveChat();
            }
        });

        subscribeButton = new JButton("Subscribe");
        subscribeButton.setBackground(THEME_PRIMARY);
        subscribeButton.setForeground(Color.WHITE);
        subscribeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subscribeButton.setFocusPainted(false);
        subscribeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        subscribeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        unsubscribeButton = new JButton("Unsubscribe");
        unsubscribeButton.setBackground(THEME_PRIMARY);
        unsubscribeButton.setForeground(Color.WHITE);
        unsubscribeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        unsubscribeButton.setFocusPainted(false);
        unsubscribeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        unsubscribeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

//      JComboBox - Chat Selector
        chatComboBox = new JComboBox<>();
        chatComboBox.setBackground(Color.WHITE);
        chatComboBox.setForeground(THEME_TEXT);
        chatComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(THEME_SECONDARY, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // Populate chat dropdown with available chats
        populateChatsDropdown();

//         Add action listener to chat combo box
        chatComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCurrentChatFromComboBox();
            }
        });


        // Initialize current chat if user is already subscribed to any chat
        if (chatComboBox.getItemCount() > 0) {
            chatComboBox.setSelectedIndex(0);
            updateCurrentChatFromComboBox();
        }


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(THEME_BACKGROUND);

        // back button pannel
        JPanel backButtonPannel = new JPanel();
        backButtonPannel.setLayout(new BorderLayout());
        backButtonPannel.setBackground(THEME_BACKGROUND);
        backButtonPannel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        backButtonPannel.setPreferredSize(new Dimension(300, 60));

// Create style header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(THEME_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setPreferredSize(new Dimension(400, 60));

// Create headerContent panel to hold Back button and profile panel
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setPreferredSize(new Dimension(500,80));
        headerContent.setBackground(THEME_BACKGROUND);

// Add Back button to the left (WEST) of headerContent
        headerContent.add(unsubscribeButton, BorderLayout.WEST);

// Create user profile panel for the header
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout());
        profilePanel.setBackground(THEME_BACKGROUND);
        profilePanel.setPreferredSize(new Dimension(600, 60));

// Add user nickname with professional styling
        JLabel nicknameLabel = new JLabel("Hi, " + user.getNickname());
        nicknameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nicknameLabel.setForeground(THEME_HEADER);

        // add profile panel to image
        ImageIcon originalIcon = new ImageIcon(user.getProfilePicture()); // Replace with your image path
        Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

// Create label to hold profile picture
        JLabel profilePicLabel = new JLabel(scaledIcon);
        profilePicLabel.setPreferredSize(new Dimension(60, 60));

// Optional: Add padding around the image
        profilePicLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

// Add components to profile panel
        profilePanel.add(nicknameLabel, BorderLayout.CENTER);
        profilePanel.add(chatComboBox,BorderLayout.EAST);
        profilePanel.add(profilePicLabel, BorderLayout.WEST);

// Add profile panel to the headerContent (shifted to the right side)
        headerContent.add(profilePanel, BorderLayout.CENTER);

// Now add headerContent to the headerPanel
        headerPanel.add(headerContent, BorderLayout.CENTER);

// Add header panel to the main panel
        panel.add(headerPanel, BorderLayout.NORTH);

// Add message area (scrollable)
        panel.add(new JScrollPane(messageArea));
//        panel.add(backButtonPannel,BorderLayout.WEST);
//        panel.add(messageArea, BorderLayout.CENTER);

        // Create professional-style message input area
        JPanel messageInputPanel = new JPanel();
        messageInputPanel.setLayout(new BorderLayout());
        messageInputPanel.setBackground(THEME_BACKGROUND);
        messageInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Create a rounded panel for the message field and send button
        JPanel inputFieldPanel = new JPanel();
        inputFieldPanel.setLayout(new BorderLayout());
        inputFieldPanel.setBackground(Color.WHITE);
        inputFieldPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(THEME_PRIMARY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Add message field to the input field panel
        inputFieldPanel.add(messageField, BorderLayout.CENTER);

        // Create a panel for the send button
        JPanel sendButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        sendButtonPanel.setBackground(Color.WHITE);
        sendButtonPanel.add(sendButton);

        // Add send button panel to the input field panel
        inputFieldPanel.add(sendButtonPanel, BorderLayout.EAST);

        // Add input field panel to the message input panel
        messageInputPanel.add(inputFieldPanel, BorderLayout.CENTER);

        // Create a panel for the action buttons
        JPanel actionButtonsPanel = new JPanel();
        actionButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        actionButtonsPanel.setBackground(THEME_BACKGROUND);


        actionButtonsPanel.add(backButton);
        actionButtonsPanel.add(leaveChatButton);
        actionButtonsPanel.add(updateProfileButton);
        actionButtonsPanel.add(subscribeButton);
        actionButtonsPanel.add(unsubscribeButton);
        // Add admin panel button (only visible for admin users)
        actionButtonsPanel.add(adminPanelButton);

        // Add action buttons panel to the message input panel
        messageInputPanel.add(actionButtonsPanel, BorderLayout.SOUTH);

        // Create a panel for the controls (subscription only)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BorderLayout());
        controlsPanel.setBackground(THEME_BACKGROUND);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBackground(THEME_BACKGROUND);

        // Add controls panel and message input panel to the bottom panel
        bottomPanel.add(controlsPanel, BorderLayout.NORTH);
        bottomPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Add bottom panel to the main panel
        panel.add(bottomPanel, BorderLayout.SOUTH);

        add(panel);


        if (chatNetworkService != null) {
            displayMessage("Connected to chat server successfully.");
        } else {
            displayMessage("WARNING: Not connected to chat server. Messages will only be displayed locally.");
        }

        // Button actions
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        leaveChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leaveChat();
            }
        });

        // Add action listeners for subscribe and unsubscribe buttons
        subscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subscribeToChat();
            }
        });

        unsubscribeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                unsubscribeFromChat();
            }
        });

        // Handle "Enter" key press for sending messages
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Add action listener for update profile button
        updateProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close chat window
                new ProfileEditor(user).setVisible(true); // Open profile update screen
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close chat window
                new LoginView().setVisible(true); // Open login screen
            }
        });

        // Add action listener for admin panel button
        adminPanelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open admin view without closing the chat window
                new AdminView().setVisible(true);
            }
        });
    }

    private void loadChatFromFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                messageArea.setText("No chat history available.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");

            }
            reader.close();
            messageArea.setText(content.toString());
        } catch (IOException e) {
            messageArea.setText("Error reading chat file.");
            e.printStackTrace();
        }
    }

    public void displayMessage(String message) {
        String sender;
        String content;
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.getHour() + ":" + String.format("%2d", now.getMinute());

        if (isSystemMessage(message)) {
            messageArea.append("\n" + message + "\n");
        } else if (message.contains(":")) {
            try {
                sender = message.substring(0, message.indexOf(":")).trim();
                content = message.substring(message.indexOf(":") + 1).trim();
            } catch (Exception e) {
                sender = user.getNickname();
                content = message;
            }

            if (sender.equals(user.getNickname())) {
                messageArea.append("\n" + getSpaces(5) + "You - " + timestamp + "\n");
                for (String line : splitMessage(content, 100)) {
                    messageArea.append(getSpaces(5 - line.length()) + line + "\n");
                }
            } else {
                messageArea.append("\n" + sender + " - " + timestamp + "\n");
                for (String line : splitMessage(content, 100)) {
                    messageArea.append(line + "\n");
                }
            }
        } else {
            content = message;
            messageArea.append("\n" + getSpaces(50) + " You - " + timestamp + "\n");
            for (String line : splitMessage(content, 100)) {
                messageArea.append(getSpaces(50 - line.length()) + line + "\n");
            }
        }

        if (message.contains("has joined the chat")) {
            String nickname = message.substring(0, message.indexOf(" has joined"));
            if (!nickname.equals(user.getNickname()) && !connectedUsers.contains(nickname)) {
                connectedUsers.add(nickname);
            }
        }

        if (message.contains("has left the chat")) {
            String nickname = message.substring(0, message.indexOf(" has left"));
            connectedUsers.remove(nickname);
        }

        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    private boolean isSystemMessage(String msg) {
        return msg.contains("Connected to chat server") ||
                msg.contains("WARNING:");
    }


    // Helper method to create spaces for alignment
    private String getSpaces(int count) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < count; i++) {
            spaces.append(" ");
        }
        return spaces.toString();
    }

    // Helper method to split long messages into multiple lines
    private String[] splitMessage(String message, int maxLength) {
        if (message.length() <= maxLength) {
            return new String[] { message };
        }

        int lines = (message.length() + maxLength - 1) / maxLength;
        String[] result = new String[lines];

        for (int i = 0; i < lines; i++) {
            int start = i * maxLength;
            int end = Math.min(start + maxLength, message.length());
            result[i] = message.substring(start, end);
        }

        return result;
    }

    // Helper method to format LocalDateTime to a user-friendly format
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        return dateTime.format(formatter);
    }

    // Method to send a message
    private void sendMessage() {
//        if (!isChatActive) {
//            JOptionPane.showMessageDialog(this, "You have already left the chat.");
//            return;
//        }


        String message = messageField.getText().trim();
        if (message.isEmpty()) {
            return;
        }


        if (message.equalsIgnoreCase("Bye")) {
            leaveChat(); // Leave the chat if the user types "Bye"
            return;
        }

        // Check if a chat is selected
        if (currentChatMessage == null) {
            JOptionPane.showMessageDialog(this,
                    "Please subscribe to a chat ",
                    "No Chat subscribe", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if the user is an admin or subscribed to the current chat
        boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase("admin");
        if (!isAdmin && !userSubscriptionService.isUserSubscribedToChat(user, currentChatMessage)) {
            JOptionPane.showMessageDialog(this,
                    "You are not subscribed chat. Please subscribe.",
                    "Not Subscribed", JOptionPane.WARNING_MESSAGE);
            return;
        }


        try {
            // Check if chatService is available
            if (chatNetworkService == null) {
                // If chatService is not available, just display the message locally
                displayMessage(user.getNickname() + ": " + message);

                // Show a warning that the message was not sent to the server
                JOptionPane.showMessageDialog(this,
                        "Message displayed locally only. Not connected to chat server.",
                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                chatNetworkService.broadcastMessage(user.getNickname() + ": " + message);
            }

            messageField.setText(""); // Clear the input field
        } catch (Exception e) {
            // Display the message locally even if sending to server fails
            displayMessage(user.getNickname() + ": " + message);

            JOptionPane.showMessageDialog(this,
                    "Failed to send message to server: " + e.getMessage() + "\nMessage displayed locally only.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Method to leave the chat
    private void leaveChat() {
        if (!isChatActive) {
            JOptionPane.showMessageDialog(this, "You have already left the chat.");
            return;
        }

        try {
            // Notify the server that the user has left (if connected)
            if (chatNetworkService != null) {
                try {
                    // Unregister the callback before notifying that the user has left
                    if (callback != null && callbackId != null) {
                        try {
                            chatNetworkService.unregisterCallbackById(callbackId);
                        } catch (Exception ex) {
                            System.err.println("Error unregistering callback: " + ex.getMessage());
                        }
                    }

                    chatNetworkService.notifyUserLeft(user.getNickname());
                } catch (Exception e) {
                    System.err.println("Error notifying server about user leaving: " + e.getMessage());
                    // Continue with local processing even if server notification fails
                }
            } else {
                System.out.println("Chat server not connected. User leaving handled locally only.");
            }

            // Display a message indicating that the user has left
            LocalDateTime leaveTime = LocalDateTime.now();
            String leaveMessage = user.getNickname() + " left: " + formatDateTime(leaveTime);
            displayMessage(leaveMessage);

            // Check if this is the last user in the chat
            if (connectedUsers.size() <= 1) {
                // This is the last user, so end the chat
                LocalDateTime endTime = LocalDateTime.now();
                String endMessage = "Chat stopped: " + formatDateTime(endTime);
                displayMessage(endMessage);

                // Save the chat to a .txt file
                saveChatToFile(endTime);
            }
        } catch (Exception e) {
            System.err.println("Error during chat leaving process: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Unregister the observer
            if (userSubscriptionService != null && observer != null) {
                try {
                    userSubscriptionService.removeObserver(observer);
                } catch (Exception e) {
                    System.err.println("Error removing observer: " + e.getMessage());
                }
            }

            isChatActive = false; // Mark chat as inactive
            JOptionPane.showMessageDialog(this, "You have left the chat.");
            // Close this window after leaving
            dispose();
        }
    }

    // save the chat to a .txt file
    public  void saveChatToFile(LocalDateTime endTime) {
        try {
            // Get the chat content
            String chatContent = messageArea.getText();
            // Create a new chat in the database
            ChatServiceManager chatServiceManager = new ChatServiceManager();
            ChatMessage chatMessage = chatServiceManager.startChat();
            // Set the end time and save the chat log
            chatMessage.setEndTime(endTime);
            chatServiceManager.stopChat(chatMessage, chatContent);
            System.out.println("Chat saved to file: " + chatMessage.getFilePath());
        } catch (Exception e) {
            System.err.println("Error saving chat to file: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Method called when a user is subscribed to the chat
    public void userSubscribed(User user) {
        if (!user.getNickname().equals(this.user.getNickname())) {
            // Add the user to the connected users list if not already present
            if (!connectedUsers.contains(user.getNickname())) {
                connectedUsers.add(user.getNickname());
            }

            // Display a message indicating that the user has subscribed
            displayMessage(user.getNickname() + " has subscribed to this chat.");
        }
    }

    // Method called when a user is unsubscribed from the chat
    public void userUnsubscribed(User user) {
        // Remove the user from the connected users list
        connectedUsers.remove(user.getNickname());

        // Display a message indicating that the user has unsubscribed
        displayMessage(user.getNickname() + " has unsubscribed from this chat.");
    }

    // Method to populate the chat dropdown with available chats
    private void populateChatsDropdown() {
        try {
            // Clear existing items
            chatComboBox.removeAllItems();

            // Get all chats from the database
            ChatServiceManager chatDbService = new ChatServiceManager();
            List<ChatMessage> chatMessages = getAllChats();

            // Add chats to the dropdown
            for (ChatMessage chatMessage : chatMessages) {
                chatComboBox.addItem("Chat " + chatMessage.getId());
            }
        } catch (Exception e) {
            System.err.println("Error populating chats dropdown: " + e.getMessage());
            e.printStackTrace();
        }
    }





    // Method to get all chats from the database
    private List<ChatMessage> getAllChats() {
        try (Session session = utils.HibernateUtil.getSessionFactory().openSession()) {
            Query<ChatMessage> query = session.createQuery("FROM ChatMessage", ChatMessage.class);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error getting chats: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    // Method to subscribe to a chat
    private void subscribeToChat() {
        if (chatComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a chat to subscribe to.");
            return;
        }

        try {
            // Get the selected chat ID
            String chatItem = (String) chatComboBox.getSelectedItem();
            int chatId = Integer.parseInt(chatItem.replace("Chat ", ""));

            // Get the chat from the database
            ChatMessage chatMessage = getChatById(chatId);

            if (chatMessage != null) {
                boolean alreadySubscribed = userSubscriptionService.isUserSubscribedToChat(user, chatMessage);

                // Check if this is already the current chat
                boolean isCurrentChat = (currentChatMessage != null && currentChatMessage.getId() == chatMessage.getId());

                if (!alreadySubscribed) {
                    // Subscribe the user to the chat
                    userSubscriptionService.subscribeUserToChat(user, chatMessage);
                    JOptionPane.showMessageDialog(this, "You subscribed to Chat " + chatId);
                    // Display a message in the chat window
//                    displayMessage("You are now viewing Chat " + chatId + ". Only subscribers can send messages to this chat.");
                } else {
                    // User is already subscribed, just switch to this chat
                    JOptionPane.showMessageDialog(this, "You are already subscribed to Chat " + chatId);

                    // Only display the message if this is not already the current chat
                    if (!isCurrentChat) {
                        displayMessage("You are now viewing Chat " + chatId + ".");
                    }
                }

                this.currentChatMessage = chatMessage;
            } else {
                JOptionPane.showMessageDialog(this, "Chat not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error subscribing to chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to unsubscribe from a chat
    private void unsubscribeFromChat() {
        if (chatComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a chat to unsubscribe from.");
            return;
        }

        try {
            // Get the selected chat ID
            String chatItem = (String) chatComboBox.getSelectedItem();
            int chatId = Integer.parseInt(chatItem.replace("Chat ", ""));

            // Get the chat from the database
            ChatMessage chatMessage = getChatById(chatId);

            if (chatMessage != null) {
                // Unsubscribe the user from the chat
                userSubscriptionService.unsubscribeUserFromChat(user, chatMessage);
                JOptionPane.showMessageDialog(this, "You have unsubscribed from Chat " + chatId);
            } else {
                JOptionPane.showMessageDialog(this, "Chat not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error unsubscribing from chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a chat by ID
    private ChatMessage getChatById(int chatId) {
        try (Session session = utils.HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ChatMessage.class, chatId);
        } catch (Exception e) {
            System.err.println("Error getting chat by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Method to update the current chat from the combo box selection
    private void updateCurrentChatFromComboBox() {
        if (chatComboBox.getSelectedItem() == null) {
            return;
        }
        try {
            // Get the selected chat ID
            String chatItem = (String) chatComboBox.getSelectedItem();
            int chatId = Integer.parseInt(chatItem.replace("Chat ", ""));
            // Get the chat from the database
            ChatMessage chatMessage = getChatById(chatId);

            if (chatMessage != null) {
                String filePath = "src/main/java/core/services/chat_logs/chat_" + chatId + ".txt";
                loadChatFromFile(filePath);

                // Check if the user is an admin
                boolean isAdmin = user.getRole() != null && user.getRole().equalsIgnoreCase("admin");

                boolean alreadySubscribed = isAdmin || userSubscriptionService.isUserSubscribedToChat(user, chatMessage);

                boolean isCurrentChat = (currentChatMessage != null && currentChatMessage.getId() == chatMessage.getId());

                if (alreadySubscribed) {
                    // User is already subscribed or is an admin, set this as the current chat
                    this.currentChatMessage = chatMessage;

                    if (!isCurrentChat) {
                        if (isAdmin && !userSubscriptionService.isUserSubscribedToChat(user, chatMessage)) {
                            displayMessage("Admin access: You are now viewing Chat " + chatId + "(not subscribed).");
                        } else {
                            displayMessage("You are now viewing Chat " + chatId + ".");
                        }
                    }
                } else {
                    if (!isCurrentChat) {
                        displayMessage("You need to subscribe to Chat " + chatId + " before send messages.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating current chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}