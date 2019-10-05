package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField inputId;

	@FXML
	private TextField inputName;

	@FXML
	private TextField inputEmail;

	@FXML
	private DatePicker inputBirthdate;

	@FXML
	private TextField inputBaseSalary;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthdate;

	@FXML
	private Label labelErrorBaseSalary;

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
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(inputId.getText()));

		if (inputName.getText() == null || inputName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(inputName.getText());

		if (!exception.getErrors().isEmpty()) {
			throw exception;
		}
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
		Constraints.setTextFieldMaxLength(inputName, 70);
		Constraints.setTextFieldDouble(inputBaseSalary);
		Constraints.setTextFieldMaxLength(inputEmail, 60);
		Utils.formatDatePicker(inputBirthdate, "dd/MM/yyyy");
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		inputId.setText(String.valueOf(entity.getId()));
		inputName.setText(entity.getName());
		inputEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		inputBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthdate() != null) {
			inputBirthdate.setValue(
					LocalDateTime.ofInstant(entity.getBirthdate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
	}

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
