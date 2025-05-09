package core.models;


import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatMessage chatMessage;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChatMessage getChat() {
        return chatMessage;
    }

    public void setChat(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
