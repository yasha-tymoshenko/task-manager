package com.tymoshenko.view;

import com.tymoshenko.MainApp;
import com.tymoshenko.model.ExportFormat;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Yakiv Tymoshenko
 * @since 15.02.2016
 */
public class MenuBarController {

    private static final Logger LOG = LoggerFactory.getLogger(MenuBarController.class);

    private MainApp mainApp;

    @FXML
    private void handleExport() {
        // Show Save File Dialog
        File file = xmlFileChooser().showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.doExport(file, ExportFormat.XML);
        }
    }

    @FXML
    private void handleExportToExcel() {
        // Show Save File Dialog
        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Microsoft Excel 97/2000/XP (.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xls")) {
                file = new File(file.getPath() + ".xls");
            }
            mainApp.doExportToExcel(file, ExportFormat.Excel);
        }
    }

    @FXML
    private void handleImport() {
        // Open file dialog
        File file = xmlFileChooser().showOpenDialog(mainApp.getPrimaryStage());
        mainApp.doImport(file);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private FileChooser xmlFileChooser() {
        FileChooser fileChooser = new FileChooser();
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
