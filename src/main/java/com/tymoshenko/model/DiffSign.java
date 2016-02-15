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

    DiffSign(String sign) {
        this.sign = new SimpleStringProperty(sign);
    }

    public String getSign() {
        return sign.get();
    }

    public SimpleStringProperty signProperty() {
        return sign;
    }
}
