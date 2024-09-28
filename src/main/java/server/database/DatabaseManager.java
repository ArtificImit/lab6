package server.database;

import objectpack.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() throws SQLException {
        String url = "jdbc:postgresql://localhost:8387/UserDatabase";
        String user = "s412968";
        String password = "dZEZ<0434";
        connection = DriverManager.getConnection(url, user, password);
    }

    // Метод для добавления Ticket в базу данных
    public boolean addTicket(Ticket ticket) {
        String insertTicketQuery = "INSERT INTO tickets (id, name, price, discount, refundable, type, event_id, x_coord, y_coord, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertTicketQuery)) {
            insertStmt.setLong(1, ticket.getId());
            insertStmt.setString(2, ticket.getName());
            insertStmt.setInt(3, ticket.getPrice() != null ? ticket.getPrice() : 1);
            insertStmt.setLong(4, ticket.getDiscount());
            insertStmt.setBoolean(5, ticket.getRefundable() != null ? ticket.getRefundable() : false);
            insertStmt.setString(6, ticket.getType().name());
            insertStmt.setInt(7, ticket.getEvent() != null ? ticket.getEvent().getId() : null);
            insertStmt.setInt(8, ticket.getCoordinates().getX());
            insertStmt.setDouble(9, ticket.getCoordinates().getY());
            insertStmt.setTimestamp(10, Timestamp.valueOf(ticket.getCreationDate().toLocalDateTime()));

            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для добавления Event в базу данных
    public boolean addEvent(Event event) {
        String insertEventQuery = "INSERT INTO events (id, name, event_date, event_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertEventQuery)) {
            insertStmt.setInt(1, event.getId());
            insertStmt.setString(2, event.getName());
            insertStmt.setTimestamp(3, event.getDate() != null ? Timestamp.valueOf(event.getDate()) : null);
            insertStmt.setString(4, event.getEventType().name());

            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Метод для получения Ticket из базы данных
    public Ticket getTicketById(Long id) {
        String selectTicketQuery = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectTicketQuery)) {
            selectStmt.setLong(1, id);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                // Извлечение полей для объекта Ticket
                String name = resultSet.getString("name");
                Integer price = resultSet.getInt("price");
                Long discount = resultSet.getLong("discount");
                Boolean refundable = resultSet.getBoolean("refundable");
                String type = resultSet.getString("type");
                int eventId = resultSet.getInt("event_id");
                int x = resultSet.getInt("x_coord");
                double y = resultSet.getDouble("y_coord");
                ZonedDateTime creationDate = resultSet.getTimestamp("creation_date").toLocalDateTime().atZone(ZoneId.systemDefault());

                // Восстановление связанных объектов
                Event event = getEventById(eventId);
                Coordinates coordinates = new Coordinates(x, y);

                return new Ticket(id, name, coordinates, creationDate, price, discount, refundable, TicketType.valueOf(type), event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Метод для получения Event по ID
    public Event getEventById(int id) {
        String selectEventQuery = "SELECT * FROM events WHERE id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectEventQuery)) {
            selectStmt.setInt(1, id);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                LocalDateTime date = resultSet.getTimestamp("event_date") != null ? resultSet.getTimestamp("event_date").toLocalDateTime() : null;
                String eventType = resultSet.getString("event_type");

                return new Event(id, name, date, EventType.valueOf(eventType));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
