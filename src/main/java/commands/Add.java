package commands;

import QA.Response;
import objectpack.Ticket;
import server.database.Collection;

/**
 *
 * Реализация команды add
 */
public class Add extends Command implements CommandUsingElement{

    public <T extends Ticket> Add(Collection<T> collection, String argument, T el) {
        super(collection, argument, el);
    }

    /**
     * Метод, добавляющий элемент в коллекцию и выводящий результат (добавлен или не добавлен)
     */
    @Override
    public Response execute() {

        if(this.collection.add(el))
            return new Response("Элемент добавлен");
        else
            return new Response("Элемент не был добавлен");
    }

    @Override
    public String getHelp() {
        return "добавляет новый элемент в коллекцию, ввод элемента осущестлявется в следующих 5 строках";
    }
}
