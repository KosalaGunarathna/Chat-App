package core.services;

import core.models.User;
import core.models.ChatMessage;


 //Interface for the Observer pattern to handle chat subscriptions

public interface ChatObserver {


    void onSubscribe(User user, ChatMessage chatMessage);


    void onUnsubscribe(User user, ChatMessage chatMessage);
}