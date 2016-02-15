package com.tymoshenko.view;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.ExportFormat;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author Yakiv Tymoshenko
 * @since 15.02.2016
 */
public class MenuBarController {

    private MainApp mainApp;

    @FXML
    private void handleExport() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.export(file, ExportFormat.XML);
        }
    }

    @FXML
    private void handleImport() {

    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
