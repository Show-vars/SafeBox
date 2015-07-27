package ru.iostd.safebox.controllers;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javax.swing.Timer;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.crypto.NoSuchPaddingException;
import ru.iostd.safebox.exceptions.InvalidFileFormatException;
import ru.iostd.safebox.MainApp;
import ru.iostd.safebox.data.SafeDataManager;
import ru.iostd.safebox.data.SafeRecord;
import ru.iostd.safebox.exceptions.BadMasterKeyException;
import ru.iostd.safebox.wizard.importdata.ImportSurvey;
import ru.iostd.safebox.wizard.importdata.ImportSurveyData;

public class MainController implements Initializable {

    private final ObservableList<SafeRecord> observableList = FXCollections.observableList(SafeDataManager.getInstance().getData().getRecords());
    private Stage primaryStage;

    @FXML
    private TableView dataTable;
    @FXML
    private TableColumn columnTitle;
    @FXML
    private TableColumn columnUsername;
    @FXML
    private TableColumn columnPassword;
    @FXML
    private TableColumn columnURL;
    @FXML
    private TableColumn columnNotes;
    @FXML
    private TextField searchField;

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        try {
            SafeDataManager.getInstance().save();
            MainApp.exit();
        } catch (IOException | InvalidFileFormatException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            MainApp.showException(primaryStage, ex);
        }
    }

    @FXML
    private void handleImportButton(ActionEvent event) {
        try {
            ImportSurveyData data = new ImportSurveyData();
            data.observableList = observableList;
            Stage surveyStage = new Stage();
            surveyStage.setTitle("Importing");
            surveyStage.initOwner(primaryStage);
            surveyStage.setResizable(false);
            surveyStage.getIcons().add(new Image(getClass().getResourceAsStream("/media/SafeBoxLogo.png")));
            surveyStage.initModality(Modality.APPLICATION_MODAL);
            ImportSurvey survey = new ImportSurvey(data, surveyStage);
            survey.start();

        } catch (Exception ex) {
            MainApp.showException(primaryStage, ex);
        }
    }

    public void handleDiscardButton(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Discard changes");
        alert.setContentText("Are you sure that you want to discard changes?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                SafeDataManager.getInstance().load();

            } catch (IOException | InvalidFileFormatException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | BadMasterKeyException ex) {
                MainApp.showException(primaryStage, ex);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        columnTitle.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SafeRecord, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SafeRecord, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getTitle());
                } else {
                    return new SimpleStringProperty("n/a");
                }
            }
        });
        columnUsername.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SafeRecord, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SafeRecord, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getUserName());
                } else {
                    return new SimpleStringProperty("n/a");
                }
            }
        });
        columnPassword.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SafeRecord, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SafeRecord, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty("******");
                } else {
                    return new SimpleStringProperty("n/a");
                }
            }
        });
        columnURL.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SafeRecord, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SafeRecord, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getUrl());
                } else {
                    return new SimpleStringProperty("n/a");
                }
            }
        });
        columnNotes.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SafeRecord, String>, ObservableValue<String>>() {

            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SafeRecord, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getNotes());
                } else {
                    return new SimpleStringProperty("n/a");
                }
            }
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            ((FilteredList<SafeRecord>) dataTable.getItems()).setPredicate(safeData -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return safeData.getTitle().toLowerCase().contains(lowerCaseFilter);
            });
        });
        observableList.addListener((ListChangeListener.Change<? extends SafeRecord> c) -> {
            dataTable.setItems(new FilteredList<>(observableList, p -> true));
        });
        dataTable.setItems(new FilteredList<>(observableList, p -> true));

        final MenuItem addMenuItem = new MenuItem("Add", new ImageView(new Image(getClass().getResourceAsStream("/media/plus.png"))));
        final MenuItem editMenuItem = new MenuItem("Edit", new ImageView(new Image(getClass().getResourceAsStream("/media/pencil.png"))));
        final MenuItem removeMenuItem = new MenuItem("Remove", new ImageView(new Image(getClass().getResourceAsStream("/media/trash.png"))));
        addMenuItem.setOnAction((ActionEvent event) -> {
            Dialog dialog = addEditDialog(null);
            Optional<SafeRecord> result = dialog.showAndWait();

            result.ifPresent(safeData -> {
                observableList.add(safeData);
            });
        });
        editMenuItem.setOnAction((ActionEvent event) -> {
            SafeRecord safeRecord = (SafeRecord) dataTable.getSelectionModel().getSelectedItem();

            Dialog dialog = addEditDialog(safeRecord);
            Optional<SafeRecord> result = dialog.showAndWait();

            result.ifPresent(safeData -> {
                observableList.set(observableList.indexOf(safeRecord), safeData);
            });
        });
        removeMenuItem.setOnAction((ActionEvent event) -> {
            SafeRecord safeRecord = (SafeRecord) dataTable.getSelectionModel().getSelectedItem();

            observableList.remove(safeRecord);
        });

        dataTable.setContextMenu(new ContextMenu(addMenuItem, editMenuItem, new SeparatorMenuItem(), removeMenuItem));

        final Timer t = new Timer(15000, new ActionListener() {
            private final ClipboardContent content = new ClipboardContent();

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                content.putString("");
                clipboard.setContent(content);
            }
        });
        t.setRepeats(false);

        dataTable.setRowFactory(tv -> {
            TableRow<SafeRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    SafeRecord rowData = row.getItem();
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();
                    content.putString(rowData.getPassword());
                    clipboard.setContent(content);

                    //if (t.isRunning()) {
                    //    t.restart();
                    //} else {
                    //    t.start();
                    //}
                }
            });
            return row;
        });

    }

    private Dialog addEditDialog(SafeRecord safeRecord) {
        Dialog<SafeRecord> dialog = new Dialog<>();
        dialog.setTitle("Add new record");
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField urlFiled = new TextField();
        TextArea notesFeild = new TextArea();
        notesFeild.setWrapText(true);

        if (safeRecord != null) {
            titleField.setText(safeRecord.getTitle());
            usernameField.setText(safeRecord.getUserName());
            passwordField.setText(safeRecord.getPassword());
            urlFiled.setText(safeRecord.getUrl());
            notesFeild.setText(safeRecord.getNotes());
        }

        Button rand = new Button("Randomize");
        rand.setOnAction((ActionEvent event) -> {
            passwordField.setText(generateString(new Random(), "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", 16));
        });
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("User Name:"), 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(rand, 2, 2);
        grid.add(new Label("URL:"), 0, 3);
        grid.add(urlFiled, 1, 3);
        grid.add(new Label("Notes:"), 0, 4);
        grid.add(notesFeild, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.initOwner(primaryStage);

        Platform.runLater(() -> titleField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new SafeRecord(titleField.getText(), usernameField.getText(), passwordField.getText(), urlFiled.getText(), notesFeild.getText(), LocalDateTime.now());
            }
            return null;
        });

        return dialog;
    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
