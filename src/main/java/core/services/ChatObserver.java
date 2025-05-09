package core.services;

import core.models.User;
import core.models.ChatMessage;

/**
 * Interface for the Observer pattern to handle chat subscriptions
 */
public interface ChatObserver {
    /**
     * Called when a user is subscribed to a chat
     * @param user The user who was subscribed
     * @param chatMessage The chat the user was subscribed to
     */
    void onSubscribe(User user, ChatMessage chatMessage);
    
    /**
     * Called when a user is unsubscribed from a chat
     * @param user The user who was unsubscribed
     * @param chatMessage The chat the user was unsubscribed from
     */
    void onUnsubscribe(User user, ChatMessage chatMessage);
}