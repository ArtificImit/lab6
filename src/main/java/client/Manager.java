package client;

import QA.Request;
import QA.Response;
import commands.CommandMap;
import commands.CommandUsingElement;
import commands.CommandWithId;
import objectpack.EventType;
import objectpack.Ticket;
import objectpack.TicketType;
import objectpack.exceptions.TicketException;

import java.util.*;

/**
 *
 *Класс Manager представляет собой текстовый интерфейс для взаимодействия пользователя с сервером.
 * Он считывает команды, вводимые пользователем, проверяет их валидность, собирает необходимые аргументы,
 * а затем отправляет запросы на сервер с использованием клиента.
 */
public class Manager {
    /**
     * Сканер для считывания введенных данных
     */
    private Scanner sc; //используется для считывания пользовательского ввода с консоли.

    private Client client; //который управляет сетевым подключением и отправкой/приемом данных.

    private CommandMap commandMap; //карта команд, которая связывает имена команд с их реализациями.

    /**
     * Метод запуска
     */
    public void start(){
        Response response;
        response = this.client.start();
        this.commandMap = (CommandMap) response.getResponse()[0];

        System.out.println("Здравсвтуйте, для получения справки по командам введите help");
        run();
    }

    /**
     * Конструктор инициализирует сканер и создает объект Client, используя адрес и порт сервера, к которому нужно подключиться.
     */
    public Manager(String server_address, int server_port) {
        this.sc = new Scanner(System.in);
        this.client = new Client(server_address, server_port);
    }
    /**
     * Этот метод проверяет, требуется ли команде дополнительный элемент (например, объект класса нашей коллекции).
     */
    public boolean checkIfNeedElement(String commandName){
        return CommandUsingElement.class.isAssignableFrom(this.commandMap.get(commandName));
    }
    public boolean checkIfNeedId(String commandName){
        return CommandWithId.class.isAssignableFrom(this.commandMap.get(commandName));
    }
    /**
     *
     * @param msg Сообщение, которое говорит пользователю что и как вводить
     * @param checker Чекер для проверки аргументов на валидность
     * @see ArgumentChecker
     * @return Строка - валидный аргумент
     */
    private String getArgumentWithRules(String msg, ArgumentChecker<String> checker){
        String arg = "";
        System.out.println(msg);
        arg = this.sc.nextLine();
        while (!checker.check(arg)){
            System.out.println("Неверный формат ввода. Попробуйте еще раз.");
            System.out.println(msg);
            arg = this.sc.nextLine();
        }
        return arg;
    }
    private ArrayList<String> readElement(){
        ArrayList<String> args = new ArrayList<String>();

        args.add(getArgumentWithRules("Введите имя (непустая строка)",
                arg -> !arg.trim().isEmpty()));

        args.add(getArgumentWithRules("Введите первую координату в формате: x - целое число",
                arg -> ArgumentValidator.checkX(arg)));
        args.add(getArgumentWithRules("Введите вторую координату в формате: y - число с дробной частью",
                arg -> ArgumentValidator.checkY(arg)));

        args.add(getArgumentWithRules("Введите цену билета (целое положительное число, может быть пустым):",
                arg -> ArgumentValidator.checkPrice(arg)));

        args.add(getArgumentWithRules("Введите скидку на билет (целое число от 1 до 100):",
                arg -> ArgumentValidator.checkDiscount(arg)));

        args.add(getArgumentWithRules("Введите возможность возврата (true/false, может быть пустым):",
                arg -> ArgumentValidator.checkRefundable(arg)));

        List<TicketType> possibleTypes = Arrays.asList(TicketType.values());
        ArrayList<String> possibleTypesStr = new ArrayList<>();
        for (TicketType type : possibleTypes) {
            possibleTypesStr.add(type.toString());
        }
        args.add(getArgumentWithRules("Введите тип билета из представленных " + possibleTypesStr.toString() + ":",
                arg -> ArgumentValidator.checkType(arg, TicketType.class)));

        List<EventType> possibleEventTypes = Arrays.asList(EventType.values());
        ArrayList<String> possibleEventTypesStr = new ArrayList<>();
        for (EventType type : possibleEventTypes) {
            possibleEventTypesStr.add(type.toString());
        }
        args.add(getArgumentWithRules("Введите тип события из представленных " + possibleEventTypesStr.toString() + ":",
                arg -> ArgumentValidator.checkType(arg, EventType.class)));

        args.add(getArgumentWithRules("Введите имя события (непустая строка):",
                arg -> !arg.trim().isEmpty()));

        args.add(getArgumentWithRules("Введите дату проведения события (в формате 2024-09-03T12:00:00, можете и не вводить):",
                arg -> !arg.trim().isEmpty()));


        return args;
    }


    /**
     * Проверяет на валидность аргумент команды, если тот должен быть id
     * @param commandToCheck команда, аргумент которой нужно проверить
     * @return true, если id валиден, false в ином случае
     */

    /**
     * Основной метод для работы с пользователем и чтения введенных команд
     */
    public void run() {
        String command = "";
        while(!command.equals("exit")) {
            ArrayList<String> element = new ArrayList<>();
            try {
                command = sc.nextLine();
                if(command.equals(""))
                    continue;
                String[] commandToCheck = command.split(" ");
                if(this.checkIfNeedId(commandToCheck[0])){
                    if(!ArgumentValidator.checkId(commandToCheck))
                        continue;
                }
                if (this.checkIfNeedElement(commandToCheck[0]))
                    element = this.readElement();
                if (Objects.equals(commandToCheck[0], "save")){
                    Request request = new Request("", element, true);
                    request.saveFlag = true;
                    this.client.sendRequest(request);
                }
                else {
                    Request request = new Request(command, element, true);
                    this.client.sendRequest(request);
                }
            }
            catch (NoSuchElementException e){
                sc.close();
                System.out.println("Программа завершена");
                Request request = new Request("exit", element, true);
                this.client.sendRequest(request);
            }

            System.out.println(this.client.receiveResponse());
        }
    }



}

