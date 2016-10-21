import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class GetMessageThread extends Thread {
    private String loginName;

     GetMessageThread(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                URL getMessageUrl = new URL("http://localhost:8080/getMessages?loginName=" + loginName);
                HttpURLConnection http = (HttpURLConnection) getMessageUrl.openConnection();

                try (InputStream is = http.getInputStream()) {
                    String text = Main.getStringFromInputStream(is);
                    if (!text.isEmpty()) {
                        Gson gson = new GsonBuilder().create();

                        Message[] list = gson.fromJson(text, Message[].class);
                        for (Message m : list) {
                            System.out.println(m);
                        }
                    }
                } catch (Exception e) {
                    if (e.getMessage().contains("Server returned HTTP response code: 500")) {
                        System.out.println("You were removed from the chat due to inactivity");
                        break;
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }

                sleep(1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
