package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

/**
 *
 * Реализация команды show
 */
public class Show extends Command{

    public <T extends Ticket> Show(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }


    /**
     * Метод, выводящий все элементы коллекции в порядке их добавления
     */
    @Override
    public Response execute() {
        if(collection.size() > 0)
            return new Response(collection.stream().sorted().toArray());
        else
            return new Response("В коллекции нет элементов");
    }

    @Override
    public String getHelp() {
        return "Выводит  в стандартный поток вывода все элементы коллекции в строковом представлении";
    }
}
