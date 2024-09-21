package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

/**
 *
 * Реализация команды exit
 */
public class Exit extends Command {
    public <T extends Ticket> Exit(Collection<T> storage, String argument, T el) {
        super(storage, argument, el);
    }

    /**
     * Метод, завершающий работу программы без сохранения коллекции
     */
    @Override
    public Response execute() {
        return new Response("До свидания!");
    }

    @Override
    public String getHelp() {
        return "Завершает программу (без сохранения в файл)";
    }
}
