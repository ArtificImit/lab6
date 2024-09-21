package commands;
import objectpack.Ticket;
import QA.Response;
import server.database.Collection;

/**
 *
 * Абстрактный класс команды, который реализуют все команды

 */
public abstract class Command {

    protected Collection collection;
    protected String argument;
    protected Ticket el;

    public abstract Response execute();
    public abstract String getHelp();

    public <T extends Ticket> Command(Collection<T> collection, String argument, T el){
        this.collection = collection;
        this.argument = argument;
        this.el = el;
    }

    @Override
    public String toString() {
        return "Command{" +
                "collection=" + collection +
                ", argument='" + argument + '\'' +
                ", el=" + el +
                '}';
    }
}
