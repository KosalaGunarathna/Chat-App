package ui.screens;

import core.models.User;
import core.models.ChatMessage;
import core.services.ChatObserver;

public class ChatScreenObserver implements ChatObserver {
    
    private ChatScreen chatScreen;
    
    public ChatScreenObserver(ChatScreen chatScreen) {
        this.chatScreen = chatScreen;
    }
    
    @Override
    public void onSubscribe(User user, ChatMessage chatMessage) {
        // Notify the chat window that a user has subscribed
        chatScreen.userSubscribed(user);
    }
    
    @Override
    public void onUnsubscribe(User user, ChatMessage chatMessage) {
        // Notify the chat window that a user has unsubscribed
        chatScreen.userUnsubscribed(user);
    }
}