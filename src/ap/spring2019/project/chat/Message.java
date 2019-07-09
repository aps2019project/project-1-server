package ap.spring2019.project.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Message {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");
    private static final ArrayList<Message> chat = new ArrayList<>();

    private Date date;
    private String message;
    private String userName;

    public static synchronized ArrayList<Message> getChat() {
        return chat;
    }

    public static synchronized void addMessage(Message message) {
        synchronized (chat) {
            chat.add(message);
        }
    }
}
