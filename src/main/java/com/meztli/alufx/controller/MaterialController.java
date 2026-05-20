package com.meztli.alufx.controller;

import com.meztli.alufx.entities.Material;
import com.meztli.alufx.repository.MaterialRepository;
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

    private static final java.util.List<java.lang.ref.WeakReference<Runnable>> onMaterialSavedListeners = new java.util.ArrayList<>();

    public static synchronized void addOnMaterialSavedListener(Runnable listener) {
        onMaterialSavedListeners.add(new java.lang.ref.WeakReference<>(listener));
    }

    public static synchronized void notifyMaterialSaved() {
        java.util.Iterator<java.lang.ref.WeakReference<Runnable>> iterator = onMaterialSavedListeners.iterator();
        while (iterator.hasNext()) {
            Runnable listener = iterator.next().get();
            if (listener == null) {
                iterator.remove();
            } else {
                try {
                    listener.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void initialize() {
        MaterialRepository repository = new MaterialRepository();
        List<Material> materials = repository.findAll();
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

        MaterialRepository repository = new MaterialRepository();
        Material material = new Material();
        material.setNombre(nombre.getText());
        repository.save(material);
        notifyMaterialSaved();

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
