package ru.iostd.safebox.wizard.createnew;

import java.io.File;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.iostd.safebox.wizard.WizardPage;

public class SelectKeyPage extends WizardPage {

    private TextField keyFileField;
    private Label errorLabel;

    public SelectKeyPage() {
        super("Select master key file");

    }

    @Override
    public Parent getContent() {
        Button openButton = new Button("...");
        keyFileField = new TextField();
        errorLabel = new Label();
        errorLabel.setStyle("-fx-color: red;");

        HBox box = new HBox(keyFileField, openButton);

        FileChooser keyFileChooser = new FileChooser();

        keyFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        keyFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SafeBox Master Key File", "*.sbmk"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        openButton.setOnAction((ActionEvent e) -> {
            File file;
            if (((CreateSurveyData) data).existingKey) {
                file = keyFileChooser.showOpenDialog(owner);
            } else {
                file = keyFileChooser.showSaveDialog(owner);
            }
            if (file != null) {
                keyFileField.setText(file.getPath());
            }
        });

        keyFileField.textProperty().addListener((ObservableValue<? extends String> observableValue, String oldValue, String newValue) -> {
            nextButton.setDisable(newValue.isEmpty());
        });

        return new VBox(box, errorLabel);
    }

    @Override
    public void nextPage() {
        super.nextPage();
        ((CreateSurveyData) data).keyPath = keyFileField.getText();
    }

    @Override
    public void call() {
        keyFileField.textProperty().unbind();
        if (((CreateSurveyData) data).existingKey) {
            keyFileField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                File file = new File(newValue);
                if (!file.exists() || !file.canRead()) {
                    errorLabel.setText("File does't exists");
                    nextButton.setDisable(true);
                } else {
                    errorLabel.setText("");
                    nextButton.setDisable(false);
                }
            });
        }
    }
}
