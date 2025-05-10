package ui.screens;

import core.models.User;
import core.services.UserAccountService;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Screen for admins to update user profile details
 * This is a specialized version of ProfileUpdateScreen that returns to AdminPanel
 */
public class AdminProfileEditor extends ui.screens.ProfileEditor {

    public AdminProfileEditor(User user) {
        super(user);

        setTitle("Edit User Profile");

        for (ActionListener al : backButton.getActionListeners()) {
            backButton.removeActionListener(al);
        }

        backButton.setText("Back");

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close profile update screen
                new AdminView().setVisible(true); // Return to admin panel
            }
        });

        // Override the update button action to return to AdminPanel after successful update
        for (ActionListener al : updateButton.getActionListeners()) {
            updateButton.removeActionListener(al);
        }

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String nickname = nicknameField.getText();

                // Validate input
                if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(AdminProfileEditor.this,
                            "Please fill in all required fields (Username, Password, Nickname).",
                            "Update Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update user details
                user.setUsername(username);
                user.setPassword(password);
                user.setNickname(nickname);
                user.setProfilePicture(profilePicturePath);

                UserAccountService userAccountService = new UserAccountService();
                userAccountService.updateUser(user);

                JOptionPane.showMessageDialog(AdminProfileEditor.this, "Profile updated successfully!");
                dispose(); // Close profile update screen
                new AdminView().setVisible(true); // Return to admin panel
            }
        });
    }
}
