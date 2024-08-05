package lk.dc.dfs.filesharingapplication.application.controller;

import lk.dc.dfs.filesharingapplication.domain.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class WebController {

    @Autowired
    private NodeService nodeService;

    @GetMapping("/")
    public String index(Model model) throws IOException {
        nodeService.searchFile("harry potter");
        return "index";
    }
}
