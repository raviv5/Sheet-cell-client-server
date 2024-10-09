package component.header;

import component.app.AppController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttp;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import utils.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.UPLOAD_FILE;

public class HeaderController {

        @FXML
        private Button buttonUpdateCell;

        @FXML
        private Button buttonUploadXmlFile;

        @FXML
        private Label labelCellId;

        @FXML
        private Label labelOriginalValue;

        @FXML
        private Label labelVersionSelector;

        @FXML
        private SplitMenuButton splitMenuButtonSelectVersion;

        @FXML
        private Label lableFileName;

        @FXML
        private TextField textFieldCellId;

        @FXML
        private TextField textFieldFileName;

        @FXML
        private TextField textFieldOrignalValue;

        @FXML
        private TextField textFieldLastUpdateInVersion;

        @FXML
        private TextField textFieldVersionSelector;

        private SimpleStringProperty selectedFileProperty;

        private AppController mainController;

        public Button getButtonUpdateCell() {
                return buttonUpdateCell;
        }

        public Button getButtonUploadXmlFile() {
                return buttonUploadXmlFile;
        }

        public Label getLabelCellId() {
                return labelCellId;
        }

        public Label getLabelOriginalValue() {
                return labelOriginalValue;
        }

        public Label getLabelVersionSelector() {
                return labelVersionSelector;
        }

        public SplitMenuButton getSplitMenuButtonSelectVersion() {
                return splitMenuButtonSelectVersion;
        }

        public Label getLableFileName() {
                return lableFileName;
        }

        public TextField getTextFieldCellId() {
                return textFieldCellId;
        }

        public TextField getTextFieldFileName() {
                return textFieldFileName;
        }

        public TextField getTextFieldOrignalValue() {
                return textFieldOrignalValue;
        }

        public TextField getTextFieldLastUpdateInVersion() {
                return textFieldLastUpdateInVersion;
        }

        public TextField getTextFieldVersionSelector() {
                return textFieldVersionSelector;
        }

        public AppController getMainController() {
                return mainController;
        }

        public void setMainController(AppController mainController) {
                this.mainController = mainController;
        }

        public String getSelectedFileProperty() {
                return selectedFileProperty.get();
        }

        public SimpleStringProperty selectedFilePropertyProperty() {
                return selectedFileProperty;
        }

        //TEST
        public void bindCellIdTextField(StringProperty strProp) {
                textFieldCellId.textProperty().bind(strProp);
        }

        //test nor real implementation
        @FXML
        void buttonUpdateCellAction(ActionEvent event) {
                mainController.updateCell();
        }

        @FXML
        public void buttonUploadXmlFileAction(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select xml file");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
                File selectedFile = fileChooser.showOpenDialog(mainController.getPrimaryStage());
                if (selectedFile == null) {
                        return;
                }
                selectedFileProperty = new SimpleStringProperty(selectedFile.getAbsolutePath());
                textFieldFileName.textProperty().bind(selectedFileProperty);
                /*
                here i need to pass the content of the file into the server.
                then the server open the file.
                */

                String body = GSON_INSTANCE.toJson(selectedFileProperty.get());
                HttpClientUtil.runAsyncPost(UPLOAD_FILE,body, new Callback() {
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                                if (response.code() != 200) {
                                        Platform.runLater(() -> {
                                           mainController.showAlertPopup(new Exception(response.body().toString()),"loading file");
                                        });
                                }
                                else{
                                        Platform.runLater(() -> {
                                           //mainController.
                                        });
                                }

                        }

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Platform.runLater(()->mainController.showAlertPopup(new Exception(),"unexpected error"));
                        }
                });

                mainController.uploadXml(selectedFileProperty.get());
        }

        @FXML //no need
        void textFieldFileNameAction(ActionEvent event) {

        }

        @FXML //maybe
        void textFieldOrignalValueAction(ActionEvent event) {
                mainController.updateCell();
        }

        @FXML
        void textFieldVersionSelectorAction(ActionEvent event) {

        }

        public void init() {
                SimpleBooleanProperty showHeadersProperty = this.mainController.showHeadersProperty();

                buttonUpdateCell.disableProperty().bind(showHeadersProperty.not());
                splitMenuButtonSelectVersion.setDisable(true);
                textFieldOrignalValue.disableProperty().bind(showHeadersProperty.not());
                textFieldCellId.disableProperty().bind(showHeadersProperty.not());
                textFieldLastUpdateInVersion.disableProperty().bind(showHeadersProperty.not());
                textFieldCellId.textProperty().bind(this.mainController.getCellInFocus().getCoordinate());
                textFieldOrignalValue.textProperty().bindBidirectional(this.mainController.getCellInFocus().getOriginalValue());
                textFieldLastUpdateInVersion.textProperty().bind(this.mainController.getCellInFocus().getLastUpdateVersion());
        }

        public void addMenuOptionToVersionSelection(String numberOfVersion) {

                MenuItem menuItem = new MenuItem(numberOfVersion + " (Editable)");

                // Add an action listener to the MenuItem
                menuItem.setOnAction(event -> {
                        // Update the SplitMenuButton's text to show the selected option
                        mainController.viewSheetVersion(numberOfVersion);
                });

                splitMenuButtonSelectVersion.getItems().forEach(item -> item.setText(item.getText().substring(0,1)));
                // Add the MenuItem to the SplitButton
                splitMenuButtonSelectVersion.getItems().addFirst(menuItem);
        }

        public void clearVersionButton() {
                splitMenuButtonSelectVersion.getItems().clear();
        }
}
