package com.denisindenbom.cyberauth.user;

public class User
{
    private final String name;
    private final String passwordHash;

    public User(String name, String passwordHash)
    {
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPasswordHash()
    {
        return this.passwordHash;
    }
}
