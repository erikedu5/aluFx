package com.meztli.alufx.controller;

import com.meztli.alufx.HelloApplication;
import com.meztli.alufx.dto.Calculo;
import com.meztli.alufx.entities.*;
import com.meztli.alufx.repository.MaterialRepository;
import com.meztli.alufx.repository.MedidaRepository;
import com.meztli.alufx.repository.PedidoRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    TextField nombre;

    @FXML
    TableView calculo;

    @FXML
    ChoiceBox tipoProducto;

    private String materialElegido;

    private String tipoProductoElegido;

    private ObservableList<Calculo> currentCalculo;

    private final Runnable materialSavedListener = this::refreshMateriales;

    public void refreshMateriales() {
        try {
            MaterialRepository repository = new MaterialRepository();
            ObservableList<String> newData = FXCollections.observableArrayList();
            for (Material m : repository.findAll()) {
                newData.add(m.getId() + "-" + m.getNombre());
            }
            materiales.setItems(newData);
        } catch (Exception e) {
            System.out.println("Error al refrescar materiales: " + e.getMessage());
        }
    }

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

        MaterialController.addOnMaterialSavedListener(materialSavedListener);
        refreshMateriales();

        try {
            materiales.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                if (newValue != null) {
                    materialElegido = newValue.toString().split("-")[0];
                }
            });
            tipoProducto.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                if (newValue != null) {
                    tipoProductoElegido = newValue.toString();
                }
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

        final ObservableList<Calculo> data = FXCollections.observableArrayList();

        for (Medida med : medidas) {
            Calculo calculo1 = new Calculo();
            Corte corte = med.getCorte();
            boolean aplicaAncho = corte.isAplicaAncho();
            boolean aplicaAlto = corte.isAplicaAlto();
            calculo1.setTipoCorte(corte.getNombre());

            Double medidaVal = med.getMedida();
            calculo1.setAncho(aplicaAncho ? String.valueOf(Double.parseDouble(ancho.getText()) - medidaVal) : "-");
            calculo1.setAlto(aplicaAlto ? String.valueOf(Double.parseDouble(alto.getText()) - medidaVal) : "-");
            data.add(calculo1);
        }

        populateCalculoTable(data);
    }

    private void populateCalculoTable(ObservableList<Calculo> data) {
        currentCalculo = data;

        TableColumn tipoCorteCol = new TableColumn("Tipo de corte");
        tipoCorteCol.setCellValueFactory(new PropertyValueFactory("tipoCorte"));

        TableColumn altoCol = new TableColumn("Alto");
        altoCol.setCellValueFactory(new PropertyValueFactory("alto"));

        TableColumn anchoCol = new TableColumn("Ancho");
        anchoCol.setCellValueFactory(new PropertyValueFactory("ancho"));

        if (!calculo.getColumns().isEmpty()) {
            calculo.getColumns().clear();
        }
        calculo.getColumns().addAll(tipoCorteCol, altoCol, anchoCol);
        calculo.setItems(null);
        calculo.setItems(data);
        calculo.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    protected void onGuardarButtonClick() {
        Window owner = nombre.getScene().getWindow();

        if (Objects.isNull(materialElegido) || Objects.isNull(tipoProductoElegido)) {
            showAlert(Alert.AlertType.ERROR, owner, "Información faltante",
                    "Selecciona un material y un tipo de producto");
            return;
        }
        if (nombre.getText() == null || nombre.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, owner, "Información faltante",
                    "Escribe un nombre para guardar en el historial");
            return;
        }
        if (currentCalculo == null || currentCalculo.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Información faltante",
                    "Primero calcula antes de guardar");
            return;
        }

        Pedido pedido = new Pedido();
        pedido.setNombre(nombre.getText());
        pedido.setFecha(LocalDateTime.now());
        Material material = new Material();
        material.setId(Integer.parseInt(materialElegido));
        pedido.setMaterial(material);
        pedido.setTipoProducto(tipoProductoElegido);
        pedido.setAlto(Double.parseDouble(alto.getText()));
        pedido.setAncho(Double.parseDouble(ancho.getText()));

        List<PedidoDetalle> detalles = new ArrayList<>();
        for (Calculo c : currentCalculo) {
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setPedido(pedido);
            detalle.setTipoCorte(c.getTipoCorte());
            detalle.setAnchoResultado(c.getAncho());
            detalle.setAltoResultado(c.getAlto());
            detalles.add(detalle);
        }
        pedido.setDetalles(detalles);

        new PedidoRepository().save(pedido);

        showAlert(Alert.AlertType.INFORMATION, owner, "Guardado correcto!",
                "El pedido se guardó correctamente en el historial");
    }

    public void cargarPedido(Pedido pedido) {
        materiales.getSelectionModel().select(pedido.getMaterial().getId() + "-" + pedido.getMaterial().getNombre());
        tipoProducto.getSelectionModel().select(pedido.getTipoProducto());
        alto.setText(String.valueOf(pedido.getAlto()));
        ancho.setText(String.valueOf(pedido.getAncho()));
        nombre.setText(pedido.getNombre());

        ObservableList<Calculo> data = FXCollections.observableArrayList();
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            Calculo c = new Calculo();
            c.setTipoCorte(detalle.getTipoCorte());
            c.setAncho(detalle.getAnchoResultado());
            c.setAlto(detalle.getAltoResultado());
            data.add(c);
        }
        populateCalculoTable(data);
    }

    @FXML
    protected void onVerHistorialButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("historial-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 500);
            HistorialController controller = fxmlLoader.getController();
            controller.setHelloController(this);
            Stage stage = new Stage();
            stage.setTitle("Historial");
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/logo.png")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    @FXML
    protected void onMaterialesButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(HelloApplication.class.getResource("materiales-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 500);
            Stage stage = new Stage();
            stage.setTitle("Materiales");
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/logo.png")));
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
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/logo.png")));
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
            stage.getIcons().add(new Image(HelloApplication.class.getResourceAsStream("/logo.png")));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
