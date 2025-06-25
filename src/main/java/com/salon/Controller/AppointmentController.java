package com.salon.Controller;

import com.salon.Model.*;
import com.salon.Utils.CustomAlert;
import com.salon.Utils.SmsService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppointmentController {

    @FXML private TextField clientName;
    @FXML private TextField clientContact;
    @FXML private DatePicker appointmentDate;
    @FXML private ComboBox<Integer> hourCombo;
    @FXML private ComboBox<Integer> minuteCombo;
    @FXML private ComboBox<String> ampmCombo;
    @FXML private ComboBox<String> serviceTypeCombo;
    @FXML private TextField staffNameField;

    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Date> colDate;
    @FXML private TableColumn<Appointment, Time> colTime;
    @FXML private TableColumn<Appointment, String> colClientName;
    @FXML private TableColumn<Appointment, String> colContact;
    @FXML private TableColumn<Appointment, String> colService;
    @FXML private TableColumn<Appointment, String> colStaff;

    private int selectedAppointmentId = -1;

    @FXML
    public void initialize() {
        loadServicesIntoComboBox(); // 🔄 Load from DB
        ampmCombo.setItems(FXCollections.observableArrayList("AM", "PM"));

        for (int i = 1; i <= 12; i++) hourCombo.getItems().add(i);
        for (int i = 0; i < 60; i += 15) minuteCombo.getItems().add(i);

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colClientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("clientContact"));
        colService.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staffName"));

        appointmentTable.setOnMouseClicked(e -> {
            Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedAppointmentId = selected.getId();
                clientName.setText(selected.getClientName());
                clientContact.setText(selected.getClientContact());
                appointmentDate.setValue(selected.getDate().toLocalDate());

                int hour = selected.getTime().toLocalTime().getHour();
                int minute = selected.getTime().toLocalTime().getMinute();
                String ampm = (hour >= 12) ? "PM" : "AM";
                if (hour == 0) hour = 12;
                else if (hour > 12) hour -= 12;

                hourCombo.setValue(hour);
                minuteCombo.setValue(minute);
                ampmCombo.setValue(ampm);

                serviceTypeCombo.setValue(selected.getServiceType());
                staffNameField.setText(selected.getStaffName());
            }
        });

        loadAppointments();
        startReminderScheduler();
    }

    private void loadServicesIntoComboBox() {
        try {
            Connection conn = DatabaseConnection.connect();
            ServiceDAO serviceDAO = new ServiceDAO(conn);
            List<Service> services = serviceDAO.getAllServices();

            List<String> serviceNames = new ArrayList<>();
            for (Service s : services) {
                serviceNames.add(s.getName());
            }

            serviceTypeCombo.setItems(FXCollections.observableArrayList(serviceNames));
        } catch (Exception e) {
            e.printStackTrace();
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Service Error", "Could not load services.");
        }
    }

    @FXML
    public void handleAddAppointment() {
        try {
            if (clientName.getText().isEmpty() || clientContact.getText().isEmpty() || appointmentDate.getValue() == null ||
                    hourCombo.getValue() == null || minuteCombo.getValue() == null || ampmCombo.getValue() == null ||
                    serviceTypeCombo.getValue() == null || staffNameField.getText().isEmpty()) {

                CustomAlert.showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all fields.");
                return;
            }

            int hour = hourCombo.getValue();
            int minute = minuteCombo.getValue();
            String ampm = ampmCombo.getValue();

            if (ampm.equals("PM") && hour < 12) hour += 12;
            else if (ampm.equals("AM") && hour == 12) hour = 0;

            String timeStr = String.format("%02d:%02d:00", hour, minute);

            Appointment appointment = new Appointment();
            appointment.setClientName(clientName.getText().trim());
            appointment.setClientContact(clientContact.getText().trim());
            appointment.setDate(Date.valueOf(appointmentDate.getValue()));
            appointment.setTime(Time.valueOf(timeStr));
            appointment.setServiceType(serviceTypeCombo.getValue());
            appointment.setStaffName(staffNameField.getText().trim());

            if (selectedAppointmentId == -1) {
                AppointmentDAO.insertAppointment(appointment);
                CustomAlert.showSuccess("Appointment Booked!");

                String message = String.format("Hi %s, your appointment is confirmed for %s at %02d:%02d %s. Thanks for choosing Salon Magical!",
                        appointment.getClientName(),
                        appointment.getDate(),
                        hourCombo.getValue(), minuteCombo.getValue(), ampmCombo.getValue()
                );
                SmsService.sendSms(appointment.getClientContact(), message);

            } else {
                appointment.setId(selectedAppointmentId);
                AppointmentDAO.updateAppointment(appointment);
                CustomAlert.showSuccess("Appointment Updated!");

                String message = String.format("Hi %s, your appointment has been updated to %s at %02d:%02d %s. - Salon Magical",
                        appointment.getClientName(),
                        appointment.getDate(),
                        hourCombo.getValue(), minuteCombo.getValue(), ampmCombo.getValue()
                );
                SmsService.sendSms(appointment.getClientContact(), message);

                selectedAppointmentId = -1;
            }

            clearFields();
            loadAppointments();

        } catch (Exception e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Error", "Failed to Save Appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDelete() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            CustomAlert.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment.");
            return;
        }

        try {
            AppointmentDAO.deleteAppointmentById(selected.getId());
            CustomAlert.showSuccess("Appointment Deleted");

            String message = String.format("Hi %s, your appointment scheduled for %s at %s has been cancelled. - Salon Magical",
                    selected.getClientName(),
                    selected.getDate().toString(),
                    selected.getTime().toString().substring(0, 5)
            );
            SmsService.sendSms(selected.getClientContact(), message);

            loadAppointments();
            clearFields();
        } catch (Exception e) {
            CustomAlert.showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete appointment: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdate() {
        handleAddAppointment(); // reuse logic
    }

    @FXML
    public void handleRefresh() {
        loadAppointments();
    }

    private void clearFields() {
        clientName.clear();
        clientContact.clear();
        appointmentDate.setValue(null);
        hourCombo.setValue(null);
        minuteCombo.setValue(null);
        ampmCombo.setValue(null);
        serviceTypeCombo.setValue(null);
        staffNameField.clear();
        selectedAppointmentId = -1;
    }

    private void loadAppointments() {
        try {
            AppointmentDAO.deleteExpiredAppointments();
            List<Appointment> appointments = AppointmentDAO.getAllAppointments();
            appointmentTable.getItems().setAll(appointments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔔 Reminder Scheduler
    private void startReminderScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Appointment> appointments = AppointmentDAO.getAllAppointments();
                LocalDateTime now = LocalDateTime.now();

                for (Appointment appt : appointments) {
                    LocalDateTime apptDateTime = LocalDateTime.of(appt.getDate().toLocalDate(), appt.getTime().toLocalTime());
                    long minutesUntil = Duration.between(now, apptDateTime).toMinutes();

                    if (minutesUntil <= 120 && minutesUntil > 110) {
                        String reminderMessage = String.format(
                                "Hi %s, your appointment is today at %s. Please be on time or it may be cancelled. - Salon Magical",
                                appt.getClientName(),
                                appt.getTime().toString().substring(0, 5)
                        );
                        SmsService.sendSms(appt.getClientContact(), reminderMessage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }
}
