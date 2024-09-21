package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

/**
 * Реализация команды sort
 */
public class Sort extends Command {

    public <T extends Ticket> Sort(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }

    /**
     * Метод, сортирующий коллекцию по цене и возвращающий отсортированную коллекцию
     */
    //TODO проверить как он выводит если постваить .stream().sorted().toArray()
    @Override
    public Response execute() {
        if (!collection.isEmpty()) {
            collection.sortByCompareTo();
            return new Response(collection.stream().sorted().toArray());
        } else {
            return new Response("В коллекции нет элементов");
        }
    }

    @Override
    public String getHelp() {
        return "Сортирует коллекцию по цене и выводит отсортированные элементы.";
    }
}
