package com.example.demo1;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldClockController {

    @FXML
    private Label clockLabel;

    @FXML
    private TextField timeZoneField;

    @FXML
    private VBox bottomBox;

    @FXML
    private Button addButton;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @FXML
    private void initialize() {
        startMainClock();
        addButton.setOnAction(event -> addClock());
    }

    private void startMainClock() {
        executorService.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    LocalTime now = LocalTime.now();
                    Platform.runLater(() -> {
                        String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        clockLabel.setText("Đồng hồ chính: " + formattedTime);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addClock() {
        String timeZoneId = timeZoneField.getText();
        if (!isValidTimeZone(timeZoneId)) {
            System.err.println("Múi giờ không hợp lệ!");
            return;
        }

        BorderPane newClockPane = new BorderPane();
        Label newClockLabel = new Label();
        newClockPane.setCenter(newClockLabel);

        Scene newScene = new Scene(newClockPane, 200, 100);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.setTitle("Đồng hồ " + timeZoneId);
        newStage.show();

        Thread newClockThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    LocalTime now = LocalTime.now(ZoneId.of(timeZoneId));
                    Platform.runLater(() -> {
                        String formattedTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                        newClockLabel.setText("Đồng hồ " + timeZoneId + ": " + formattedTime);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (DateTimeException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        newClockThread.setDaemon(true);
        newClockThread.start();
    }

    private boolean isValidTimeZone(String timeZoneId) {
        try {
            ZoneId.of(timeZoneId);
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }
}
