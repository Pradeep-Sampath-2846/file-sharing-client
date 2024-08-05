package lk.dc.dfs.filesharingapplication.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientNode {
    private String ip;
    private int port;
    private String username;
}
