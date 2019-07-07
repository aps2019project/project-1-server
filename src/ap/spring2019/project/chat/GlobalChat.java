package ap.spring2019.project.chat;

import java.util.ArrayList;

public class GlobalChat {

    private static final GlobalChat instance = new GlobalChat();

    private ArrayList<Message> messages;

    private GlobalChat() {
        messages = new ArrayList<>();
    }

    public static GlobalChat getInstance() {
        return instance;
    }

    public static synchronized ArrayList<Message> getMessages() {
        return instance.messages;
    }

    public static synchronized ArrayList<Message> getMessages(String userName) {
        ArrayList<Message> userMessages = new ArrayList<>();
        instance.messages.forEach(message -> {
            if (message.getUserName().equals(userName))
                userMessages.add(message);
        });
        return userMessages;
    }

    



}
