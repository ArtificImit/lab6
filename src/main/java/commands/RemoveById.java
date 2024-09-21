package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.NoSuchElementException;

/**
 *
 * Реализация команды remove_by_id
 */
public class RemoveById extends Command implements CommandWithId{
    /**
     * id элемента, который будет удален
     */

    public <T extends Ticket> RemoveById(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }


    /**
     * Метод, удаляющий элемент по его id и выводящий результат операци
     */
    @Override
    public Response execute() {
        try {
            Ticket ticket = collection.findTicketById(Integer.parseInt(argument));
            boolean res = this.collection.remove(ticket);
            if(res) {
                return new Response("Элемент удалён");
            }
        }
        catch (NoSuchElementException e){
            return new Response("Элемента с таким id в коллекции нет");
        }


        return new Response("Упси-дупси, не вышло");
    }

    @Override
    public String getHelp() {
        return "Удаляет элемент из коллекции по его id";
    }
}
