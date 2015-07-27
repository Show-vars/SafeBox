package ru.iostd.safebox.wizard.importdata;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.iostd.safebox.wizard.WizardPage;

public class FinishPage extends WizardPage {

    public FinishPage() {
        super("Done");

    }

    @Override
    public Parent getContent() {
        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(new Label("Import succesful completed"));

        return vbox;
    }

    @Override
    public void call() {
        priorButton.setDisable(true);
        finishButton.setDisable(false);
        cancelButton.setDisable(true);
        ((ImportSurveyData) data).finished = true;
    }

}
