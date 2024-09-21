package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.stream.Collectors;

/**
 * Реализация команды filter_starts_with_name
 */
public class FilterStartsWithName extends Command {
    /**
     * @see Collection
     */
    private Collection<? extends Ticket> collection;

    public <T extends Ticket> FilterStartsWithName(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
        this.collection = collection;
    }

    /**
     * Метод, выводящий все элементы коллекции, имена которых начинаются с заданной строки pattern
     */
    @Override
    public Response execute() {
        Collection<? extends Ticket> res = this.collection.stream()
                .filter(e -> e.getName().startsWith(this.argument))
                .collect(Collectors.toCollection(Collection::new));

        if (res.size() == 0) {
            return new Response("Совпадений не обнаружено");
        }

        return new Response(res.toArray());
    }

    @Override
    public String getHelp() {
        return "Выводит элементы, значение поля name которых начинается с заданной подстроки";
    }
}
