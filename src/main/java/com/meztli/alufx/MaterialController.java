package com.meztli.alufx;

import com.meztli.alufx.entities.Material;
import com.meztli.alufx.service.MaterialService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class MaterialController {

    @FXML
    private TextField nombre;

    private ObservableList<Material> data;

    @FXML
    private TableView materiales;

    @FXML
    public void initialize() {
        MaterialService service = new MaterialService();
        List<Material> materials = service.getAllMaterials();
        data = FXCollections.observableArrayList(materials);

        TableColumn<Material, Integer> idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Material, String> nombreCol = new TableColumn<>("nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        materiales.getColumns().setAll(idCol, nombreCol);
        materiales.setItems(data);
    }
    @FXML
    protected void onGuardarButtonClick() {
        Window owner = nombre.getScene().getWindow();

        MaterialService service = new MaterialService();
        Material material = new Material();
        material.setNombre(nombre.getText());
        service.saveMaterial(material);

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
