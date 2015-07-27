package ru.iostd.safebox.wizard.importdata;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import ru.iostd.safebox.wizard.createnew.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javax.crypto.NoSuchPaddingException;
import ru.iostd.safebox.data.SafeDataManager;
import ru.iostd.safebox.data.SafeRecord;
import ru.iostd.safebox.exceptions.InvalidFileFormatException;
import ru.iostd.safebox.wizard.WizardPage;
import ru.iostd.safebox.wizard.importdata.formats.JsonFormat;
import ru.iostd.safebox.wizard.importdata.formats.JsonPassword;

public class ProcessPage extends WizardPage {

    private ProgressBar bar;
    private Label label;
    private Task<Boolean> worker;

    public ProcessPage() {
        super("Importing");

    }

    @Override
    public Parent getContent() {
        VBox vbox = new VBox();
        bar = new ProgressBar();
        label = new Label("Importing...");
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
                    File file = new File(((ImportSurveyData) data).path);

                    Gson gson = new Gson();

                    JsonFormat format = gson.fromJson(new FileReader(file), JsonFormat.class);

                    format.getPasswords().stream().forEach((p) -> {
                        ((ImportSurveyData) data).observableList.add(
                                new SafeRecord(p.getTitle(), p.getUserName(), p.getPassword(), "", p.getNotes(), LocalDateTime.now())
                        );
                    });

                    updateMessage("Done");
                    updateProgress(10, 10);
                } catch (FileNotFoundException | JsonSyntaxException | JsonIOException ex) {
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
