package ru.iostd.safebox.wizard;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

abstract public class WizardPage extends VBox {

    protected final Button priorButton = new Button("Back");
    protected final Button nextButton = new Button("Next");
    protected final Button cancelButton = new Button("Cancel");
    protected final Button finishButton = new Button("Finish");
    protected WizardData data;
    protected Stage owner;

    public WizardPage(String title) {
        super();
        getChildren().add(LabelBuilder.create().text(title).style("-fx-font-weight: bold; -fx-padding: 0 0 5 0;").build());
        setId(title);
        setSpacing(5);
        setStyle("-fx-padding:10; ");

        Region spring = new Region();
        VBox.setVgrow(spring, Priority.ALWAYS);
        getChildren().addAll(getContent(), spring, getButtons());

        priorButton.setOnAction((ActionEvent actionEvent) -> {
            priorPage();
        });
        nextButton.setOnAction((ActionEvent actionEvent) -> {
            nextPage();
        });
        cancelButton.setOnAction((ActionEvent actionEvent) -> {
            getWizard().cancel();
        });
        finishButton.setOnAction((ActionEvent actionEvent) -> {
            getWizard().finish();
        });

        finishButton.setDisable(true);

    }

    public void setData(WizardData data) {
        this.data = data;
    }

    public void setOwner(Stage owner) {
        this.owner = owner;
    }

    private HBox getButtons() {
        Region spring = new Region();
        HBox.setHgrow(spring, Priority.ALWAYS);
        HBox buttonBar = new HBox(5);
        cancelButton.setCancelButton(true);
        finishButton.setDefaultButton(true);
        buttonBar.getChildren().addAll(spring, priorButton, nextButton, cancelButton, finishButton);
        return buttonBar;
    }

    public abstract Node getContent();

    private boolean hasNextPage() {
        return getWizard().hasNextPage();
    }

    private boolean hasPriorPage() {
        return getWizard().hasPriorPage();
    }

    public void nextPage() {
        getWizard().nextPage();
    }

    private void priorPage() {
        getWizard().priorPage();
    }

    private void navTo(String id) {
        getWizard().navTo(id);
    }

    private Wizard getWizard() {
        return (Wizard) getParent();
    }

    public void manageButtons() {
        if (!hasPriorPage()) {
            priorButton.setDisable(true);
        }

        if (!hasNextPage()) {
            nextButton.setDisable(true);
        }
    }

    abstract public void call();

}
