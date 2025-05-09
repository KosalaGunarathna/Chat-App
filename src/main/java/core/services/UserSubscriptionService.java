package core.services;

import core.models.User;
import core.models.UserSubscription;
import core.models.ChatMessage;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class UserSubscriptionService {

    private List<ChatObserver> observers = new ArrayList<>();

    // Add an observer
    public void addObserver(ChatObserver observer) {
        observers.add(observer);
    }

    // Remove an observer
    public void removeObserver(ChatObserver observer) {
        observers.remove(observer);
    }

    // Notify observers when a user is subscribed to a chat
    private void notifySubscribe(User user, ChatMessage chatMessage) {
        for (ChatObserver observer : observers) {
            observer.onSubscribe(user, chatMessage);
        }
    }

    // Notify observers when a user is unsubscribed from a chat
    private void notifyUnsubscribe(User user, ChatMessage chatMessage) {
        for (ChatObserver observer : observers) {
            observer.onUnsubscribe(user, chatMessage);
        }
    }

    // Subscribe a user to a chat
    public void subscribeUserToChat(User user, ChatMessage chatMessage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            UserSubscription userSubscription = new UserSubscription();
            userSubscription.setUser(user);
            userSubscription.setChat(chatMessage);

            session.save(userSubscription);
            transaction.commit();

            System.out.println("User " + user.getNickname() + " subscribed to chat " + chatMessage.getId());

            // Notify observers
            notifySubscribe(user, chatMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Unsubscribe a user from a chat
    public void unsubscribeUserFromChat(User user, ChatMessage chatMessage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String hql = "FROM UserSubscription WHERE user.id = :userId AND chatMessage.id = :chatId";
            List<UserSubscription> userSubscriptions = session.createQuery(hql, UserSubscription.class)
                    .setParameter("userId", user.getId())
                    .setParameter("chatId", chatMessage.getId())
                    .list();

            if (!userSubscriptions.isEmpty()) {
                for (UserSubscription userSubscription : userSubscriptions) {
                    session.delete(userSubscription);
                }
                System.out.println("User " + user.getNickname() + " unsubscribed from chat " + chatMessage.getId());

                // Notify observers
                notifyUnsubscribe(user, chatMessage);
            } else {
                System.out.println("Subscription not found.");
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all subscriptions for a specific chat
    public List<UserSubscription> getSubscriptionsForChat(ChatMessage chatMessage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM UserSubscription WHERE chatMessage.id = :chatId";
            return session.createQuery(hql, UserSubscription.class)
                    .setParameter("chatId", chatMessage.getId())
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get all subscriptions for a specific user
    public List<UserSubscription> getSubscriptionsForUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM UserSubscription WHERE user.id = :userId";
            return session.createQuery(hql, UserSubscription.class)
                    .setParameter("userId", user.getId())
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Check if a user is subscribed to a specific chat
    public boolean isUserSubscribedToChat(User user, ChatMessage chatMessage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM UserSubscription WHERE user.id = :userId AND chatMessage.id = :chatId";
            List<UserSubscription> userSubscriptions = session.createQuery(hql, UserSubscription.class)
                    .setParameter("userId", user.getId())
                    .setParameter("chatId", chatMessage.getId())
                    .list();
            return !userSubscriptions.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all users subscribed to a specific chat
    public List<User> getUsersSubscribedToChat(ChatMessage chatMessage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT s.user FROM UserSubscription s WHERE s.chatMessage.id = :chatId";
            return session.createQuery(hql, User.class)
                    .setParameter("chatId", chatMessage.getId())
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
