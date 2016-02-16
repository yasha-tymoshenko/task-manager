package com.tymoshenko.model;

import javafx.beans.property.SimpleStringProperty;

/**
 * @author Yakiv Tymoshenko
 * @since 16.02.2016
 */
public enum  DiffSign {
    NO_CHANGES("="),
    CHANGED("~"),
    ADDED("+"),
    REMOVED("-");

    private SimpleStringProperty sign;
    private SimpleStringProperty _name;

    DiffSign(String sign) {
        this.sign = new SimpleStringProperty(sign);
        this._name = new SimpleStringProperty(this.name());
    }

    public String getSign() {
        return sign.get();
    }

    public SimpleStringProperty signProperty() {
        return sign;
    }

    public SimpleStringProperty nameProperty() {
        return _name;
    }
}
