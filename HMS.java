/*
GROUP: Ng Yan Lam, Leong Yung Thai, Desmond Cheng Wen Xuan
Group Assignment
Title: Hospital Management System
*/

package hms;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Date;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class HMS extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage loginPage) {
        loginPage.setTitle("Login");

        // Load the image
        Image userImg = new Image("assets/icon/username-icon.png");
        Image passImg = new Image("assets/icon/password-icon.png");

        // Create an ImageView to display the image
        ImageView userImgView = new ImageView(userImg);
        userImgView.setFitWidth(50);
        userImgView.setFitHeight(50);
        ImageView passImgView = new ImageView(passImg);
        passImgView.setFitWidth(50);
        passImgView.setFitHeight(50);

        // Staff ID
        Label staffIDLabel = new Label();
        staffIDLabel.setGraphic(userImgView);
        TextField staffIDField = new TextField();
        staffIDField.setFocusTraversable(false);
        staffIDField.setPromptText("Username");

        // Password
        Label passwordLabel = new Label();
        passwordLabel.setGraphic(passImgView);
        PasswordField passwordField = new PasswordField();
        passwordField.setFocusTraversable(false);
        passwordField.setPromptText("Password");

        // Login Button
        Button loginBtn = new Button("Login");

        // Font for Login Page
        Font loginFont = Font.font("Arial", 20);

        // Create the login layout
        Pane loginLayout = new Pane();
        loginLayout.setPadding(new Insets(20));
        loginLayout.getChildren().add(staffIDLabel);
        staffIDLabel.setLayoutX(40);
        staffIDLabel.setLayoutY(40);
        loginLayout.getChildren().add(staffIDField);
        staffIDField.setLayoutX(110);
        staffIDField.setLayoutY(50);
        staffIDField.setFont(loginFont);
        loginLayout.getChildren().add(passwordLabel);
        passwordLabel.setLayoutX(40);
        passwordLabel.setLayoutY(120);
        loginLayout.getChildren().add(passwordField);
        passwordField.setLayoutX(110);
        passwordField.setLayoutY(132);
        passwordField.setFont(loginFont);
        loginLayout.getChildren().add(loginBtn);
        loginBtn.setLayoutX(130);
        loginBtn.setLayoutY(230);
        loginBtn.setPrefWidth(150);

        Scene loginScene = new Scene(loginLayout, 400, 300);
        loginPage.setScene(loginScene);
        loginPage.setResizable(false);
        loginPage.show();

        // Login function
        loginBtn.setOnAction(e -> {
            String username = staffIDField.getText();
            String password = passwordField.getText();

            try {
                // Connect to MySQL database
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");

                // Create statement object
                Statement stmt = con.createStatement();

                // Execute SQL query to verify user
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM staff WHERE USERNAME='" + username + "' AND PASSWORD ='" + password + "'");
                if (rs.next()) {
                    loginPage.close();
                    mainPage(rs.getString("USERNAME"), rs.getString("EMPLOYEETYPE"));
                } else {
                    // Show an error message for invalid credentials
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Information");
                    alert.setHeaderText(null);
                    alert.setContentText("Wrong username or password");
                    alert.showAndWait();
                }
                // Clean up resources
                rs.close();
                stmt.close();
                con.close();
            } catch (Exception a) {
                a.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Error: " + a.getMessage());
                alert.showAndWait();
            }
        });
    }

    // Main Page
    private void mainPage(String staffID, String EmployeeType) {
        Stage mainPageStage = new Stage();
        mainPageStage.setTitle("Hospital Management System");

        Label systemTitleLabel = new Label("Welcome, " + staffID);
        systemTitleLabel.setStyle("-fx-font-size: 30;");
        systemTitleLabel.setPadding(new Insets(20));

        // Tab Pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setSide(Side.LEFT);
        tabPane.getStyleClass().add("horizontal-tabs");

        // Overview Page
        Tab overviewTab = new Tab("Overview");

        // Overview Tab Icon
        Image overviewIcon = new Image("assets/icon/overview-logo.png");
        ImageView overviewIconView = new ImageView(overviewIcon);
        overviewIconView.setFitWidth(30);
        overviewIconView.setFitHeight(30);
        overviewIconView.getStyleClass().add("TabIcon");
        overviewTab.setGraphic(overviewIconView);

        VBox overviewLayout = new VBox();
        overviewLayout.setSpacing(10);
        overviewLayout.setPadding(new Insets(20));
        overviewPage(overviewLayout);
        overviewTab.setContent(overviewLayout);
        overviewTab.setClosable(false);

        // Click tab to reload the page
        overviewTab.setOnSelectionChanged(event -> {
            if (overviewTab.isSelected()) {
                overviewLayout.getChildren().clear();
                overviewPage(overviewLayout);
            }
        });

        // Register Page
        Tab registerTab = new Tab("Registration");

        // Register Tab Icon
        Image registerIcon = new Image("assets/icon/register-logo.png");
        ImageView registerIconView = new ImageView(registerIcon);
        registerIconView.setFitWidth(30);
        registerIconView.setFitHeight(30);
        registerIconView.getStyleClass().add("TabIcon");
        registerTab.setGraphic(registerIconView);

        VBox registerLayout = new VBox(10);
        registerLayout.setPadding(new Insets(20));
        registerPage(registerLayout);
        registerTab.setContent(registerLayout);
        registerTab.setClosable(false);

        // Consultation Page
        Tab consultationTab = new Tab("Consultation");

        // Consultation Tab Icon
        Image consultationIcon = new Image("assets/icon/consultation-logo.png");
        ImageView consultationIconView = new ImageView(consultationIcon);
        consultationIconView.setFitWidth(30);
        consultationIconView.setFitHeight(30);
        consultationIconView.getStyleClass().add("TabIcon");
        consultationTab.setGraphic(consultationIconView);

        VBox consultationLayout = new VBox(10);
        consultationLayout.setPadding(new Insets(20));
        consultationPage(consultationLayout);
        consultationTab.setContent(consultationLayout);
        consultationTab.setClosable(false);

        // Patient Page
        Tab patientTab = new Tab("Patient List");

        // Patient Tab Icon
        Image patientIcon = new Image("assets/icon/patient-logo.png");
        ImageView patientIconnView = new ImageView(patientIcon);
        patientIconnView.setFitWidth(30);
        patientIconnView.setFitHeight(30);
        patientIconnView.getStyleClass().add("TabIcon");
        patientTab.setGraphic(patientIconnView);

        VBox patientLayout = new VBox();
        patientLayout.setPadding(new Insets(20));
        patientLayout.setSpacing(10);
        patientPage(patientLayout);
        patientTab.setContent(patientLayout);
        patientTab.setClosable(false);

        patientTab.setOnSelectionChanged(event -> {
            if (patientTab.isSelected()) {
                patientLayout.getChildren().clear();
                patientPage(patientLayout);
            }
        });

        // Checkout Page
        Tab checkoutTab = new Tab("Check Out");

        // Checkout Tab Icon
        Image checkoutIcon = new Image("assets/icon/checkout-logo.png");
        ImageView checkoutIconView = new ImageView(checkoutIcon);
        checkoutIconView.setFitWidth(30);
        checkoutIconView.setFitHeight(30);
        checkoutIconView.getStyleClass().add("TabIcon");
        checkoutTab.setGraphic(checkoutIconView);

        VBox checkoutLayout = new VBox();
        checkoutLayout.setPadding(new Insets(20));
        checkoutLayout.setSpacing(10);
        checkoutPage(checkoutLayout);
        checkoutTab.setContent(checkoutLayout);
        checkoutTab.setClosable(false);

        // Logout Page
        Tab logoutTab = new Tab("Logout");

        // Logout Tab Icon
        Image logoutIcon = new Image("assets/icon/logout-logo.png");
        ImageView logoutIconView = new ImageView(logoutIcon);
        logoutIconView.setFitWidth(30);
        logoutIconView.setFitHeight(30);
        logoutIconView.getStyleClass().add("TabIcon");
        logoutTab.setGraphic(logoutIconView);

        logoutTab.setClosable(false);
        logout(logoutTab, mainPageStage);

        // Add the tabs to the tab pane
        tabPane_employeeVerify(EmployeeType, tabPane, overviewTab, registerTab, consultationTab, patientTab,
                checkoutTab,
                logoutTab);

        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_LEFT);
        mainLayout.getChildren().addAll(systemTitleLabel, tabPane);

        Scene mainScene = new Scene(mainLayout, 1200, 800);
        mainScene.getStylesheets().add(getClass().getResource("style").toExternalForm());
        mainPageStage.setScene(mainScene);
        mainPageStage.setMaximized(true);
        // mainPageStage.setResizable(false);
        mainPageStage.show();
    }

    // Ultilities
    private static void tabPane_employeeVerify(String EmployeeType, TabPane tabPane, Tab overviewTab, Tab registerTab,
            Tab consultationTab, Tab patientTab, Tab checkoutTab,
            Tab logoutTab) {

        if (EmployeeType.equals("Front-desk")) {
            consultationTab.setDisable(true);
            patientTab.setDisable(true);
            checkoutTab.setDisable(true);
        } else if (EmployeeType.equals("Nurse")) {
            registerTab.setDisable(true);
            checkoutTab.setDisable(true);
        } else if (EmployeeType.equals("Doctor")) {
            registerTab.setDisable(true);
            checkoutTab.setDisable(true);
        } else if (EmployeeType.equals("Administrator")) {
            registerTab.setDisable(true);
            consultationTab.setDisable(true);
        }

        tabPane.getTabs().addAll(overviewTab, registerTab, consultationTab, patientTab, checkoutTab, logoutTab);
        // System.out.println(EmployeeType);
    }

    private static void overviewPage(VBox overviewLayout) {
        // Font
        Font overviewFont_1 = Font.font("Arial", FontWeight.BOLD, 25);
        Font clockFont = Font.font("Arial", FontWeight.BOLD, 40);

        // Label of Overview Page
        Label overviewTitle = new Label("Overview");
        Label timeLabel = new Label();
        Label dateWeek = new Label();

        // Create a Rectangle to represent the card
        Rectangle card_1 = new Rectangle(410, 100);
        card_1.setFill(Color.WHITE);
        card_1.setStroke(Color.BLACK);
        card_1.setArcWidth(20);
        card_1.setArcHeight(20);

        Rectangle card_2 = new Rectangle(200, 80);
        card_2.setFill(Color.WHITE);
        card_2.setStroke(Color.BLACK);
        card_2.setArcWidth(20);
        card_2.setArcHeight(20);

        Rectangle card_3 = new Rectangle(200, 80);
        card_3.setFill(Color.WHITE);
        card_3.setStroke(Color.BLACK);
        card_3.setArcWidth(20);
        card_3.setArcHeight(20);

        Rectangle card_4 = new Rectangle(200, 80);
        card_4.setFill(Color.WHITE);
        card_4.setStroke(Color.BLACK);
        card_4.setArcWidth(20);
        card_4.setArcHeight(20);

        Rectangle card_5 = new Rectangle(200, 80);
        card_5.setFill(Color.WHITE);
        card_5.setStroke(Color.BLACK);
        card_5.setArcWidth(20);
        card_5.setArcHeight(20);

        Rectangle card_6 = new Rectangle(300, 280);
        card_6.setFill(Color.WHITE);
        card_6.setStroke(Color.BLACK);
        card_6.setArcWidth(20);
        card_6.setArcHeight(20);

        StackPane cardLayout_1 = new StackPane(card_1);
        StackPane cardLayout_2 = new StackPane(card_2);
        StackPane cardLayout_3 = new StackPane(card_3);
        StackPane cardLayout_4 = new StackPane(card_4);
        StackPane cardLayout_5 = new StackPane(card_5);
        StackPane cardLayout_6 = new StackPane(card_6);

        // Get total patient
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) AS Total_Patient FROM patient");
            if (rs.next()) {
                int total = rs.getInt("Total_Patient");

                // Create an ImageView to display the image
                Image img = new Image("assets/icon/patient.png");
                ImageView patientImgView = new ImageView(img);
                patientImgView.setFitWidth(80);
                patientImgView.setFitHeight(80);

                Label patientImg = new Label();
                patientImg.setGraphic(patientImgView);
                Label totalLabel = new Label("Total patient's");
                totalLabel.setFont(Font.font("Arial", 20));
                Label countLabel = new Label("" + total);
                countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));

                HBox patientBox = new HBox(5);
                HBox.setMargin(patientImg, new Insets(0, 30, 0, 0));
                VBox patientContentBox = new VBox(5);
                patientContentBox.getChildren().addAll(totalLabel, countLabel);
                patientBox.getChildren().addAll(patientImg, patientContentBox);
                patientBox.setAlignment(Pos.CENTER);
                patientContentBox.setAlignment(Pos.CENTER_LEFT);
                cardLayout_1.getChildren().add(patientBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        // Get total bed from Maternity Ward
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT total_bed, occupied_bed FROM ward WHERE WARDNAME='Maternity'");
            if (rs.next()) {

                Label wardName = new Label("Maternity Ward");
                wardName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                Label totalBedLabel = new Label("Total Bed");
                totalBedLabel.setFont(Font.font("Arial", 13));
                Label bedAvailabilityLabel = new Label(
                        rs.getString("occupied_bed") + " / " + rs.getString("total_bed"));
                bedAvailabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

                VBox wardBox = new VBox(5);
                wardBox.getChildren().addAll(wardName, totalBedLabel, bedAvailabilityLabel);
                wardBox.setPadding(new Insets(0, 20, 0, 20));
                wardBox.setAlignment(Pos.CENTER_LEFT);
                cardLayout_2.getChildren().add(wardBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        // Get total bed from Surgical Ward
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT total_bed, occupied_bed FROM ward WHERE WARDNAME='Surgical'");
            if (rs.next()) {
                Label wardName = new Label("Surgical Ward");
                wardName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                Label totalBedLabel = new Label("Total Bed");
                totalBedLabel.setFont(Font.font("Arial", 13));
                Label bedAvailabilityLabel = new Label(
                        rs.getString("occupied_bed") + " / " + rs.getString("total_bed"));
                bedAvailabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

                VBox wardBox = new VBox(5);
                wardBox.getChildren().addAll(wardName, totalBedLabel, bedAvailabilityLabel);
                wardBox.setPadding(new Insets(0, 20, 0, 20));
                wardBox.setAlignment(Pos.CENTER_LEFT);
                cardLayout_3.getChildren().add(wardBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        // Get total bed from Cancer Ward
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT total_bed, occupied_bed FROM ward WHERE WARDNAME='Cancer'");
            if (rs.next()) {
                Label wardName = new Label("Cancer Ward");
                wardName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                Label totalBedLabel = new Label("Total Bed");
                totalBedLabel.setFont(Font.font("Arial", 13));
                Label bedAvailabilityLabel = new Label(
                        rs.getString("occupied_bed") + " / " + rs.getString("total_bed"));
                bedAvailabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

                VBox wardBox = new VBox(5);
                wardBox.getChildren().addAll(wardName, totalBedLabel, bedAvailabilityLabel);
                wardBox.setPadding(new Insets(0, 20, 0, 20));
                wardBox.setAlignment(Pos.CENTER_LEFT);
                cardLayout_4.getChildren().add(wardBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        // Get total bed from Cardiac Ward
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT total_bed, occupied_bed FROM ward WHERE WARDNAME='Cardiac'");
            if (rs.next()) {
                Label wardName = new Label("Cardiac Ward");
                wardName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
                Label totalBedLabel = new Label("Total Bed");
                totalBedLabel.setFont(Font.font("Arial", 13));
                Label bedAvailabilityLabel = new Label(
                        rs.getString("occupied_bed") + " / " + rs.getString("total_bed"));
                bedAvailabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

                VBox wardBox = new VBox(5);
                wardBox.getChildren().addAll(wardName, totalBedLabel, bedAvailabilityLabel);
                wardBox.setPadding(new Insets(0, 20, 0, 20));
                wardBox.setAlignment(Pos.CENTER_LEFT);
                cardLayout_5.getChildren().add(wardBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        // Get total of doctors
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) AS Total_Doctor FROM staff WHERE EMPLOYEETYPE='Doctor'");
            if (rs.next()) {
                int total = rs.getInt("Total_Doctor");

                // Create an ImageView to display the image
                Image img = new Image("assets/icon/doctor.png");
                ImageView doctorImgView = new ImageView(img);
                doctorImgView.setFitWidth(80);
                doctorImgView.setFitHeight(80);

                Label doctorImg = new Label();
                doctorImg.setGraphic(doctorImgView);
                Label totalLabel = new Label("Total doctor's");
                totalLabel.setFont(Font.font("Arial", 20));
                Label countLabel = new Label("" + total);
                countLabel.setFont(Font.font("Arial", FontWeight.BOLD, 17));

                VBox doctorBox = new VBox(5);
                VBox.setMargin(totalLabel, new Insets(30, 0, 0, 0));
                doctorBox.getChildren().addAll(doctorImg, totalLabel, countLabel);
                doctorBox.setPadding(new Insets(0, 20, 0, 20));
                doctorBox.setAlignment(Pos.CENTER);
                cardLayout_6.getChildren().add(doctorBox);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Opps...Something went wrong");
                alert.showAndWait();
            }
            // Clean up resources
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + a.getMessage());
            alert.showAndWait();
        }

        getCurrentTime(timeLabel, dateWeek);
        VBox clockLayout = new VBox(5);
        clockLayout.setPrefSize(250, 150);
        clockLayout.setAlignment(Pos.CENTER);
        clockLayout.getChildren().addAll(timeLabel, dateWeek);

        VBox availabilityShow = new VBox(10);
        VBox wardStatus = new VBox(10);
        HBox bedAvailability_1 = new HBox(10);
        HBox bedAvailability_2 = new HBox(10);
        bedAvailability_1.getChildren().addAll(cardLayout_2, cardLayout_3);
        bedAvailability_2.getChildren().addAll(cardLayout_4, cardLayout_5);
        wardStatus.getChildren().addAll(bedAvailability_1, bedAvailability_2);
        availabilityShow.getChildren().addAll(cardLayout_1, wardStatus);

        HBox firstRow = new HBox(20);
        firstRow.getChildren().addAll(clockLayout, availabilityShow, cardLayout_6);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        firstRow.getChildren().addAll(spacer);

        HBox.setHgrow(clockLayout, Priority.ALWAYS);
        HBox.setHgrow(cardLayout_6, Priority.ALWAYS);

        HBox.setMargin(availabilityShow, new Insets(0, 10, 0, 10));
        firstRow.setAlignment(Pos.CENTER);

        overviewTitle.setFont(overviewFont_1);
        timeLabel.setFont(clockFont);
        VBox contentLayout = new VBox(10);
        contentLayout.getChildren().addAll(overviewTitle, firstRow);
        overviewLayout.getChildren().add(contentLayout);
    }

    private static void registerPage(VBox registerLayout) {
        // Font
        Font registerTitleFont = Font.font("Arial", FontWeight.BOLD, 25);
        Font registerLabelFont = Font.font("Arial", 18);

        // Label
        Label registrationTitleLabel = new Label("Patient Registration");
        registrationTitleLabel.setFont(registerTitleFont);
        Label spacing = new Label("");
        Label firstNameLabel = new Label("First Name");
        firstNameLabel.setFont(registerLabelFont);
        Label lastNameLabel = new Label("Last Name");
        lastNameLabel.setFont(registerLabelFont);
        Label phoneNumberLabel = new Label("Phone Number");
        phoneNumberLabel.setFont(registerLabelFont);
        Label dateOfBirthLabel = new Label("Date of Birth");
        dateOfBirthLabel.setFont(registerLabelFont);
        Label personalHealthNumberLabel = new Label("Personal Health Number");
        personalHealthNumberLabel.setFont(registerLabelFont);
        Label addressLabel = new Label("Address");
        addressLabel.setFont(registerLabelFont);
        Label postalCodeLabel = new Label("Postal Code");
        postalCodeLabel.setFont(registerLabelFont);
        Label cityLabel = new Label("City");
        cityLabel.setFont(registerLabelFont);
        Label countryLabel = new Label("Country");
        countryLabel.setFont(registerLabelFont);
        Label patientIdenficationNumberLabel = new Label("Patient Idenfication Number");
        patientIdenficationNumberLabel.setFont(registerLabelFont);

        // Textfield
        TextField firstNameField = new TextField();
        firstNameField.setFont(registerLabelFont);
        TextField lastNameField = new TextField();
        lastNameField.setFont(registerLabelFont);
        TextField dateOfBirthField = new TextField();
        dateOfBirthField.setFont(registerLabelFont);
        dateOfBirthField.setPromptText("eg. yyyy-mm-dd");
        TextField phoneNumberField = new TextField();
        phoneNumberField.setFont(registerLabelFont);
        phoneNumberField.setPromptText("eg. 0123456789");
        TextField personalHealthNumberField = new TextField();
        personalHealthNumberField.setFont(registerLabelFont);
        TextField addressField = new TextField();
        addressField.setFont(registerLabelFont);
        TextField postalCodeField = new TextField();
        postalCodeField.setFont(registerLabelFont);
        TextField cityField = new TextField();
        cityField.setFont(registerLabelFont);
        TextField countryField = new TextField();
        countryField.setFont(registerLabelFont);
        TextField patientIdenficationNumberField = new TextField();
        patientIdenficationNumberField.setFont(registerLabelFont);
        patientIdenficationNumberField.setPromptText("eg. 023456789900");

        // Button for Registration Page
        Button registerBtn = new Button("Register");
        registerBtn.setPadding(new Insets(10));
        registerBtn.setFont(registerLabelFont);
        HBox btnLayout = new HBox(registerBtn);
        btnLayout.setAlignment(Pos.CENTER_RIGHT);
        btnLayout.setPadding(new Insets(10));

        registerBtn.setOnAction(e -> {

            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String dateOfBirth = dateOfBirthField.getText();
            String phoneNumber = phoneNumberField.getText();
            String personalHealthNumber = personalHealthNumberField.getText();
            String address = addressField.getText();
            String postalCode = postalCodeField.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            String patientIdentificationNumber = patientIdenficationNumberField.getText();

            // Insert the data into the database
            try {
                // Please check your port before run it!!!
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
                Statement stmt = con.createStatement();

                String query = "INSERT INTO patient (FIRSTNAME, LASTNAME, DATEOFBIRTH, PHONENUMBER, PHEALTHNUMBER, ADDRESS, POSTALCODE, CITY, COUNTRY, PIDENTIFICATIONNUMBER, isHospitalized) VALUES ('"
                        +
                        firstName + "', '" + lastName + "', '" + dateOfBirth + "', '" + phoneNumber + "', '"
                        + personalHealthNumber + "', '"
                        + address + "', '" + postalCode + "', '" + city + "', '" + country + "', '"
                        + patientIdentificationNumber + "', 0)";

                int rowsAffected = stmt.executeUpdate(query);

                if (rowsAffected > 0) {
                    // Show registration success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Registration");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Registration successfully!");
                    successAlert.showAndWait();

                    // Clear the text fields
                    firstNameField.setText("");
                    lastNameField.setText("");
                    phoneNumberField.setText("");
                    personalHealthNumberField.setText("");
                    addressField.setText("");
                    postalCodeField.setText("");
                    cityField.setText("");
                    countryField.setText("");
                    patientIdenficationNumberField.setText("");
                    dateOfBirthField.setText("");
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Registration failed! Please try again.");
                    errorAlert.showAndWait();
                }

                stmt.close();
                con.close();
            } catch (Exception ex) {
                // Show error message
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
                ex.printStackTrace();
            }
        });

        registerLayout.getChildren().addAll(registrationTitleLabel, spacing, firstNameLabel, firstNameField,
                lastNameLabel, lastNameField, dateOfBirthLabel, dateOfBirthField, phoneNumberLabel, phoneNumberField,
                patientIdenficationNumberLabel,
                patientIdenficationNumberField,
                personalHealthNumberLabel, personalHealthNumberField, addressLabel,
                addressField, postalCodeLabel, postalCodeField, cityLabel, cityField, countryLabel, countryField,
                btnLayout);
    }

    private static void consultationPage(VBox consultationLayout) {

        VBox patientDataContainer = new VBox(10);

        // Font
        Font consultationTitleFont = Font.font("Arial", FontWeight.BOLD, 25);
        Font consultationLabelFont = Font.font("Arial", 18);

        // Label for Consulation Page
        Label consultationTitle = new Label("Ward Assignation");
        consultationTitle.setFont(consultationTitleFont);

        Label spacing = new Label("");

        // First row components
        Label PHN = new Label("Patient Health Number");
        PHN.setFont(consultationLabelFont);
        PHN.setPadding(new Insets(5, 0, 0, 0));
        TextField phnTextfield = new TextField();
        phnTextfield.setFont(consultationLabelFont);
        Button searchBtn = new Button("Search");
        searchBtn.setFont(consultationLabelFont);
        searchBtn
                .setOnAction(event -> getPatientData(phnTextfield.getText(), consultationLayout, patientDataContainer));

        // Second row components
        Label des = new Label("Description");
        des.setFont(consultationLabelFont);
        TextArea desTextfield = new TextArea();
        desTextfield.setPromptText("Write something...");
        desTextfield.setPrefHeight(200);
        desTextfield.setWrapText(true);
        desTextfield.setFont(consultationLabelFont);

        // Third row components
        // DropDown Box
        Label wardTypeLabel = new Label("Ward Type: ");
        wardTypeLabel.setFont(consultationLabelFont);
        ComboBox<String> wardType = new ComboBox<>();
        wardTypeDropDown(wardType);

        // Display Available BedID
        Label bedIDLabel = new Label("BedID Assinged: ");
        bedIDLabel.setFont(consultationLabelFont);
        TextField bedIDTextField = new TextField();
        bedIDTextField.setPromptText("Please select ward type");
        bedIDTextField.setEditable(false);
        bedIDTextField.setMouseTransparent(true);
        bedIDTextField.setFocusTraversable(false);

        Label assignedDoctorLabel = new Label("Doctor Assigned");
        assignedDoctorLabel.setFont(consultationLabelFont);
        TextField assignedDoctorTextField = new TextField();
        assignedDoctorTextField.setPromptText("Please select ward type");
        assignedDoctorTextField.setEditable(false);
        assignedDoctorTextField.setMouseTransparent(true);
        assignedDoctorTextField.setFocusTraversable(false);

        Label assignedNurseLabel = new Label("Nurse Assigned");
        assignedNurseLabel.setFont(consultationLabelFont);
        TextField assignedNurseTextField = new TextField();
        assignedNurseTextField.setPromptText("Please select ward type");
        assignedNurseTextField.setEditable(false);
        assignedNurseTextField.setMouseTransparent(true);
        assignedNurseTextField.setFocusTraversable(false);

        wardType.setOnAction(event -> availableBed(wardType.getValue(), bedIDTextField, assignedDoctorTextField,
                assignedNurseTextField));

        // Fourth row
        Button resetBtn = new Button("Reset");
        resetBtn.setFont(consultationLabelFont);
        resetBtn.setOnAction(event -> {
            phnTextfield.clear();
            patientDataContainer.getChildren().clear();
            consultationLayout.getChildren().clear();
            consultationPage(consultationLayout);
        });

        Button assignBtn = new Button("Assign");
        assignBtn.setFont(consultationLabelFont);
        assignBtn.setOnAction(event -> assignBed(consultationLayout, phnTextfield, desTextfield, wardType.getValue(),
                bedIDTextField, assignedDoctorTextField, assignedNurseTextField));

        HBox firstRow = new HBox(10);
        firstRow.getChildren().addAll(PHN, phnTextfield, searchBtn);

        VBox secondRow = new VBox(10);
        secondRow.getChildren().addAll(des, desTextfield);

        HBox thirdRow = new HBox(10);
        thirdRow.setPadding(new Insets(20, 0, 0, 0));
        thirdRow.getChildren().addAll(wardTypeLabel, wardType, bedIDLabel, bedIDTextField, assignedDoctorLabel,
                assignedDoctorTextField, assignedNurseLabel, assignedNurseTextField);

        HBox fourthRow = new HBox(10);
        fourthRow.getChildren().addAll(resetBtn, assignBtn);

        consultationLayout.getChildren().addAll(consultationTitle, spacing, firstRow, patientDataContainer, secondRow,
                thirdRow, fourthRow);
    }

    private static void patientPage(VBox patientLayout) {
        // Font
        Font patientTitleFont = Font.font("Arial", FontWeight.BOLD, 25);
        Font patientLabelFont = Font.font("Arial", 18);

        // Label for Consulation Page
        Label patientTitle = new Label("Patient List");
        patientTitle.setFont(patientTitleFont);
        Label spacing_1 = new Label();

        // Table
        TableView<ObservableList<String>> table = new TableView<>();
        table.setFixedCellSize(40);
        repopulateTable(table, "");

        VBox patientListBox = new VBox(10);
        patientListBox.getChildren().addAll(table);
        patientListBox.setPadding(new Insets(0, 20, 0, 20));

        // Search fields
        TextField searchField = new TextField();
        searchField.setPromptText("Please search with PatientID or Lastname or PHN");
        searchField.setFont(patientLabelFont);
        searchField.setPrefWidth(1450);

        // Search Button
        Button searchBtn = new Button("Search");
        searchBtn.setFont(patientLabelFont);
        searchBtn.setOnAction(event -> repopulateTable(table, searchField.getText()));

        // Refersh Button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setFont(patientLabelFont);
        refreshBtn.setOnAction(event -> {
            repopulateTable(table, "");
            searchField.clear();
        });

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.getChildren().addAll(searchField, searchBtn, refreshBtn);
        patientLayout.getChildren().addAll(patientTitle, spacing_1, searchBox, patientListBox);
    }

    private static void checkoutPage(VBox checkoutLayout) {

        HBox row1 = new HBox(20);
        VBox patientDataContainer = new VBox(10);

        // Font
        Font checkoutTitleFont = Font.font("Arial", FontWeight.BOLD, 25);
        Font checkouLabelFont = Font.font("Arial", 18);

        // Label and TextField of Checkout Page
        Label checkoutTitleLabel = new Label("Check Out Patient");
        checkoutTitleLabel.setFont(checkoutTitleFont);
        Label spacing = new Label("");
        Label phnLabel = new Label("Patient Health Number");
        phnLabel.setFont(checkouLabelFont);
        TextField phnField = new TextField();
        phnField.setFont(checkouLabelFont);
        phnField.setPrefWidth(1450);
        Button searchBtn = new Button("Search");
        searchBtn.setFont(checkouLabelFont);
        searchBtn
                .setOnAction(event -> getPatientData(phnField.getText(), checkoutLayout, patientDataContainer));
        CheckBox checkBox = new CheckBox("I had admitted the patient above is ready to check out");
        checkBox.setFont(checkouLabelFont);
        Button resetBtn = new Button("Reset");
        resetBtn.setFont(checkouLabelFont);
        resetBtn.setOnAction(event -> {
            phnField.clear();
            patientDataContainer.getChildren().clear();
            checkoutLayout.getChildren().clear();
            checkoutPage(checkoutLayout);
        });

        // Checkout Button
        Button checkoutBtn = new Button("Check out");
        checkoutBtn.setOnAction(e -> {
            if (!checkBox.isSelected()) {
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Confirmation");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Please confirm patient checkout");
                confirmationAlert.showAndWait();
                return;
            }

            String phn = phnField.getText();

            try {
                // Please check your port before run it!!!
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
                Statement stmt = con.createStatement();

                String deletePatientQuery = "DELETE FROM patient WHERE PHEALTHNUMBER = '" + phn + "'";
                String deleteBedQuery = "UPDATE bed AS b " +
                        "INNER JOIN ward AS w " +
                        "SET b.PHEALTHNUMBER = NULL, b.Description = NULL, b.assigned_Doctor = NULL, b.assigned_Nurse = NULL, w.occupied_bed = w.occupied_bed - 1 "
                        +
                        "WHERE b.PHEALTHNUMBER = '" + phn + "' AND w.WARDNAME = b.WardType";

                int rowsAffectedPatient = stmt.executeUpdate(deletePatientQuery);
                int rowsAffectedBed = stmt.executeUpdate(deleteBedQuery);

                if (rowsAffectedPatient > 0 && rowsAffectedBed > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Check out");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Checkout successfully!");
                    successAlert.showAndWait();
                    // Clear the text fields
                    phnField.setText("");
                    checkBox.setSelected(false);
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Information");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Checkout failed! Please confirm the Patient Health Number is existing.");
                    errorAlert.showAndWait();
                }

                stmt.close();
                con.close();
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
                ex.printStackTrace();
            }
        });

        row1.getChildren().addAll(phnField, searchBtn, resetBtn);
        checkoutLayout.getChildren().addAll(checkoutTitleLabel, spacing, phnLabel, row1, patientDataContainer,
                checkBox, checkoutBtn);
    }

    private static void logout(Tab logoutTab, Stage mainPageStage) {
        logoutTab.setOnSelectionChanged(event -> {
            if (logoutTab.isSelected()) {
                // Close the main page stage
                mainPageStage.close();
                // Open the login page
                HMS hms = new HMS();
                Stage loginPageStage = new Stage();
                loginPageStage.setTitle("Log In");
                hms.start(loginPageStage);
            }
        });
    }

    private static void getCurrentTime(Label timeLabel, Label dateWeek) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Date currentDate = new Date();

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String time = timeFormat.format(currentDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String date = dateFormat.format(currentDate);

                    SimpleDateFormat dayFormat = new SimpleDateFormat("E");
                    String dayOfWeek = dayFormat.format(currentDate);

                    Platform.runLater(() -> {
                        timeLabel.setText(time);
                        dateWeek.setText(date + ", " + dayOfWeek);
                    });
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private static void repopulateTable(TableView<ObservableList<String>> table, String searchInfo) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();

            String query = "SELECT p.PATIENTID, p.FIRSTNAME, p.LASTNAME, p.DATEOFBIRTH, p.PHONENUMBER, p.PIDENTIFICATIONNUMBER, p.PHEALTHNUMBER, p.ADDRESS, p.POSTALCODE, p.CITY, p.COUNTRY, p.isHospitalized, b.WardType, b.BEDID, b.assigned_Doctor, b.assigned_Nurse FROM patient AS p LEFT JOIN bed AS b ON p.PHEALTHNUMBER = b.PHEALTHNUMBER";

            if (!searchInfo.isEmpty()) {
                query += " WHERE p.LASTNAME = '" + searchInfo + "' OR p.PHEALTHNUMBER = '" + searchInfo
                        + "' OR p.PATIENTID = '" + searchInfo + "'";
            }

            query += " ORDER BY p.PATIENTID ASC";

            ResultSet rs = stmt.executeQuery(query);

            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            table.getColumns().clear();
            for (int i = 1; i <= columnCount; i++) {
                final int columnIndex = i;
                String columnName = getColumnName(metaData.getColumnName(i));
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(
                        cellData -> new ReadOnlyStringWrapper(cellData.getValue().get(columnIndex - 1)));

                // Center-align the cell content
                column.setCellFactory(tc -> {
                    TableCell<ObservableList<String>, String> cell = new TableCell<ObservableList<String>, String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty ? null : item);
                            setAlignment(Pos.CENTER);
                        }
                    };
                    return cell;
                });
                column.setSortable(false);
                table.getColumns().add(column);
            }

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }

            table.setItems(data);
            table.prefHeightProperty()
                    .bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.0)));

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Error: " + ex.getMessage());
            errorAlert.showAndWait();
            ex.printStackTrace();
        }
    }

    private static String getColumnName(String columnName) {
        switch (columnName) {
            case "PATIENTID":
                return "Patient ID";
            case "FIRSTNAME":
                return "First Name";
            case "LASTNAME":
                return "Last Name";
            case "DATEOFBIRTH":
                return "Date of Birth";
            case "PHONENUMBER":
                return "Phone Number";
            case "PIDENTIFICATIONNUMBER":
                return "P.IC";
            case "PHEALTHNUMBER":
                return "P.HN";
            case "ADDRESS":
                return "Address";
            case "POSTALCODE":
                return "Postal Code";
            case "CITY":
                return "City";
            case "COUNTRY":
                return "Country";
            case "isHospitalized":
                return "Hospitalized";
            case "WardType":
                return "Ward Type";
            case "BEDID":
                return "Bed ID";
            case "assigned_Doctor":
                return "Doctor In Charge";
            case "assigned_Nurse":
                return "Nurse In Charge";
            default:
                return columnName;
        }
    }

    private static void getPatientData(String phn, VBox consultationLayout, VBox patientDataContainer) {
        // Font
        Font consultationLabelFont = Font.font("Arial", 20);

        patientDataContainer.getChildren().clear();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            String query = "SELECT FIRSTNAME, LASTNAME, DATEOFBIRTH, PIDENTIFICATIONNUMBER FROM patient";

            if (!phn.isEmpty()) {
                query += " WHERE PHEALTHNUMBER = '" + phn + "'";
            }

            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                if (!phn.isEmpty()) {
                    HBox row_1 = new HBox(20);
                    Label firstNameLabel = new Label("First Name: " + rs.getString("FIRSTNAME"));
                    firstNameLabel.setFont(consultationLabelFont);
                    Label lastNameLabel = new Label("Last Name: " + rs.getString("LASTNAME"));
                    lastNameLabel.setFont(consultationLabelFont);
                    Label dateOfBirthLabel = new Label("Date of Birth: " + rs.getString("DATEOFBIRTH"));
                    dateOfBirthLabel.setFont(consultationLabelFont);
                    Label PIdentificationNumLabel = new Label(
                            "Patient Identification Number: " + rs.getString("PIDENTIFICATIONNUMBER"));
                    PIdentificationNumLabel.setFont(consultationLabelFont);

                    VBox column_1 = new VBox(20);
                    VBox column_2 = new VBox(20);
                    column_1.getChildren().addAll(firstNameLabel, dateOfBirthLabel);
                    column_2.getChildren().addAll(lastNameLabel, PIdentificationNumLabel);
                    row_1.getChildren().addAll(column_1, column_2);

                    patientDataContainer.setPadding(new Insets(20, 0, 20, 0));
                    patientDataContainer.getChildren().add(row_1);
                }
            } else {
                HBox row_1 = new HBox(20);
                Label contentLabel = new Label("The PHN is not found.");
                contentLabel.setFont(consultationLabelFont);
                row_1.getChildren().add(contentLabel);
                patientDataContainer.getChildren().add(contentLabel);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Error: " + ex.getMessage());
            errorAlert.showAndWait();
            ex.printStackTrace();
        }
    }

    private static void wardTypeDropDown(ComboBox<String> wardType) {
        try {
            // Connect to MySQL database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT WARDNAME FROM ward");

            ArrayList<String> wardTypeList = new ArrayList<>();
            while (rs.next()) {
                String wardName = rs.getString("WARDNAME");
                wardTypeList.add(wardName);
            }

            wardType.setItems(FXCollections.observableArrayList(wardTypeList));

            rs.close();
            stmt.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Error: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private static void availableBed(String wardType, TextField bedIDTextField, TextField assignedDoctorTextField,
            TextField assignedNurseTextField) {
        try {
            // Please check your port before run it!!!
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
            Statement stmt = con.createStatement();
            Statement stmt1 = con.createStatement();
            Statement stmt2 = con.createStatement();
            String query = "SELECT BEDID FROM bed";
            String query1 = "SELECT FIRSTNAME, LASTNAME FROM staff";
            String query2 = "SELECT FIRSTNAME, LASTNAME FROM staff";

            if (wardType != null) {
                query += " WHERE WardType = '" + wardType + "' AND PHealthNumber IS NULL LIMIT 1";
                query1 += " WHERE EMPLOYEETYPE = 'Doctor' AND ward_InCharge = '" + wardType + "'";
                query2 += " WHERE EMPLOYEETYPE = 'Nurse' AND ward_InCharge = '" + wardType + "'";
            }

            ResultSet rs = stmt.executeQuery(query);
            ResultSet rs1 = stmt1.executeQuery(query1);
            ResultSet rs2 = stmt2.executeQuery(query2);

            if (rs.next()) {
                String bedID = rs.getString("BEDID");
                bedIDTextField.setText(bedID);
            } else {
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Bed Full");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("The bed of the ward is full!");
                confirmationAlert.showAndWait();
                return;
            }
            if (rs1.next()) {
                String DocInCharge = rs1.getString("LASTNAME");
                String DocInCharge1 = rs1.getString("FIRSTNAME");
                assignedDoctorTextField.setText(DocInCharge + " " + DocInCharge1);
            } else {
                assignedDoctorTextField.setText("");
            }
            if (rs2.next()) {
                String NurInCharge = rs2.getString("LASTNAME");
                String NurInCharge1 = rs2.getString("FIRSTNAME");
                assignedNurseTextField.setText(NurInCharge + " " + NurInCharge1);
            } else {
                assignedNurseTextField.setText("");
            }
            rs.close();
            rs1.close();
            rs2.close();
            stmt.close();
            stmt1.close();
            stmt2.close();
            con.close();
        } catch (Exception ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Error: " + ex.getMessage());
            errorAlert.showAndWait();
            ex.printStackTrace();
        }
    }

    private static void assignBed(VBox consultationLayout, TextField phn, TextArea desc, String wardType,
            TextField bedIDTextField, TextField assignedDoctorTextField, TextField assignedNurseTextField) {

        String patientHealthNumber = phn.getText();
        String description = desc.getText();
        String bedID = bedIDTextField.getText();
        String assignedDoctor = assignedDoctorTextField.getText();
        String assignedNurse = assignedNurseTextField.getText();

        if (!patientHealthNumber.isEmpty() || !description.isEmpty()) {
            try {
                // Please check your port before running it!!!
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hms", "root", "");
                Statement stmt = con.createStatement();

                String query = "UPDATE bed AS b " +
                        "INNER JOIN patient AS p INNER JOIN ward AS w " +
                        "SET b.PHEALTHNUMBER = '" + patientHealthNumber + "', " +
                        "    b.Description = '" + description + "', " +
                        "    b.assigned_Doctor = '" + assignedDoctor + "', " +
                        "    b.assigned_Nurse = '" + assignedNurse + "', " +
                        "    p.isHospitalized = true, " +
                        "    p.WARDTYPE = '" + wardType + "', " +
                        "    w.occupied_bed = w.occupied_bed + 1 " +
                        "WHERE b.WardType = '" + wardType + "' " +
                        "  AND b.BEDID = '" + bedID + "' " +
                        "  AND p.PHEALTHNUMBER = '" + patientHealthNumber + "' " +
                        "  AND w.WARDNAME = '" + wardType + "'";

                int rowsAffected = stmt.executeUpdate(query);

                if (rowsAffected > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Bed Assignment");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Bed assigned successfully.");
                    successAlert.showAndWait();

                    consultationLayout.getChildren().clear();
                    consultationPage(consultationLayout);
                }

                stmt.close();
                con.close();
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Error: " + ex.getMessage());
                errorAlert.showAndWait();
                ex.printStackTrace();
            }
        }
    }
}