package com.meztli.alufx;

import com.meztli.alufx.entities.JdbcDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CortesController {

    @FXML
    private TextField nombre;

    private ObservableList<ObservableList> data;

    @FXML
    private TableView cortes;

    @FXML
    private CheckBox ancho;

    @FXML
    private CheckBox alto;

    @FXML
    public void initialize() throws SQLException {
        JdbcDao jdbcDao = new JdbcDao();
        ResultSet rs = jdbcDao.selectAll("cortes");
        data = FXCollections.observableArrayList();

        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/
        if (cortes.getColumns().size() > 1) {
            cortes.getColumns().remove(0, 4);
        }

        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                            param -> new SimpleStringProperty(param.getValue().get(j).toString()));

            cortes.getColumns().addAll(col);
        }


        /********************************
         * Data added to ObservableList *
         ********************************/
        while(rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                row.add(rs.getString(i));
            }
            data.add(row);
        }

        cortes.setItems(data);
    }
    @FXML
    protected void onGuardarButtonClick() throws SQLException {
        Window owner = nombre.getScene().getWindow();

        Map<String, Object> values = new HashMap<>();
        values.put("nombre", nombre.getText());
        values.put("aplicaAncho", ancho.isSelected() ? 1: 0);
        values.put("aplicaAlto", alto.isSelected() ? 1: 0);

        JdbcDao jdbcDao = new JdbcDao();
        jdbcDao.insert("cortes", values);
        showAlert(Alert.AlertType.INFORMATION, owner, "Creación correcta!",
                "Tipo de corte " + nombre.getText() + " Creado correctamente");
        initialize();
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}
