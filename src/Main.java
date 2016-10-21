
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        String loginName = null;
        try (Scanner scanner = new Scanner(System.in)) {
            String duplicate = "true";
            while (duplicate.equals("true")) {
                System.out.println("Enter not duplicate login: ");
                loginName = scanner.nextLine();

                HttpURLConnection conn = connectionToUrl("http://localhost:8080/addClient?loginName=" + loginName);

                duplicate = conn.getContentType();
                System.out.println(duplicate);
            }

            System.out.println("Connected to chat. Room: common");

            GetMessageThread thread = new GetMessageThread(loginName);
            thread.setDaemon(true);
            thread.start();

            printMenu();
            while (thread.isAlive()) {
                System.out.println("Enter number of menu: ");
                String number = scanner.nextLine();
                if (thread.isAlive())
                    switch (number) {
                        case "1":
                            printMenu();
                            break;
                        case "2":
                            createRoom(scanner, loginName);
                            break;
                        case "3":
                            enterRoom(scanner, loginName);
                            break;
                        case "4":
                            sendMessage(scanner, loginName, "all");
                            break;
                        case "5":
                            sendPrivateMessage(scanner, loginName);
                            break;
                        case "6":
                            getAllClients();
                            break;
                        case "7":
                            getClientStatus(scanner);
                            break;
                        default:
                            return;
                    }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    static String getStringFromInputStream(InputStream is) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(is);
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    private static void printMenu() {
        System.out.println("1: Print this menu. 2: Create room. 3: Enter room. 4: Send message. 5: Send private message. " +
                "6: Get all clients. 7. Checking client status. 8: Leave chat");

    }

    private static void createRoom(Scanner scanner, String loginName) throws IOException {
        System.out.println("Enter room name to create it: ");
        String room = scanner.nextLine();

        HttpURLConnection conn = connectionToUrl("http://localhost:8080/createChatRoom?loginName=" + loginName +
                "&room=" + room);
        System.out.println(conn.getContentType());
    }

    private static void enterRoom(Scanner scanner, String loginName) throws IOException {
        System.out.println("Enter room name to enter: ");
        String room = scanner.nextLine();

        HttpURLConnection conn = connectionToUrl("http://localhost:8080/enterChatRoom?loginName=" + loginName +
                "&room=" + room);
        System.out.println(conn.getContentType());
    }

    private static void sendMessage(Scanner scanner, String from, String to) throws IOException {
        System.out.println("Enter your message: ");
        String text = scanner.nextLine();

        Message m = new Message();
        m.setFrom(from);
        m.setTo(to);
        m.setText(text);
        String error = m.send("http://localhost:8080/addMessage");
        if ("error".equals(error)) {
            System.out.println("Message not sent!");
        }
    }

    private static void sendPrivateMessage(Scanner scanner, String from) throws IOException {
        System.out.println("Enter the name to whom to send: ");
        String to = scanner.nextLine();

        sendMessage(scanner, from, to);
    }

    private static void getAllClients() throws IOException {
        HttpURLConnection conn = connectionToUrl("http://localhost:8080/getAllClients");
        System.out.println(conn.getContentType());
    }

    private static void getClientStatus(Scanner scanner) throws IOException {
        System.out.println("Enter client name: ");
        String loginName = scanner.nextLine();

        HttpURLConnection conn = connectionToUrl("http://localhost:8080/getClientStatus?loginName=" + loginName);
        System.out.println(conn.getContentType());
    }

    private static HttpURLConnection connectionToUrl(String url) throws IOException {
        URL enterRoomURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) enterRoomURL.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        return conn;
    }
}
