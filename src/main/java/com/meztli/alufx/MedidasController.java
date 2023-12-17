package com.meztli.alufx;

import com.meztli.alufx.entities.JdbcDao;
import com.meztli.alufx.entities.MedidasDinamic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MedidasController {

    @FXML
    private ChoiceBox materiales;

    private ObservableList<String> data;

    private ObservableList<String> cortesData;

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
        JdbcDao jdbcDao = new JdbcDao();
        try {
            ResultSet rs = jdbcDao.selectAll("materiales");
            data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(rs.getInt("id") + "-" + rs.getString("nombre"));
            }
            materiales.setItems(null);
            materiales.setItems(data);


            ResultSet rs2 = jdbcDao.selectAll("cortes");
            cortesData = FXCollections.observableArrayList();
            while (rs2.next()) {

                Label space0 = new Label(" ");
                dinamic.getChildren().add(space0);

                // Para cada respuesta en la base de datos, crea un TextField
                String respuesta = rs2.getInt("id") + "-" + rs2.getString("nombre");
                Label label = new Label(respuesta);
                dinamic.getChildren().add(label);

                Label space = new Label(" ");
                dinamic.getChildren().add(space);

                CheckBox applica = new CheckBox("Aplica");
                dinamic.getChildren().add(applica);

                TextField textField = new TextField();
                textField.setId(respuesta);
                dinamic.getChildren().add(textField);

                MedidasDinamic md = new MedidasDinamic(rs2.getInt("id"), textField, applica);
                medidasDinamics.add(md);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        tipoProducto.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            tipoProductoElegido = newValue.toString();
            if (tipoProductoElegido != null && materialElegido != null) {
                this.clearTextFields();
                try {
                    ResultSet rs3 = jdbcDao.selectAllByMaterialIdAndTipoProducto("medidas", materialElegido, tipoProductoElegido);
                    while (rs3.next()) {
                        medidasDinamics.forEach(medidasDinamic -> {
                            try {
                                if (rs3.getInt("id_corte") == medidasDinamic.getIdCorte()) {
                                    medidasDinamic.getCheckBox().setSelected(true);
                                    medidasDinamic.getTextField().setText("" + rs3.getInt("medida"));
                                }
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        materiales.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            materialElegido = newValue.toString().split("-")[0];
            if (tipoProductoElegido != null && materialElegido != null) {
                this.clearTextFields();
                try {
                    ResultSet rs3 = jdbcDao.selectAllByMaterialIdAndTipoProducto("medidas", materialElegido, tipoProductoElegido);
                    while (rs3.next()) {
                        medidasDinamics.forEach(medidasDinamic -> {
                            try {
                                if (rs3.getInt("id_corte") == medidasDinamic.getIdCorte()) {
                                    medidasDinamic.getCheckBox().setSelected(true);
                                    medidasDinamic.getTextField().setText("" + rs3.getInt("medida"));
                                }
                            } catch (SQLException e) {
                                System.out.println(e.getMessage());
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
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
        JdbcDao jdbcDao = new JdbcDao();
        Map<String, Object> medidas = new HashMap<>();
        medidasDinamics.forEach(medidasDinamic -> {
            try {
                if (medidasDinamic.getCheckBox().isSelected()) {
                    medidas.put("id_corte", medidasDinamic.getIdCorte());
                    medidas.put("id_material", materialElegido);
                    medidas.put("tipoProducto", tipoProductoElegido);
                    medidas.put("medida", medidasDinamic.getTextField().getText().equals("") ? 0.0 : medidasDinamic.getTextField().getText());
                    jdbcDao.insert("medidas", medidas);
                }
            } catch (Exception e) {
                if (e.getMessage().contains("Duplicate")) {
                    Double medida = medidasDinamic.getTextField().getText().equals("") ? 0.0 : Double.valueOf(medidasDinamic.getTextField().getText());
                    jdbcDao.updateMedidas(medida, medidasDinamic.getIdCorte(), Integer.parseInt(materialElegido));
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
