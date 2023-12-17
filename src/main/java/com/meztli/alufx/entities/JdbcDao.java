package com.meztli.alufx.entities;

import java.sql.*;
import java.util.Map;

public class JdbcDao {

    // Replace below database url, username and password with your actual database credentials
    private static final String DATABASE_URL = "jdbc:mariadb://localhost:3306/aluhelper?useSSL=false";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "root";
    private String query = "";


    public void insert(String table, Map<String, Object> params) {

        String query = "INSERT INTO ".concat(table).concat("(columns) VALUES (values)");
        final String[] columns = {"("};
        final String[] values = {"("};
        params.forEach((param, value) -> {
            columns[0] = columns[0].concat(param).concat(", ");
            values[0] = values[0].concat("?").concat(", ");
        });
        columns[0] = columns[0].concat(")");
        values[0] = values[0].concat(")");

        query = query.replace("(columns)", columns[0]);
        query = query.replace("(values)", values[0]);

        query = query.replace(", )", ")");
        query = query.replace(", )", ")");

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int index = 1;
            for (Map.Entry<String, Object> param : params.entrySet()) {
                preparedStatement.setString(index, param.getValue().toString());
                index++;
            }

            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMedidas(Double medida, int id_corte, int id_materia) {
       String queryUpdate = "UPDATE medidas SET medida=? WHERE id_corte=? AND id_material= ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate)) {

            preparedStatement.setDouble(1, medida);
            preparedStatement.setDouble(2, id_corte);
            preparedStatement.setDouble(3, id_materia);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public ResultSet selectAll(String table) {
        String query = "SELECT * FROM ".concat(table);
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    public ResultSet selectAllByMaterialIdAndTipoProducto(String table, String materialElegido, String tipoProductoElegido) {
        String query = "SELECT * FROM ".concat(table) +  " WHERE id_material = " + materialElegido + " and tipoProducto = '" + tipoProductoElegido + "'";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }

    public ResultSet selectById(String table, int id) {
        String query = "SELECT * FROM ".concat(table) +  " WHERE id = " + id;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }


    public ResultSet selectBy(String table, String column,  int id) {
        String query = "SELECT * FROM ".concat(table) +  " WHERE " + column + " = " + id;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD)) {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            printSQLException(e);
        }
        return null;
    }
}