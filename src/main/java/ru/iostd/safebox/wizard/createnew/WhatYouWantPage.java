package ru.iostd.safebox.wizard.createnew;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import ru.iostd.safebox.wizard.WizardPage;

public class WhatYouWantPage extends WizardPage {

    private RadioButton dbOnly;
    private RadioButton both;
    private ToggleGroup options;

    public WhatYouWantPage() {
        super("Creating new database");
    }

    @Override
    public Node getContent() {
        dbOnly = new RadioButton("Database with existing master key");
        both = new RadioButton("Database with new master key");
        options = new ToggleGroup();

        nextButton.setDisable(true);
        dbOnly.setToggleGroup(options);
        both.setToggleGroup(options);
        options.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observableValue, Toggle oldToggle, Toggle newToggle) -> {
            nextButton.setDisable(false);
        });

        VBox vbox = new VBox(5, new Label("What are you want to create?"), dbOnly, both);

        return vbox;
    }

    @Override
    public void nextPage() {
        super.nextPage();
        ((CreateSurveyData) data).existingKey = dbOnly.isSelected();
    }

    @Override
    public void call() {
        
    }
}
