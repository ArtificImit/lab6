package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;
import server.util.Pair;
import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
/**
 *
 * Реализация команды history
 */
public class History extends Command{
    /**
     * История команд в виде пар (Имя, Объект класса команды)
     */
    private Deque<Pair<String, Command>> history;

    public <T extends Ticket> History(Collection<T> storage, String argument, T el, Deque<Pair<String, Command>> history) {
        super(storage, argument, el);
        this.history = history;
    }

    private <T extends Ticket> History(Collection<T> storage, String argument, T el){
        super(storage, argument, el);
    }
    /**
     * Метод, выводящий последние 7 команд
     */
    @Override
    public Response execute() {
        return new Response(history.stream().map(Pair::getFirst).collect(Collectors.toCollection(LinkedList::new)).toArray());
    }

    @Override
    public String getHelp() {
        return "Выводит последние 7 команд (без их аргументов)";
    }
}
