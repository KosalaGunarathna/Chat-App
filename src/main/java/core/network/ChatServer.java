package core.network;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ChatServer {
    public static void main(String[] args) {
        try {

            ChatNetworkService chatNetworkService = new ChatNetworkServerHandler();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("ChatService", chatNetworkService);

            System.out.println("Chat server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
