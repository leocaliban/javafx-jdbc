package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.utils.Constraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormController implements Initializable {

	@FXML
	private TextField inputId;

	@FXML
	private TextField inputName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button buttonSave;

	@FXML
	private Button buttonCancel;

	@FXML
	public void onButtonSaveAction(ActionEvent event) {
	}

	@FXML
	public void onButtonCancelAction(ActionEvent event) {
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(inputId);
		Constraints.setTextFieldMaxLength(inputName, 30);
	}

}
