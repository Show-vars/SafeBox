package ru.iostd.safebox.wizard.importdata;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import ru.iostd.safebox.wizard.WizardPage;

public class SelectFilePage extends WizardPage {

    private TextField fileField;

    public SelectFilePage() {
        super("Select file");

    }

    @Override
    public Parent getContent() {
        Button openButton = new Button("...");
        fileField = new TextField();
        HBox box = new HBox();

        box.getChildren().addAll(fileField, openButton);
        
        FileChooser fileChooser = new FileChooser();

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        openButton.setOnAction((ActionEvent e) -> {
            File file = fileChooser.showOpenDialog(owner);
            if (file != null) {
                fileField.setText(file.getPath());
            }
        });

        fileField.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            nextButton.setDisable(newValue.isEmpty());
        });

        return box;
    }

    @Override
    public void nextPage() {
        super.nextPage();
        ((ImportSurveyData) data).path = fileField.getText();
    }

    @Override
    public void call() {
        
    }
}
