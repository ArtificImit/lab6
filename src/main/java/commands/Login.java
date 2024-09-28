package commands;

import QA.Response;
import server.database.UserDatabase;
import server.userspace.User;

/**
 * Команда для входа пользователя в систему.
 */
public class Login extends Command implements Authentication {

    private UserDatabase userDb;
    private String username;
    private String password;

    public Login(UserDatabase userDb, String username, String password) {
        super(null, null, null); // не требует коллекции и элемента
        this.userDb = userDb;
        this.username = username;
        this.password = password;
    }

    @Override
    public Response execute() {
        if (userDb.authenticateUser(username, password)) {
            return new Response("Login successful.");
        } else {
            return new Response("Invalid username or password.");
        }
    }

    @Override
    public String getHelp() {
        return "Использование: login <username> <password>";
    }
}
