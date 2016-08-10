package com.framgia.ishipper.server;
import com.framgia.ishipper.model.User;
import java.util.List;

/**
 * Created by vuduychuong1994 on 8/9/16.
 */
public class ShipperNearbyResponse {


    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
