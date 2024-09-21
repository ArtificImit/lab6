package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.util.NoSuchElementException;

/**
 *
 * Реализация команды update
 */
public class Update extends Command implements CommandWithId{

    public <T extends Ticket> Update(Collection<T> storage, String argument, T el) {
        super(storage, argument, el);
    }

    /**
     * Метод, обновляющий элемент в коллекции по его id и выводящий результат
     */
    @Override
    public Response execute() {
        try {
            Ticket ticket = collection.findTicketById(Integer.parseInt(argument));
            ticket.setId(Long.parseLong(argument));
            boolean res = this.collection.remove(ticket);
            if(res) {
                this.collection.add(ticket);
                return new Response("Элемент обновлен");
            }
        }
        catch (NoSuchElementException e){
            return new Response("Элемента с таким id в коллекции нет");
        }


        return new Response("Упси-дупси, не вышло");
    }

    @Override
    public String getHelp() {
        return "Обновляет значение элемента коллекции, id которого равен заданному";
    }
}
