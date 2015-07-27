package ru.iostd.safebox;

import ru.iostd.safebox.data.SafeDataManager;
import ru.iostd.safebox.exceptions.InvalidFileFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.crypto.NoSuchPaddingException;
import ru.iostd.safebox.config.ConfigManager;
import ru.iostd.safebox.controllers.MainController;
import ru.iostd.safebox.exceptions.BadMasterKeyException;
import ru.iostd.safebox.wizard.createnew.CreateSurvey;
import ru.iostd.safebox.wizard.createnew.CreateSurveyData;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            ConfigManager.getInstance().load();
        } catch (IOException ex) {
            showException(null, ex);
            MainApp.exit();
        }
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/OpenScene.fxml"));
        } catch (IOException ex) {
            showException(null, ex);
            MainApp.exit();
        }

        Scene scene = new Scene(root);
        stage.setTitle("SafeBox");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/media/SafeBoxLogo.png")));
        stage.show();

        Button dbSelectButton = ((Button) scene.lookup("#dbSelectButton"));
        Button keySelectButton = ((Button) scene.lookup("#keySelectButton"));

        ComboBox dbField = ((ComboBox) scene.lookup("#dbField"));
        ComboBox keyField = ((ComboBox) scene.lookup("#keyField"));

        List<String> dbLastFiles = ConfigManager.getInstance().getConfig().getLastDbFiles();
        List<String> keyLastFiles = ConfigManager.getInstance().getConfig().getLastKeyFiles();

        dbField.setItems(FXCollections.observableList(dbLastFiles));
        keyField.setItems(FXCollections.observableList(keyLastFiles));

        if (!dbLastFiles.isEmpty()) {
            dbField.setValue(dbLastFiles.get(dbLastFiles.size()-1));
        }

        if (!keyLastFiles.isEmpty()) {
            keyField.setValue(keyLastFiles.get(keyLastFiles.size()-1));
        }

        ((Hyperlink) scene.lookup("#newButton")).setOnAction((ActionEvent e) -> {
            CreateSurveyData data = new CreateSurveyData();
            Stage surveyStage = new Stage();
            surveyStage.initOwner(stage);
            surveyStage.setResizable(false);
            surveyStage.setTitle("Creating");
            surveyStage.getIcons().add(new Image(getClass().getResourceAsStream("/media/SafeBoxLogo.png")));
            surveyStage.initModality(Modality.APPLICATION_MODAL);
            CreateSurvey survey = new CreateSurvey(data, surveyStage);
            survey.start();
            
            if(!data.finished) return;
            
            dbField.setValue(data.dbPath);
            keyField.setValue(data.keyPath);
            
            File dbFile = new File(data.dbPath == null ? "" : data.dbPath);
            File keyFile = new File(data.keyPath == null ? "" : data.keyPath);

            if (!dbFile.isFile() || !dbFile.exists() || !dbFile.canRead() || !dbFile.canWrite()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Unable to open database file.");
                alert.initOwner(stage);
                alert.showAndWait();
                return;
            }
            if (!keyFile.isFile() || !keyFile.exists() || !keyFile.canRead()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Unable to open master key file.");
                alert.initOwner(stage);
                alert.showAndWait();
                return;
            }

            SafeDataManager.getInstance().setFiles(dbFile, keyFile);

            ConfigManager.getInstance().getConfig().addLastDbFile(dbFile.getPath());
            ConfigManager.getInstance().getConfig().addLastKeyFile(keyFile.getPath());

            stage.hide();
            open(stage);

        });

        ((Button) scene.lookup("#cancelButton")).setOnAction((ActionEvent e) -> {
            stage.close();
            MainApp.exit();
        });

        ((Button) scene.lookup("#openButton")).setOnAction((ActionEvent e) -> {
            File dbFile = new File(dbField.getValue() == null ? "" : (String) dbField.getValue());
            File keyFile = new File(keyField.getValue() == null ? "" : (String) keyField.getValue());

            if (!dbFile.isFile() || !dbFile.exists() || !dbFile.canRead() || !dbFile.canWrite()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Unable to open database file.");
                alert.initOwner(stage);
                alert.showAndWait();
                return;
            }
            if (!keyFile.isFile() || !keyFile.exists() || !keyFile.canRead()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText("Unable to open master key file.");
                alert.initOwner(stage);
                alert.showAndWait();
                return;
            }

            SafeDataManager.getInstance().setFiles(dbFile, keyFile);

            ConfigManager.getInstance().getConfig().addLastDbFile(dbFile.getPath());
            ConfigManager.getInstance().getConfig().addLastKeyFile(keyFile.getPath());

            stage.hide();
            open(stage);

        });

        FileChooser dbFileChooser = new FileChooser();
        FileChooser keyFileChooser = new FileChooser();

        dbFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        dbFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SafeBox Database File", "*.sbdb"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        keyFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        keyFileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SafeBox Master Key File", "*.sbmk"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        dbSelectButton.setOnAction((ActionEvent e) -> {
            File file = dbFileChooser.showOpenDialog(stage);
            if (file != null) {
                ((ComboBox) scene.lookup("#dbField")).setValue(file.getPath());
            }
        });

        keySelectButton.setOnAction((ActionEvent e) -> {
            File file = keyFileChooser.showOpenDialog(stage);
            if (file != null) {
                ((ComboBox) scene.lookup("#keyField")).setValue(file.getPath());
            }
        });

    }

    public void open(Stage stage) {
        try {
            SafeDataManager.getInstance().load();
        } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidFileFormatException | InvalidAlgorithmParameterException | BadMasterKeyException ex) {
            showException(stage, ex);
            MainApp.exit();
            return;
        }

        FXMLLoader fl = new FXMLLoader();
        Parent root = null;
        try {
            fl.setLocation(getClass().getResource("/fxml/MainScene.fxml"));
            root = fl.load();
        } catch (IOException ex) {
            showException(stage, ex);
            MainApp.exit();
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.sizeToScene();

        ((MainController) fl.getController()).setStage(stage);

        stage.setOnCloseRequest((WindowEvent we) -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Exit");
            alert.setContentText("Are you sure that you want to quit without saving changes?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                stage.hide();
                stage.close();
                MainApp.exit();
            }
            we.consume();
        });
        stage.show();
    }

    public static void showException(Stage stage, Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("Look, an Exception have been throwed");
        alert.setContentText(ex.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.initOwner(stage);

        alert.showAndWait();
    }

    public static void exit() {
        try {
            ConfigManager.getInstance().save();
        } catch (IOException ex) {
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
