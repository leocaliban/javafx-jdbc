package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	private Department entity;

	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

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
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(inputId.getText()));
		obj.setName(inputName.getText());
		return obj;
	}

	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(l -> l.onDataChanged());
	}

	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		initializeNodes();

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(inputId);
		Constraints.setTextFieldMaxLength(inputName, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		inputId.setText(String.valueOf(entity.getId()));
		inputName.setText(entity.getName());
	}

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
}
