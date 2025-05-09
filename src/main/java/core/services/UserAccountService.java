package core.services;

import core.models.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.List;

public class UserAccountService {

    // Register a new user
    public void registerUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
            System.out.println("User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Login a user
    public User loginUser(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE email = :email AND password = :password";
            User user = session.createQuery(hql, User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();

            if (user != null) {
                System.out.println("Login successful!");
                return user;
            } else {
                System.out.println("Invalid email or password.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update user details
    public void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
            System.out.println("User updated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get all users
    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get a user by ID
    public static User getUserById(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, userId);
            if (user != null) {
                return user;
            } else {
                System.out.println("User not found with ID: " + userId);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Delete a user by ID
    public static boolean deleteUser(int userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            // Use getUserById to get the user
            User user = getUserById(userId);

            if (user != null) {
                session.delete(user);
                System.out.println("User deleted successfully!");
                transaction.commit();
                return true;
            } else {
                System.out.println("User not found with ID: " + userId);
                transaction.commit();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get a user by nickname
    public User getUserByNickname(String nickname) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM User WHERE nickname = :nickname";
            User user = session.createQuery(hql, User.class)
                    .setParameter("nickname", nickname)
                    .uniqueResult();

            if (user != null) {
                return user;
            } else {
                System.out.println("User not found with nickname: " + nickname);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
