package server;

import QA.Request;
import QA.Response;
import objectpack.Ticket;
import server.database.Collection;
import server.filework.*;
import server.userspace.User;
import server.util.InfoSender;
import server.util.OutStreamInfoSender;
import server.util.Pair;
import commands.Command;
import commands.ExecuteScript;
import commands.Save;
import commands.BlankCommand;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Класс - исполнитель комманд
 */
public class CommandExecuter {

    private static CommandExecuter server;
    /** Коллекция объектов типа Ticket
     * @see objectpack.Ticket
     */
    private Collection<Ticket> collection;

    /** Список команд, элементы которого пары вида (Название команды, Объект класса команды)
     * @see Pair
     */
    private LinkedList<Pair<String, Command>> history;
    private Invoker invoker;
    /** @see Invoker */
    private FileReader fileReader;
    /** @see FileReader */
    private InfoSender infoSender;
    /** @see InfoSender */
    private FileSaver fileSaver;
    /** @see FileSaver */
    private LinkedList<String> executedRecursionScript = new LinkedList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Статический метод, предоставляющий доступ к экземпляру класса исполнителя комманд
     */
    public static CommandExecuter getAccess(){
        if(server == null)
            server = new CommandExecuter();
        return server;
    }

    /** Конструктор класса, задающий все параметры и загрудающий коллекцию из файла
     * @see OutStreamInfoSender
     * @see FileInputStreamReader
     * @see CSVFileSaver
     * @see CSVLoader
     */
    private CommandExecuter()  {
        this.invoker = Invoker.getAccess();
        this.history = new LinkedList<>();
        this.infoSender = new OutStreamInfoSender();
        this.fileReader = new FileInputStreamReader();
        this.fileSaver = new CSVFileSaver();
        try {
            this.collection = (new CSVLoader(System.getenv("SAVEFILE"))).loadCollection();
        } catch (IOException e){
            File file = new File(System.getenv("SAVEFILE"));
            if(!file.exists())
                this.infoSender.sendLine("Невозможно загрузить коллекцию из файла. Файл не существует");
            else if(!file.canRead())
                this.infoSender.sendLine("Невозможно загрузить коллекцию из файла. Не хватает прав на чтение файла");
            else
                this.infoSender.sendLine("Невозможно загрузить коллекцию из файла. Ошибка в xml-тэгах");
            this.collection = new Collection<>();
        } catch(NullPointerException e){
            e.printStackTrace();
            this.infoSender.sendLine("Невозможно загрузить коллекцию из файла. Переменная SAVEFILE не определена");
        } catch (Exception e){
            this.infoSender.sendLine("Невозможно заргузить коллекцию. Ошибка в файле");
            this.collection = new Collection<>();
        }


    }

    public void addExecutedRecursionScript(String arg) {
        executedRecursionScript.add(arg);
    }
    public void clearExecutedRecursionScript() {
        executedRecursionScript.clear();
    }

    /**
     * Метод, который выбирает команду по ее названию, исполняет ее и записывает в историю команд.
     * Также здесь происходит парсинг объекта типа Vehicle из строк
     * @param request запрос
     */
    public Response executeCommand(Request request/*String command, ArrayList<String> element*/) {
        lock.writeLock().lock();
        try {
          //  User user = request.getUser();

            String command_name = request.command_name;
            String argument = request.argument;
            Ticket ticket = request.element;
            Response response;
            Command commandToExecute = this.invoker.getCommandToExecute(command_name, this.collection, argument, ticket, this.history);

            for (String s : executedRecursionScript) {
                if (s.equals(argument)) {
                    response = new Response("Рекурсия в скрипте! Инструкция пропущена. Скрипт продолжается...");
                    return response;
                }
            }

            if (request.sentFromClient) {
                if (commandToExecute instanceof ExecuteScript) {
                    this.executedRecursionScript.add(argument);
                } else if (request.saveFlag) {
                    response = new Response("Сохранение происходит автоматически");
                    request.saveFlag = false;
                    return response;
                }
            } else {
                System.out.println("Выполняется сохранение");
            }
            response = commandToExecute.execute();
            if (!(commandToExecute instanceof BlankCommand))
                this.writeCommandToHistory(new Pair<>(command_name, commandToExecute));

            return response;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public boolean checkExecutedRecursionScript(String toCheck) {
        if (executedRecursionScript.contains(toCheck)){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Метод добавляет команду в историю и если история содержит более 7 элементов удаляет первый.
     * @param command Команда в виде пары (Имя Команды, Объект Команды)
     */
    private void writeCommandToHistory(Pair<String, Command> command){
        if(this.history.size() == 7)
            this.history.removeFirst();
        this.history.add(command);
    }
    public void externalSave(){
        (new Save(this.collection, "", null)).execute();
    }

    public void setExecutedRecursionScript(LinkedList<String> executedRecursionScript) {
        this.executedRecursionScript = executedRecursionScript;
    }
}
