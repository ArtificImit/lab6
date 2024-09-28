package QA;

import objectpack.Ticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Реквест хранит четыре поля: имя команды, ее аргумент, элемент и отправителя
 */
public class Request implements Serializable {
    public final String command_name;
    public final String argument;
    public Ticket element;
    public boolean saveFlag;
    public final boolean sentFromClient;

    public Request(String command, ArrayList<String> element, boolean sentFromClient) {

        String[] commandParts = command.split(" ");
        this.command_name = commandParts[0];

        Ticket ticket = null;
        if(element.size()!= 0) {
            if (Objects.equals(command_name, "add")) {
                ticket = Ticket.parseTicket(element.toArray(new String[10]));
            }
            this.element = ticket;
        }

        if(commandParts.length > 1)
            this.argument = commandParts[1];
        else
            this.argument = null;
         //   this.argument = element.get(0);


        this.sentFromClient = sentFromClient;


    }

    @Override
    public String toString() {
        return "Request{" +
                "command_name='" + command_name + '\'' +
                ", argument='" + argument + '}';
    }
}
