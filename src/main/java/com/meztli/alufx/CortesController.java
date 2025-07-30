package com.meztli.alufx;

import com.meztli.alufx.entities.Corte;
import com.meztli.alufx.entities.CorteRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CortesController {

    @FXML
    private TextField nombre;

    private ObservableList<Corte> data;

    @FXML
    private TableView cortes;

    @FXML
    private CheckBox ancho;

    @FXML
    private CheckBox alto;

    @FXML
    public void initialize() {
        CorteRepository repo = new CorteRepository();
        List<Corte> cortesList = repo.findAll();
        data = FXCollections.observableArrayList(cortesList);

        TableColumn<Corte, Integer> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Corte, String> nombreCol = new TableColumn<>("nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Corte, Boolean> anchoCol = new TableColumn<>("aplicaAncho");
        anchoCol.setCellValueFactory(new PropertyValueFactory<>("aplicaAncho"));

        TableColumn<Corte, Boolean> altoCol = new TableColumn<>("aplicaAlto");
        altoCol.setCellValueFactory(new PropertyValueFactory<>("aplicaAlto"));

        cortes.getColumns().setAll(idCol, nombreCol, anchoCol, altoCol);
        cortes.setItems(data);
    }
    @FXML
    protected void onGuardarButtonClick() {
        Window owner = nombre.getScene().getWindow();

        Corte corte = new Corte();
        corte.setNombre(nombre.getText());
        corte.setAplicaAncho(ancho.isSelected());
        corte.setAplicaAlto(alto.isSelected());

        CorteRepository repo = new CorteRepository();
        repo.save(corte);
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
