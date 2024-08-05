//package lk.dc.dfs.filesharingapplication.controller;
//
//import lk.dc.dfs.filesharingapplication.entity.ClientNode;
//import lk.dc.dfs.filesharingapplication.domain.service.NodeService;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/client")
//public class ClientController {
//
//    private final NodeService nodeService;
//
//    public ClientController(NodeService nodeService) {
//        this.nodeService = nodeService;
//    }
//
//    @PostMapping("/register")
//    public String register(@RequestParam String bootstrapIp, @RequestParam int bootstrapPort,
//                           @RequestParam String ip, @RequestParam int port, @RequestParam String username) {
//        try {
//            nodeService.register(bootstrapIp, bootstrapPort, ip, port, username);
//            return "Registered successfully";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Registration failed";
//        }
//    }
//
//    @PostMapping("/unregister")
//    public String unregister(@RequestParam String bootstrapIp, @RequestParam int bootstrapPort,
//                             @RequestParam String ip, @RequestParam int port, @RequestParam String username) {
//        try {
//            nodeService.unregister(ip, port, username);
//            return "Unregistered successfully";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Unregistration failed";
//        }
//    }
//
//    @GetMapping("/routing-table")
//    public List<ClientNode> getRoutingTable() {
//        return nodeService.getRoutingTable();
//    }
//
//    @PostMapping("/send-file")
//    public String sendFile(@RequestParam String recipientIp, @RequestParam int recipientPort,
//                           @RequestParam String filePath) {
//        try {
//            nodeService.sendFile(recipientIp, recipientPort, filePath);
//            return "File sent successfully";
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "File transfer failed";
//        }
//    }
//
//    @GetMapping("/search-file")
//    public void searchFile(@RequestParam String filename) throws IOException {
//        nodeService.searchFile(filename);
//    }
//}
//
