package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.Comparator;
import java.util.Optional;

/**
 * Реализация команды min_by_creation_date
 */
public class MinByCreationDate extends Command {
    /**
     * @see Collection
     */
    private Collection<? extends Ticket> collection;

    public <T extends Ticket> MinByCreationDate(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
        this.collection = collection;
    }

    /**
     * Метод, находящий элемент коллекции с минимальной датой создания
     */
    @Override
    public Response execute() {
        Optional<? extends Ticket> minTicket = this.collection.stream()
                .min(Comparator.comparing((Ticket t) -> t.getCreationDate()));

        return minTicket.map(ticket -> new Response(ticket.toString())).orElseGet(() -> new Response("Коллекция пуста"));

    }

    @Override
    public String getHelp() {
        return "Находит элемент с минимальной датой создания";
    }
}
