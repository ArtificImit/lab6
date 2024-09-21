package server;
import objectpack.Ticket;
import commands.*;
import server.database.Collection;

import java.util.Deque;

public class Invoker {
    private static Invoker invoker;

    private CommandMap commandMap;

    private Invoker(){
        this.commandMap = new CommandMap();
    }

    public CommandMap getCommandMapClone(){
        return (CommandMap) commandMap.clone();
    }

    public static Invoker getAccess(){
        if(invoker == null)
            invoker = new Invoker();
        return invoker;
    }


    public void register(String name, Class<? extends Command> command){
        this.commandMap.put(name, command);

    }

    public <T extends Ticket> Command getCommandToExecute(String commandName, Collection<T> collection, String argument, T el, Deque history) {
        Command instance = new BlankCommand(collection, argument, el, commandName);
        if(this.commandMap.containsKey(commandName)) {
            try {
                Class<? extends  Command> command = this.commandMap.get(commandName);
                if(command.equals(History.class)){
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el, history);
                } else if(command.equals(Help.class)){
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el, commandMap);
                }
                else{
                    instance = (Command) command.getConstructors()[0].newInstance(collection, argument, el);
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        return instance;

    }
}
