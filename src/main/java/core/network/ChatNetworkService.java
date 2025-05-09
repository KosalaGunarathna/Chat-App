package core.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatNetworkService extends Remote {
    void broadcastMessage(String message) throws RemoteException;

    void notifyUserJoined(String nickname) throws RemoteException;

    void notifyUserLeft(String nickname) throws RemoteException;

    void registerCallback(String nickname, ChatClientCallback callback) throws RemoteException;

    String registerCallbackWithId(String nickname, ChatClientCallback callback) throws RemoteException;

    void unregisterCallback(String nickname) throws RemoteException;

    void unregisterCallbackById(String callbackId) throws RemoteException;

    void notifyNewChat(int chatId) throws RemoteException;
}
