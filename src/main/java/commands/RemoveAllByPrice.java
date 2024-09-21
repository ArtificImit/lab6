package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.Iterator;

/**
 * Реализация команды remove_all_by_price
 */
public class RemoveAllByPrice extends Command {
    private Integer price; // Цена, по которой будет происходить удаление

    public <T extends Ticket> RemoveAllByPrice(Collection<T> collection, String argument, T el) {
        super(collection, argument, null);
        try {
            this.price = Integer.parseInt(argument);
        }
        catch (NumberFormatException e){
            this.price = null;
        }

    }

    /**
     * Метод, удаляющий все элементы с указанной ценой и возвращающий результат операции
     */
    @Override
    public Response execute() {
        int count = 0;
        Iterator<Ticket> iterator = this.collection.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.getPrice() != null && ticket.getPrice().equals(price)) {
                iterator.remove();
                count++;
            }
        }

        if (count > 0) {
            return new Response("Было успешно удалено " + count + " элементов");
        } else {
            return new Response("Нет элементов с такой ценой");
        }
    }

    @Override
    public String getHelp() {
        return "Удаляет все элементы из коллекции с указанной ценой";
    }
}
