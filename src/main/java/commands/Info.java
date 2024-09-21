package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * Реализация команды info
 */
public class Info extends Command{

    public <T extends Ticket> Info(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }

    /**
     * Метод, выводящий типа, дату создания и количество элементов коллекци
     */
    @Override
    public Response execute() {
        List<String> response = new LinkedList<>();
        response.add("Тип коллекции " + collection.getClass());
        response.add("Дата создания " + this.collection.getCreationDate());
        response.add("Количество элементов " + collection.size());
        return new Response(response.toArray());
    }

    @Override
    public String getHelp() {
        return "Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
