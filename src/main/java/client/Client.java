package client;

import QA.Request;
import QA.Response;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private final String server_address;
    private final int server_port;
    Socket socket;


    public Client(String server_address, int server_port) {
        this.server_address = server_address;
        this.server_port = server_port;
    }

    public Response start() {
        try {
            this.socket = new Socket(server_address, server_port);
            System.out.println("Подключение завершено");
            System.out.println("Пожалуйста зарегистрируйтесь или войдите");
            System.out.println("Команда для входа: login username password");
            System.out.println("Команда для register username password");
            return receiveResponse();
        } catch (IOException e) {
            System.out.println("Ошибка подключения: Сервер недоступен");
            System.exit(0);
        }
        return null;
    }

    public void sendRequest(Request request) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(request);
            outputStream.flush();


        } catch (SocketException e) {
            System.out.println("Ошибка: разорвано подключение с сервером");
            this.start();
            this.sendRequest(request);
        } catch (IOException e) {
            System.out.println("Ошибка при отправке запроса");
            e.printStackTrace();
        }
    }

    public Response receiveResponse() {
        try {

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Response response = (Response) inputStream.readObject();
            return response;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SocketException e) {
            //reconnect();
            System.out.println(socket.isClosed());
            return null;
        } catch (IOException e) {
            return null;
        }


    }



    @Override
    public String toString() {
        return Boolean.toString(socket.isClosed());
    }
}
