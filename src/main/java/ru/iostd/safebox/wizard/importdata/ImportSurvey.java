package ru.iostd.safebox.wizard.importdata;

import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.iostd.safebox.wizard.Wizard;

public class ImportSurvey extends Wizard {

    private final Stage owner;

    public ImportSurvey(ImportSurveyData data, Stage owner) {
        super(data, owner, new SelectFilePage(), new ProcessPage(), new FinishPage());
        this.owner = owner;
        this.owner.setScene(new Scene(this, 350, 200));

    }

    public void start() {
        this.owner.showAndWait();
    }

    @Override
    public void finish() {
        owner.close();
    }

    @Override
    public void cancel() {
        owner.close();
    }
}
