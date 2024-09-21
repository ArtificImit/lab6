package server;

import QA.Request;
import QA.Response;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel clientChannel;
    private CommandExecuter commandExecuter;
    private static final ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Server.class);

    public Server(int port) {
        this.commandExecuter = CommandExecuter.getAccess();
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel.bind(new InetSocketAddress(port));
            logger.info("Сервер запущен на порту {}", port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    commandExecuter.externalSave();
                    logger.info("Состояние сохранено");
                } catch (Exception e) {
                    logger.error("Ошибка при сохранении состояния", e);
                }
            }));
        } catch (IOException e) {
            logger.error("Ошибка при создании серверного сокета", e);
            System.exit(0);
        }
    }

    @SneakyThrows
    private SocketChannel catchClient() {
        clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        logger.info("Подключился клиент");
        return clientChannel;
    }

    private void sendResponse(SocketChannel clientChannel, Response response) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(120000);
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
        outputStream.writeObject(response);
        outputStream.flush();

        byte[] data = byteArrayOutputStream.toByteArray();//оборачиваем в массив данных
        ByteBuffer buffer = ByteBuffer.wrap(data);// и в bytebuffer, делаем мы так потому что каналы работают напрямую с bytebuffer

        while (buffer.hasRemaining()) {//отправляем данные через канал сокета
            clientChannel.write(buffer);
        }
        logger.info("Отправлен ответ: " + response);
        outputStream.close();
        byteArrayOutputStream.close();
        buffer.clear();

        }

    private Request receiveRequest(SocketChannel clientChannel) throws IOException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(10240);  // Предполагаемый размер буфера
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            boolean dataReceived = false;

            while (true) {
                int bytesRead = clientChannel.read(buffer);  // Чтение данных из канала

                if (bytesRead != 4 && bytesRead > 0) {
                    dataReceived = true;
                    buffer.flip();  // Переключаемся в режим чтения из буфера
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);  // Извлекаем данные из буфера
                    byteArrayOutputStream.write(data);  // Записываем данные в поток
                    buffer.clear();  // Очищаем буфер для следующих данных

                } else if (bytesRead == 0) {
                    // Если данные ещё не получены, но канал остаётся активным
                    if (dataReceived) {
                        break;  // Выходим, если данные были получены, но новых больше не поступает
                    } else {
                        try {
                            Thread.sleep(30);  // Пауза для ожидания поступления данных
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                else if (bytesRead == -1) {
                    logger.info("Клиент закрыл соединение");
                    return null;
                }

            }

            // Преобразование данных в объект Request после завершения чтения
            byte[] requestData = byteArrayOutputStream.toByteArray();
            logger.info("Полученные данные длиной: " + requestData.length);

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestData);
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                return (Request) objectInputStream.readObject();

            } catch (ClassNotFoundException e) {
                logger.error("Ошибка при десериализации запроса", e);
                return null;
            }
        }
        catch (SocketException e){
            return null;
        }
    }
    private void handleServerCommands() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String command;
            while ((command = reader.readLine()) != null) {
                if ("save".equalsIgnoreCase(command)) {
                    commandExecuter.externalSave();
                    System.out.println("Сохранение завершено");
                } else if ("exit".equalsIgnoreCase(command)) {
                    shutdownServer();
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Ошибочка", e);
        }
    }

    private void shutdownServer() {
        try {
            if (clientChannel != null && clientChannel.isOpen()) {
                clientChannel.close();
                logger.info("Клиентский канал закрыт");
            }
            if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                serverSocketChannel.close();
                logger.info("Серверный сокет закрыт");
            }
            logger.info("Сервер завершил работу");
            System.exit(0); // Завершаем выполнение программы
        } catch (IOException e) {
            logger.error("Ошибка при завершении работы сервера", e);
        }
    }

    @SneakyThrows
    public void run() {
        Thread commandThread = new Thread(this::handleServerCommands);
        commandThread.start();
        logger.info("Сервер запущен");
        while (true) {
            logger.info("Ожидание клиента...");
            SocketChannel clientChannel = catchClient();
            logger.info("Клиент подключен");
            Response response = new Response(Invoker.getAccess().getCommandMapClone());
            sendResponse(clientChannel, response);
            while (true) {
                if (!clientChannel.isConnected()) {
                    clientChannel.close();
                    break;
                }
                logger.info("Ожидание запроса...");
                logger.info("Сокет закрыт?" + clientChannel.socket().isClosed());
                Request request = receiveRequest(clientChannel);
                if (request == null){
                    break;
                }
                logger.info("Запрос получен: {}", request);
                response = commandExecuter.executeCommand(request);
                sendResponse(clientChannel, response);

            }
        }
    }
}
