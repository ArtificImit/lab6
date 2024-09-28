package commands;

import QA.Response;
import server.database.UserDatabase;
import server.userspace.User;

/**
 * Команда для регистрации нового пользователя.
 */
public class Register extends Command implements Authentication{

    private UserDatabase userDb;
    private String username;
    private String password;

    public Register(UserDatabase userDb, String username, String password) {
        super(null, null, null); // не требует коллекции и элемента
        this.userDb = userDb;
        this.username = username;
        this.password = password;
    }

    @Override
    public Response execute() {
        if (userDb.registerUser(username, password)) {
            return new Response("Registration successful.");
        } else {
            return new Response("Username already exists.");
        }
    }

    @Override
    public String getHelp() {
        return "Использование: register <username> <password>";
    }
}
