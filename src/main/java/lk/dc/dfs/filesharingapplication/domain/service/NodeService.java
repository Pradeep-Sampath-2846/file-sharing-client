package lk.dc.dfs.filesharingapplication.domain.service;

import lk.dc.dfs.filesharingapplication.domain.entity.ClientNode;
import lk.dc.dfs.filesharingapplication.domain.service.core.GNode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    private final GNode currentNode;
    @Getter
    private List<ClientNode> searchResults = new ArrayList<>();
    @Value("${bootstrap.server.ip}")
    private String bootstrapIp;

    @Value("${bootstrap.server.port}")
    private int bootstrapPort;


    private int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }


    public NodeService(GNode currentNode) throws Exception {
        String uniqueID = UUID.randomUUID().toString();
        this.currentNode = currentNode;
        currentNode.init("node" + uniqueID);
        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void register(String bootstrapIp, int bootstrapPort, String ip, int port, String username) throws IOException {
//        this.currentNode = new ClientNode(ip, port, username);
//        String message = "REG " + ip + " " + port + " " + username;
//        sendToBootstrapServer(bootstrapIp, bootstrapPort, message);
//    }

    public void unregister( String ip, int port, String username) throws IOException {
        currentNode.unRegister();
    }

    public void searchFile(String fileName) throws IOException {
        int i = currentNode.doSearch(fileName);
        System.out.println(i);
    }


}
