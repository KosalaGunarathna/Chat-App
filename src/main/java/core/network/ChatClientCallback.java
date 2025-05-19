package core.network;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatClientCallback extends Remote {

    void receiveMessage(String message) throws RemoteException;

    void userJoined(String nickname) throws RemoteException;

    void userLeft(String nickname) throws RemoteException;
}
