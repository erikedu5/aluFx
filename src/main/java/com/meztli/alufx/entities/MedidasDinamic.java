package com.meztli.alufx.entities;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class MedidasDinamic {

    private int idCorte;

    private TextField textField;

    private CheckBox checkBox;

    public MedidasDinamic(int idCorte, TextField textField, CheckBox checkBox) {
        this.idCorte = idCorte;
        this.textField = textField;
        this.checkBox = checkBox;
    }

    public int getIdCorte() {
        return idCorte;
    }

    public void setIdCorte(int idCorte) {
        this.idCorte = idCorte;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
