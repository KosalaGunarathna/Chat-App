package core.services;

import core.models.ChatMessage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceManager {

    // Get a chat by ID
    public ChatMessage getChatById(int chatId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            ChatMessage chatMessage = session.get(ChatMessage.class, chatId);
            return chatMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Start a new chat
    public ChatMessage startChat() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setStartTime(LocalDateTime.now());
            session.save(chatMessage);

            transaction.commit();
            System.out.println("Chat started successfully at " + chatMessage.getStartTime());
            return chatMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void stopChat(ChatMessage chatMessage, String logContent) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Set end time
            chatMessage.setEndTime(LocalDateTime.now());

            // Save log to file
            String filePath = "src/main/java/core/services/chat_logs\\chat_" + chatMessage.getId() + ".txt";
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(logContent);
                System.out.println("Chat log saved to " + filePath);
                chatMessage.setFilePath(filePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save chat log.");
                return;
            }

            // Update chat in database
            session.update(chatMessage);

            transaction.commit();
            System.out.println("Chat stopped successfully at " + chatMessage.getEndTime());
        }
    }


    public List<ChatMessage> getAllChats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ChatMessage> query = session.createQuery("FROM ChatMessage", ChatMessage.class);
            return query.list();
        } catch (Exception e) {
            System.err.println("Error getting chats: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
