package core.network;

import ui.screens.ChatScreen;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


 //Implementation of the ChatClientCallback interface.
 //This class handles callbacks from the chat server to deliver messages to the client.

public class ChatClientCallbackImpl extends UnicastRemoteObject implements ChatClientCallback {

    private ChatScreen chatScreen;

    public ChatClientCallbackImpl(ChatScreen chatScreen) throws RemoteException {
        this.chatScreen = chatScreen;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {
        chatScreen.displayMessage(message);
    }

    @Override
    public void userJoined(String nickname) throws RemoteException {
    }

    @Override
    public void userLeft(String nickname) throws RemoteException {
    }
}
