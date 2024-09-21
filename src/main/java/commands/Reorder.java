package commands;

import QA.Response;
import objectpack.Ticket;
import server.database.Collection;

public class Reorder extends Command {

    public <T extends Ticket> Reorder(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }

//TODO смотреть sort
    @Override
    public Response execute() {
        if (collection.size() > 0) {
            collection.reorder();
            return new Response(collection.toArray());
        } else {
            return new Response("В коллекции нет элементов");
        }
    }

    @Override
    public String getHelp() {
        return "Сортирует коллекцию в обратном порядке и выводит отсортированные элементы.";
    }
}
