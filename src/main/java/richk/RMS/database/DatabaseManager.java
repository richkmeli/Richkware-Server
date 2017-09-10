package richk.RMS.database;

import richk.RMS.model.Device;
import richk.RMS.model.Model;
import richk.RMS.model.ModelException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DatabaseManager implements Model {
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String schemaDbName;
    private String tableDbName;

    public DatabaseManager() throws DatabaseException {
        ResourceBundle resource = ResourceBundle.getBundle("configuration");
        String dbClass = null;

        if (resource.getString("database.name").equals("mysql")) {
            dbUsername = resource.getString("database.username");
            dbPassword = resource.getString("database.password");
            dbUrl = resource.getString("database.url");
            schemaDbName = "RichkwareMS";
        } else if (resource.getString("database.name").equals("openshift_mysql")) {
            dbUsername = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
            dbPassword = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
            dbUrl = "jdbc:" + "mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/";
            schemaDbName = System.getenv("OPENSHIFT_APP_NAME");
        }

        dbClass = "com.mysql.jdbc.Driver";
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException(e);
        }

        // database schema creation
        tableDbName = schemaDbName + ".device";
        try {
            CreateDeviceSchema();
            dbUrl += schemaDbName;
            CreateDeviceTable();
        } catch (ModelException e) {
            throw new DatabaseException(e);
        }
    }

    private Connection connect() throws DatabaseException {
        try {
            return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private void disconnect(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) throws DatabaseException {
        try {
            resultSet.close();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        } catch (Exception e1) {        // null value of ResultSet in AddDevice, RemoveDevice...
        }
        try {
            preparedStatement.close();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }
        try {
            connection.close();
        } catch (Exception e) {
            throw new DatabaseException(e);
        }

    }

    public boolean CreateDeviceSchema() throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String schemaSQL = "CREATE SCHEMA " + schemaDbName;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(schemaSQL);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    public boolean CreateDeviceTable() throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        String tableSQL = "CREATE TABLE " + tableDbName + "(" +
                "Name VARCHAR(50) NOT NULL PRIMARY KEY," +
                "IP VARCHAR(25) NOT NULL," +
                "ServerPort VARCHAR(10)," +
                "LastConnection VARCHAR(25)," +
                "EncryptionKey VARCHAR(32)" +
                ")";

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement(tableSQL);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    public List<Device> RefreshDevice() throws ModelException {
        List<Device> deviceList = new ArrayList<Device>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + tableDbName);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Device tmp = new Device(resultSet.getString("Name"), resultSet.getString("IP"), resultSet.getString("ServerPort"), resultSet.getString("LastConnection"), resultSet.getString("EncryptionKey"));
                deviceList.add(tmp);
            }
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, resultSet);
            throw new ModelException(e);
        }
        disconnect(connection, preparedStatement, resultSet);
        return deviceList;
    }

    public boolean AddDevice(Device device) throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("INSERT INTO " + tableDbName + " (name, ip, serverport, lastconnection, encryptionkey) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, device.getName());
            preparedStatement.setString(2, device.getIP());
            preparedStatement.setString(3, device.getServerPort());
            preparedStatement.setString(4, device.getLastConnection());
            preparedStatement.setString(5, device.getEncryptionKey());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new ModelException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    public boolean EditDevice(Device device) throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("UPDATE " + tableDbName + " SET ip = ?, serverport = ?, lastconnection = ?, encryptionkey = ? WHERE name = ?");
            preparedStatement.setString(1, device.getIP());
            preparedStatement.setString(2, device.getServerPort());
            preparedStatement.setString(3, device.getLastConnection());
            preparedStatement.setString(4, device.getEncryptionKey());
            preparedStatement.setString(5, device.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new ModelException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    public Device GetDevice(String name) throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Device device = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + tableDbName + " WHERE name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                device = new Device(resultSet.getString("Name"), resultSet.getString("IP"), resultSet.getString("ServerPort"), resultSet.getString("LastConnection"), resultSet.getString("EncryptionKey"));
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, resultSet);
        }
        disconnect(connection, preparedStatement, resultSet);
        return device;
    }


    public boolean RemoveDevice(String name) throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("DELETE FROM " + tableDbName + " WHERE name = ?");
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, null);
            throw new ModelException(e);
            //return false;
        }
        disconnect(connection, preparedStatement, null);
        return true;
    }

    public String GetEncryptionKey(String name) throws ModelException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String encryptionKey = "";

        try {
            connection = connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + tableDbName + " WHERE name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                encryptionKey = resultSet.getString("EncryptionKey");
            }
        } catch (SQLException e) {
            disconnect(connection, preparedStatement, resultSet);
            throw new ModelException(e);
        }
        disconnect(connection, preparedStatement, resultSet);
        return encryptionKey;
    }
}