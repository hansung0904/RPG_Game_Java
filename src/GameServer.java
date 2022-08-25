import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;


public class GameServer {
    private final ConcurrentHashMap<ClientSession, ConcurrentHashMap> clientMap =
            new ConcurrentHashMap<>(); // < session, clientOutMap>
    private final ConcurrentHashMap<String, DataOutputStream> clientOutMap =
            new ConcurrentHashMap(); // id string


    public static void main(String[] args) throws IOException {
        GameServer server = new GameServer();
        server.start();
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println(getTime() + " Start server " + serverSocket.getLocalSocketAddress());
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientSession client = new ClientSession(socket);
                    client.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss"));
    }

    private void joinGame(ClientSession session) {
        clientOutMap.put(session.id, session.out);
        System.out.println(
                getTime() + " " + session.id + " is joined: " + session.socket.getInetAddress());
        sendToAll("[System] " + session.id + "님이 게임에 접속했습니다.");

        loggingCurrentClientCount();
    }

    private void leaveGame(ClientSession session) {
        clientMap.remove(session.id);
        clientOutMap.remove(session.id);
        sendToAll("[System] " + session.id + "님이 게임을 종료했습니다.");
        System.out.println(
                getTime() + " " + session.id + " is leaved: " + session.socket.getInetAddress());
        loggingCurrentClientCount();
    }

    private void loggingCurrentClientCount() {
        System.out.println(
                getTime() + " Currently " + clientOutMap.size() + " clients are connected.");
    }

    private void sendToAll(String message) {
        for (DataOutputStream out : clientOutMap.values()) {
            try {
                out.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    class ClientSession extends Thread {
        private final Socket socket;
        private final DataInputStream in;
        private final DataOutputStream out;
        private String id;


        ClientSession(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        }

        private void sendToClient(String msg) {
            try {
                out.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void initialize() {
            try {
                this.id = in.readUTF();
                joinGame(this);

            } catch (IOException cause) {
                cause.printStackTrace();
            }
        }


        private boolean isConnect() {
            return this.in != null;
        }

        private void disconnect() {
            leaveGame(this);
        }


        private String startGame(String id) {

            return null;
        }

        private String getClientAnswer() {
            String result = "";
            try {
                result = in.readUTF();

                if (result.equals("2")) {
                    disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }


        private String enterGameStage(String monsterState) {

            return null;
        }


        private String winGame(String id) {

            return null;
        }

        private String loseGame(String id) {

            return null;
        }

    }
}