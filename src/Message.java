import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date date = new Date();
    private String chat;
    private String from;
    private String to;
    private String text;

    public Message() {
        chat = "common";
        to = "all";
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static Message fromJSON(String s) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(s, Message.class);
    }

    @Override
    public String toString() {
        if("all".equals(to)) {
            return "[" + date.toString() + " , Chat: " + chat +
                    ", From: " + from + "] " + text;
        } else {
            return "[Private message " + date.toString() + " , Chat: " + chat +
                    ", From: " + from + "] " + text;
        }
    }

    public String send(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            String json = toJSON();
            os.write(json.getBytes());

            return conn.getContentType();
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
