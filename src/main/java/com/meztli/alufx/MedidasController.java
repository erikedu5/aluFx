package com.meztli.alufx;

import com.meztli.alufx.entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.*;

public class MedidasController {

    @FXML
    private ChoiceBox materiales;

    private ObservableList<String> data;


    private String materialElegido;
    private String tipoProductoElegido;

    private List<MedidasDinamic> medidasDinamics;

    @FXML
    VBox dinamic;

    @FXML
    ChoiceBox tipoProducto;

    @FXML
    public void initialize() {
        dinamic.autosize();

        tipoProducto.getItems().add("Puerta");
        tipoProducto.getItems().add("Ventana");

        medidasDinamics = new ArrayList<>();
        MaterialRepository materialRepo = new MaterialRepository();
        CorteRepository corteRepo = new CorteRepository();
        try {
            data = FXCollections.observableArrayList();
            for (Material m : materialRepo.findAll()) {
                data.add(m.getId() + "-" + m.getNombre());
            }
            materiales.setItems(null);
            materiales.setItems(data);

            for (Corte corte : corteRepo.findAll()) {
                Label space0 = new Label(" ");
                dinamic.getChildren().add(space0);

                String respuesta = corte.getId() + "-" + corte.getNombre();
                Label label = new Label(respuesta);
                dinamic.getChildren().add(label);

                Label space = new Label(" ");
                dinamic.getChildren().add(space);

                CheckBox applica = new CheckBox("Aplica");
                dinamic.getChildren().add(applica);

                TextField textField = new TextField();
                textField.setId(respuesta);
                dinamic.getChildren().add(textField);

                MedidasDinamic md = new MedidasDinamic(corte.getId(), textField, applica);
                medidasDinamics.add(md);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        tipoProducto.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            tipoProductoElegido = newValue.toString();
            if (tipoProductoElegido != null && materialElegido != null) {
                this.clearTextFields();
                MedidaRepository medidaRepo = new MedidaRepository();
                List<Medida> list = medidaRepo.findByMaterialAndTipoProducto(Integer.parseInt(materialElegido), tipoProductoElegido);
                for (Medida med : list) {
                    medidasDinamics.forEach(md -> {
                        if (med.getCorte().getId() == md.getIdCorte()) {
                            md.getCheckBox().setSelected(true);
                            md.getTextField().setText("" + med.getMedida().intValue());
                        }
                    });
                }
            }
        });

        materiales.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            materialElegido = newValue.toString().split("-")[0];
            if (tipoProductoElegido != null && materialElegido != null) {
                this.clearTextFields();
                MedidaRepository medidaRepo = new MedidaRepository();
                List<Medida> list = medidaRepo.findByMaterialAndTipoProducto(Integer.parseInt(materialElegido), tipoProductoElegido);
                for (Medida med : list) {
                    medidasDinamics.forEach(md -> {
                        if (med.getCorte().getId() == md.getIdCorte()) {
                            md.getCheckBox().setSelected(true);
                            md.getTextField().setText("" + med.getMedida().intValue());
                        }
                    });
                }
                System.out.println(materialElegido);
            }
        });
    }

    @FXML
    protected void onGuardarButtonClick() {
        Window owner = materiales.getScene().getWindow();
        if (Objects.isNull(materialElegido)) {
            showAlert(Alert.AlertType.ERROR, owner, "Información faltante",
                    "No has Elegido Material Faltante");
            return;
        }
        MedidaRepository medidaRepo = new MedidaRepository();
        medidasDinamics.forEach(medidasDinamic -> {
            if (medidasDinamic.getCheckBox().isSelected()) {
                Medida medida = new Medida();
                Corte corte = new Corte();
                corte.setId(medidasDinamic.getIdCorte());
                medida.setCorte(corte);
                Material material = new Material();
                material.setId(Integer.parseInt(materialElegido));
                medida.setMaterial(material);
                medida.setTipoProducto(tipoProductoElegido);
                medida.setMedida(medidasDinamic.getTextField().getText().equals("") ? 0.0 : Double.valueOf(medidasDinamic.getTextField().getText()));
                try {
                    medidaRepo.save(medida);
                } catch (RuntimeException e) {
                    medidaRepo.updateMedida(medida.getMedida(), medidasDinamic.getIdCorte(), Integer.parseInt(materialElegido));
                }
            }
        });

        showAlert(Alert.AlertType.INFORMATION, owner, "Creación correcta!",
                "Medida creada correctamente");
        this.clearTextFields();
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();

    }

    public void clearTextFields() {
        medidasDinamics.forEach(medidasDinamic -> {
            medidasDinamic.getTextField().setText("");
            medidasDinamic.getCheckBox().setSelected(false);
        });
    }

}
