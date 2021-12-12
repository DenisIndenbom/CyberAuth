package com.denisindenbom.cyberauth.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.denisindenbom.cyberauth.user.User;
import org.jetbrains.annotations.NotNull;

public class CyberAuthDB extends DBManager
{
    public CyberAuthDB(String path) throws SQLException
    {
        this.connectToDB(path);
    }

    public void createDefaultDB()
    {
        // create a default database if the database is not initialized
        try
        {
            // create table
            this.executeUpdate("create table users (name char(50) not null, password_hash char, PRIMARY KEY (name))");
            this.commit();
        }
        catch (SQLException ignored)
        {
            try
            {
                this.rollback();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        } // we ignore the exception because we believe that the database has already been created
    }

    public boolean addUser(@NotNull User user)
    {
        String sqlRequest = "insert into users (name, password_hash) values (?, ?)";

        try
        {   // add user to db
            this.executeUpdate(sqlRequest, user.getName(), user.getPasswordHash());
            // commit
            this.commit();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean changePasswordHash(String name, String newPasswordHash)
    {
        String sqlRequest = "update users set password_hash = ? where name = ?";

        try
        {   // add user to db
            this.executeUpdate(sqlRequest, newPasswordHash, name);
            // commit
            this.commit();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public User getUser(String name)
    {
        String sqlRequest = "select * from users where name=?";
        try
        {
            // make a sql query to get player data from the database
            ResultSet resultSet = this.executeQuery(sqlRequest, name);

            return new User(resultSet.getString("name"), resultSet.getString("password_hash"));
        }
        catch (SQLException e)
        {
            return new User("", "");
        }
    }

    public boolean userIs(String name)
    {
        String sqlRequest = "select * from users where name=?";
        try
        {
            // make a sql query to get player data from the database
            ResultSet resultSet = this.executeQuery(sqlRequest, name);
            return resultSet.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
