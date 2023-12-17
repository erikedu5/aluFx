package com.meztli.alufx;

import com.meztli.alufx.entities.JdbcDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import javafx.util.Callback;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MaterialController {

    @FXML
    private TextField nombre;

    private ObservableList<ObservableList> data;

    @FXML
    private TableView materiales;

    @FXML
    public void initialize() throws SQLException {
        JdbcDao jdbcDao = new JdbcDao();
        ResultSet rs = jdbcDao.selectAll("materiales");
        data = FXCollections.observableArrayList();

        /**********************************
         * TABLE COLUMN ADDED DYNAMICALLY *
         **********************************/

        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                            param -> new SimpleStringProperty(param.getValue().get(j).toString()));

            if (materiales.getColumns().size() > 1) {
                materiales.getColumns().remove(0, 1);
            }
            materiales.getColumns().addAll(col);
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

        System.out.println(data);
        materiales.setItems(data);
    }
    @FXML
    protected void onGuardarButtonClick() throws SQLException {
        Window owner = nombre.getScene().getWindow();

        Map<String, Object> values = new HashMap<>();
        values.put("nombre", nombre.getText());

        JdbcDao jdbcDao = new JdbcDao();
        jdbcDao.insert("materiales", values);
        showAlert(Alert.AlertType.INFORMATION, owner, "Creación correcta!",
                "Material " + nombre.getText() + " Creado correctamente");
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
