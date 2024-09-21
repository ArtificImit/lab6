package commands;

import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * Реализация команды help
 */
public class Help extends Command{


    private Map<String, Class<? extends Command>> commandMap;
    public <T extends Ticket> Help(Collection<T> storage, String argument, T el, Map<String, Class<? extends Command>> commandMap) {
        super(storage, argument, el);
        this.commandMap = commandMap;
    }

    private <T extends Ticket> Help(Collection<T> storage, String argument, T el){
        super(storage, argument, el);
    }
    /**
     * Метод, выводящий справку по всем командам
     */
    @Override
    public Response execute() {
        ArrayList<String> res = new ArrayList<>();
        for(String name: this.commandMap.keySet()){
            String description = name;
            Class<? extends Command> command = this.commandMap.get(name);
            if(CommandUsingElement.class.isAssignableFrom(command))
                description += " {element, ввод элемента осуществляется в следующих 5 строках} ";
            try {
                Constructor<? extends Command> constructor = command.getDeclaredConstructor(Collection.class, String.class, Ticket.class);
                constructor.setAccessible(true);
                description += " : " + command.getMethod("getHelp").invoke(constructor.newInstance(collection, argument, el));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
            res.add(description);
        }
        return new Response(res.toArray());
    }

    @Override
    public String getHelp() {
        return "Выводит справку по доступным командам";
    }
}
