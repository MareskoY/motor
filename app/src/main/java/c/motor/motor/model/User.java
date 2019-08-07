package c.motor.motor.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put("name", username);
        result.put("email", email);

        return result;
    }
    public static User documentSnapshotToUser(DocumentSnapshot documentSnapshot){
        User user = new User(
                documentSnapshot.get("name").toString(),
                documentSnapshot.get("email").toString());
        return user;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}


