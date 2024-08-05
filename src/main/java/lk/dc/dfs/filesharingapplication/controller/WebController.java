package lk.dc.dfs.filesharingapplication.controller;

import lk.dc.dfs.filesharingapplication.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/")
    public String index(Model model) throws UnknownHostException {
        InetAddress localhost = InetAddress.getLocalHost();
        model.addAttribute("ipAddress", localhost.getHostAddress());
        model.addAttribute("port", nodeService.getCurrentNode().getPort());
        model.addAttribute("availableFiles", nodeService.listFiles());
        model.addAttribute("searchResults", nodeService.getSearchResults());
        return "index";
    }
}
