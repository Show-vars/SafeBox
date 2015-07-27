package ru.iostd.safebox.wizard.importdata;

import javafx.collections.ObservableList;
import ru.iostd.safebox.data.SafeRecord;
import ru.iostd.safebox.wizard.WizardData;

public class ImportSurveyData implements WizardData {

    public String path;
    public ObservableList<SafeRecord> observableList;
    boolean finished = false;
}
