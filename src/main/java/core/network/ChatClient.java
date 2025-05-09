package core.network;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            // Connect to the RMI registry on localhost and port 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Lookup the ChatService from the registry
            ChatNetworkService chatNetworkService = (ChatNetworkService) registry.lookup("ChatService");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your nickname: ");
            String nickname = scanner.nextLine();

            // Notify that the user has joined
            chatNetworkService.notifyUserJoined(nickname);

            while (true) {
                System.out.print("Enter message (type 'Bye' to leave): ");
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("Bye")) {
                    chatNetworkService.notifyUserLeft(nickname);
                    break;
                }

                // Broadcast the message
                chatNetworkService.broadcastMessage(nickname + ": " + message);
            }

            System.out.println("You have left the chat.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
