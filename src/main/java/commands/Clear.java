package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

/**
 * Реализация команды clear
 */
public class Clear extends Command{


    public <T extends Ticket> Clear(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }

    /**
     * Метод, очищающий коллекцию
     */
    @Override
    public Response execute() {
        this.collection.clear();
        return new Response("Коллекция очищена");
    }

    @Override
    public String getHelp() {
        return "Очищает коллекцию";
    }
}
