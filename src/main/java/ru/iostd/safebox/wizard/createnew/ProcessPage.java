package ru.iostd.safebox.wizard.createnew;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.crypto.NoSuchPaddingException;
import ru.iostd.safebox.MainApp;
import ru.iostd.safebox.data.SafeDataManager;
import ru.iostd.safebox.exceptions.InvalidFileFormatException;
import ru.iostd.safebox.wizard.WizardPage;

public class ProcessPage extends WizardPage {

    private ProgressBar bar;
    private Label label;
    private Task<Boolean> worker;

    public ProcessPage() {
        super("Creating");

    }

    @Override
    public Parent getContent() {
        VBox vbox = new VBox();
        bar = new ProgressBar();
        label = new Label("Creating...");
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, bar);
        HBox.setHgrow(bar, Priority.ALWAYS);

        worker = createWorker();

        bar.progressProperty().unbind();
        bar.progressProperty().bind(worker.progressProperty());
        worker.messageProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            label.setText(newValue);
        });
        worker.setOnSucceeded((WorkerStateEvent event) -> {
            cancelButton.setDisable(true);
            if (worker.getValue() == true) {
                
                nextButton.setDisable(false);
            }
        });

        return vbox;
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() {
                try {
                    //updateProgress(0, 10);
                    File keyFile = new File(((CreateSurveyData) data).keyPath);
                    File dbFile = new File(((CreateSurveyData) data).dbPath);

                    SafeDataManager.getInstance().setFiles(dbFile, keyFile);

                    if (!((CreateSurveyData) data).existingKey) {

                        updateMessage("Generating master key file");
                        SafeDataManager.getInstance().newMasterKey();

                    }
                    updateMessage("Creating database file");
                    SafeDataManager.getInstance().newDatabase();
                    updateMessage("Done");
                    updateProgress(10, 10);
                } catch (IOException | InvalidFileFormatException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
                    updateMessage("Failed");
                    updateProgress(0, 10);
                    return false;
                }
                return true;
            }
        };
    }

    @Override
    public void call() {
        priorButton.setDisable(true);
        nextButton.setDisable(true);
        new Thread(worker).start();
    }
}
