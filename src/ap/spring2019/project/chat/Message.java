package ap.spring2019.project.chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss");

    private Date date;
    private String message;
    private String userName;

    public Message(String userName, String message) {
        this.date = new Date();
        this.message = message;
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }
}
