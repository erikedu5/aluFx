package com.meztli.alufx.controller;

import com.meztli.alufx.HelloApplication;
import com.meztli.alufx.dto.Calculo;
import com.meztli.alufx.entities.*;
import com.meztli.alufx.repository.MaterialRepository;
import com.meztli.alufx.repository.MedidaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class HelloController {

    @FXML
    private ChoiceBox materiales;

    @FXML
    TextField ancho;

    @FXML
    TextField alto;

    @FXML
    TableView calculo;

    @FXML
    ChoiceBox tipoProducto;

    private ObservableList<String> data;
    private ObservableList<ObservableList> calculoData;
    private String materialElegido;

    private String tipoProductoElegido;

    public void initialize() {

        tipoProducto.getItems().add("Puerta");
        tipoProducto.getItems().add("Ventana");

        Pattern validEditingState = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");
        UnaryOperator<TextFormatter.Change> filter = c -> {
            String text = c.getControlNewText();
            if (validEditingState.matcher(text).matches()) {
                return c ;
            } else {
                return null ;
            }
        };

        StringConverter<Double> converter = new StringConverter<Double>() {
            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s)) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }

            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };
        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, filter);
        TextFormatter<Double> textFormatter1 = new TextFormatter<>(converter, 0.0, filter);
        ancho.setTextFormatter(textFormatter);
        alto.setTextFormatter(textFormatter1);

        try {
            MaterialRepository repository = new MaterialRepository();
            data = FXCollections.observableArrayList();
            for (Material m : repository.findAll()) {
                data.add(m.getId() + "-" + m.getNombre());
            }
            materiales.setItems(null);
            materiales.setItems(data);

            materiales.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                materialElegido = newValue.toString().split("-")[0];
            });
            tipoProducto.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                tipoProductoElegido = newValue.toString();
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void onCalculateButtonClick() {
        MedidaRepository medidaRepository = new MedidaRepository();
        int matId = Integer.parseInt(materialElegido);
        var medidas = medidaRepository.findByMaterialAndTipoProducto(matId, tipoProductoElegido);

        calculo.setItems(null);
        calculoData = FXCollections.observableArrayList();

        TableColumn tipoCorteCol = new TableColumn("Tipo de corte");
        tipoCorteCol.setCellValueFactory(new PropertyValueFactory("tipoCorte"));

        TableColumn altoCol = new TableColumn("Alto");
        altoCol.setCellValueFactory(new PropertyValueFactory("alto"));

        TableColumn anchoCol = new TableColumn("Ancho");
        anchoCol.setCellValueFactory(new PropertyValueFactory("ancho"));

        final ObservableList<Calculo> data = FXCollections.observableArrayList();

        for (Medida med : medidas) {
            Calculo calculo1 = new Calculo();
            Corte corte = med.getCorte();
            boolean aplicaAncho = corte.isAplicaAncho();
            boolean aplicaAlto = corte.isAplicaAlto();
            calculo1.setTipoCorte(corte.getNombre());

            Double medidaVal = med.getMedida();
            Double anchoMed = Double.parseDouble(ancho.getText());
            Double altoMed = Double.parseDouble(alto.getText());
            if (aplicaAncho) {
                anchoMed = anchoMed - medidaVal;
            }
            if (aplicaAlto) {
                altoMed = altoMed - medidaVal;
            }
            calculo1.setAncho(anchoMed);
            calculo1.setAlto(altoMed);
            data.add(calculo1);
        }

        if (!calculo.getColumns().isEmpty()) {
            calculo.getColumns().clear();
        }
        calculo.getColumns().addAll(tipoCorteCol, altoCol, anchoCol);
        calculo.setItems(null);
        calculo.setItems(data);
        calculo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    protected void onMaterialesButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("materiales-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 500);
            Stage stage = new Stage();
            stage.setTitle("Materiales");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void onCorteButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("corte-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 500);
            Stage stage = new Stage();
            stage.setTitle("Tipos de corte");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    protected void onMedidasButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("medidas-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 600);
            Stage stage = new Stage();
            stage.setTitle("Medidas por corte y tipo de material");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}