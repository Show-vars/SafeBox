package ru.iostd.safebox.wizard.createnew;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ru.iostd.safebox.wizard.WizardPage;

public class SelectDbPage extends WizardPage {

    private TextField dbFileField;

    public SelectDbPage() {
        super("Select database file");

    }

    @Override
    public Parent getContent() {
        Button openButton = new Button("...");
        dbFileField = new TextField();
        HBox box = new HBox();

        box.getChildren().addAll(dbFileField, openButton);
        

        FileChooser dbFileChooser = new FileChooser();

        dbFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dbFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SafeBox Database File", "*.sbdb"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        openButton.setOnAction((ActionEvent e) -> {
            File file = dbFileChooser.showSaveDialog(owner);
            if (file != null) {
                dbFileField.setText(file.getPath());
            }
        });

        dbFileField.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            nextButton.setDisable(newValue.isEmpty());
        });

        return box;
    }

    @Override
    public void nextPage() {
        super.nextPage();
        ((CreateSurveyData) data).dbPath = dbFileField.getText();
    }

    @Override
    public void call() {
    }
}
