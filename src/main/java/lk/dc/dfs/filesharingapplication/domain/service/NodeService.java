package lk.dc.dfs.filesharingapplication.domain.service;

import lk.dc.dfs.filesharingapplication.domain.service.core.GNode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

@Service
public class NodeService {

    @Getter
    private final GNode currentNode;
    private final String bootstrapIp;
    private final int bootstrapPort;


    private int getAvailablePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }


    public NodeService(GNode currentNode,
                       @Value("${bootstrap.server.ip}") String bootstrapIp,
                       @Value("${bootstrap.server.port}") int bootstrapPort) throws Exception {
        this.bootstrapIp = bootstrapIp;
        this.bootstrapPort = bootstrapPort;
        String uniqueID = UUID.randomUUID().toString();
        this.currentNode = currentNode;
        currentNode.init("node" + uniqueID);
        try {
            DatagramSocket socket = new DatagramSocket();
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
