package com.denisindenbom.cyberauth.managers;

import com.denisindenbom.cyberauth.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserAuthManager
{
    private List<User> authenticatedUsers = new ArrayList<>();

    public UserAuthManager()
    {}

    public void addUser(User user)
    {
        this.authenticatedUsers.add(user);
    }

    public void removeUserByName(String name)
    {
        for (User user : this.authenticatedUsers)
        {
            if (user.getName().equals(name))
            {
                this.authenticatedUsers.remove(user);
                break;
            }
        }
    }

    public boolean userExists(String name)
    {
        for (User user : this.authenticatedUsers)
        {
            if (user.getName().equals(name)) return true;
        }

        return false;
    }

    public List<User> getAuthenticatedUsers()
    {
        return this.authenticatedUsers;
    }

    public void setAuthenticatedUsers(List<User> newAuthenticatedUsers)
    {
        this.authenticatedUsers = newAuthenticatedUsers;
    }
}
