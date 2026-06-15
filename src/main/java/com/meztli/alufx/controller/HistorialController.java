package com.meztli.alufx.controller;

import com.meztli.alufx.entities.Pedido;
import com.meztli.alufx.entities.PedidoDetalle;
import com.meztli.alufx.repository.PedidoRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HistorialController {

    @FXML
    TableView<Pedido> historial;

    private HelloController helloController;

    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
    }

    public void initialize() {
        TableColumn<Pedido, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Pedido, String> fechaCol = new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        TableColumn<Pedido, String> materialCol = new TableColumn<>("Material");
        materialCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMaterial().getNombre()));

        TableColumn<Pedido, String> productoCol = new TableColumn<>("Producto");
        productoCol.setCellValueFactory(new PropertyValueFactory<>("tipoProducto"));

        historial.getColumns().addAll(nombreCol, fechaCol, materialCol, productoCol);
        historial.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        refreshHistorial();
    }

    private void refreshHistorial() {
        PedidoRepository pedidoRepository = new PedidoRepository();
        historial.setItems(FXCollections.observableArrayList(pedidoRepository.findAll()));
    }

    @FXML
    protected void onCargarButtonClick() {
        Window owner = historial.getScene().getWindow();
        var seleccionados = historial.getSelectionModel().getSelectedItems();

        if (seleccionados.size() != 1) {
            showAlert(Alert.AlertType.ERROR, owner, "Selección inválida",
                    "Selecciona un solo registro del historial para cargarlo");
            return;
        }

        Pedido pedido = new PedidoRepository().findById(seleccionados.get(0).getId());
        helloController.cargarPedido(pedido);
        ((Stage) owner).close();
    }

    @FXML
    protected void onDescargarDocxButtonClick() {
        Window owner = historial.getScene().getWindow();
        var seleccionados = historial.getSelectionModel().getSelectedItems();

        if (seleccionados.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Selección inválida",
                    "Selecciona al menos un registro del historial");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar como");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento Word", "*.docx"));
        fileChooser.setInitialFileName("historial.docx");
        File file = fileChooser.showSaveDialog(owner);
        if (file == null) {
            return;
        }

        try (XWPFDocument document = new XWPFDocument()) {
            for (Pedido pedido : seleccionados) {
                XWPFParagraph titulo = document.createParagraph();
                XWPFRun tituloRun = titulo.createRun();
                tituloRun.setBold(true);
                tituloRun.setFontSize(14);
                tituloRun.setText(pedido.getNombre());

                XWPFParagraph info = document.createParagraph();
                XWPFRun infoRun = info.createRun();
                infoRun.setText("Fecha: " + pedido.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                infoRun.addBreak();
                infoRun.setText("Material: " + pedido.getMaterial().getNombre());
                infoRun.addBreak();
                infoRun.setText("Producto: " + pedido.getTipoProducto());
                infoRun.addBreak();
                infoRun.setText("Alto: " + pedido.getAlto() + "   Ancho: " + pedido.getAncho());

                XWPFTable table = document.createTable(pedido.getDetalles().size() + 1, 3);
                XWPFTableRow header = table.getRow(0);
                header.getCell(0).setText("Tipo de corte");
                header.getCell(1).setText("Alto");
                header.getCell(2).setText("Ancho");
                for (int i = 0; i < pedido.getDetalles().size(); i++) {
                    PedidoDetalle detalle = pedido.getDetalles().get(i);
                    XWPFTableRow row = table.getRow(i + 1);
                    row.getCell(0).setText(detalle.getTipoCorte());
                    row.getCell(1).setText(detalle.getAltoResultado());
                    row.getCell(2).setText(detalle.getAnchoResultado());
                }

                document.createParagraph();
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                document.write(out);
            }

            showAlert(Alert.AlertType.INFORMATION, owner, "Descarga completa",
                    "El archivo se generó correctamente en " + file.getAbsolutePath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, owner, "Error",
                    "No se pudo generar el documento: " + e.getMessage());
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
}
