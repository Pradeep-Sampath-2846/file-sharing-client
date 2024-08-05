package lk.dc.dfs.filesharingapplication.service;

import lk.dc.dfs.filesharingapplication.entity.ClientNode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

@Service
public class NodeService {

    private DatagramSocket socket;
    @Getter
    private List<ClientNode> routingTable = new ArrayList<>();
    @Getter
    private ClientNode currentNode;
    @Getter
    private List<ClientNode> searchResults = new ArrayList<>();
    @Value("${bootstrap.server.ip}")
    private String bootstrapIp;

    @Value("${bootstrap.server.port}")
    private int bootstrapPort;

    @PostConstruct
    public void init() {
        try {
            int port = getAvailablePort();
            String username = UUID.randomUUID().toString();
            register(bootstrapIp, bootstrapPort, InetAddress.getLocalHost().getHostAddress(), port, username);
            handleSearchRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PreDestroy
    public void destroy() throws Exception {
        unregister(currentNode.getIp(), currentNode.getPort(), currentNode.getUsername());
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
    private int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }


    public NodeService() {
        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(String bootstrapIp, int bootstrapPort, String ip, int port, String username) throws IOException {
        this.currentNode = new ClientNode(ip, port, username);
        String message = "REG " + ip + " " + port + " " + username;
        sendToBootstrapServer(bootstrapIp, bootstrapPort, message);
    }

    public void unregister( String ip, int port, String username) throws IOException {
        String message = "UNREG " + ip + " " + port + " " + username;
        sendToBootstrapServer(bootstrapIp, bootstrapPort, message);
    }

    public void echo(String message) throws IOException {
        String msg = "ECHO "+message;
        sendToBootstrapServer(bootstrapIp, bootstrapPort, message);
    }
    public void sendToBootstrapServer(String bootstrapIp, int bootstrapPort, String message) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(bootstrapIp), bootstrapPort);
        socket.send(packet);

        buffer = new byte[65536];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String response = new String(packet.getData(), 0, packet.getLength());
        processResponse(response);
    }

    private void processResponse(String response) {
        StringTokenizer st = new StringTokenizer(response, " ");
        st.nextToken(); // Skip length
        String command = st.nextToken();

        if (command.equals("REGOK")) {
            routingTable.clear();
            while (st.hasMoreTokens()) {
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                routingTable.add(new ClientNode(ip, port, ""));
            }
        }
        // Handle other responses (UNROK, ECHOK, etc.)
    }

    public void sendFile(String recipientIp, int recipientPort, String filePath) throws IOException {
        File file = new File(filePath);
        try (Socket socket = new Socket(recipientIp, recipientPort);
             FileInputStream fis = new FileInputStream(file);
             OutputStream os = socket.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
    public List<ClientNode> searchFile(String filename) throws IOException {
        List<ClientNode> availableNodes = new ArrayList<>();
        for (ClientNode node : routingTable) {
            if (requestFileAvailability(node, filename)) {
                availableNodes.add(node);
            }
        }
        return availableNodes;
    }
    private boolean requestFileAvailability(ClientNode node, String filename) throws IOException {
        String message = "SEARCH " + filename;
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(node.getIp()), node.getPort());
        socket.send(packet);

        buffer = new byte[65536];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String response = new String(packet.getData(), 0, packet.getLength());
        return response.contains("FOUND");
    }
    public byte[] getFile(String filename) throws IOException {
        Path filePath = Paths.get("resources/files/" + filename);
        return Files.readAllBytes(filePath);
    }
    // Method to list available files on the current node
    public List<String> listFiles() {
        File folder = new File("resources/files/");
        File[] files = folder.listFiles();
        List<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }
    // New method to handle incoming search requests
    public void handleSearchRequests() {
        new Thread(() -> {
            try (DatagramSocket searchSocket = new DatagramSocket(currentNode.getPort())) {
                while (true) {
                    byte[] buffer = new byte[65536];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    searchSocket.receive(packet);

                    String request = new String(packet.getData(), 0, packet.getLength());
                    if (request.startsWith("SEARCH")) {
                        String filename = request.split(" ")[1];
                        File file = new File("resources/files/" + filename);
                        String response = file.exists() ? "FOUND" : "NOT_FOUND";
                        DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.length(), packet.getAddress(), packet.getPort());
                        searchSocket.send(responsePacket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
