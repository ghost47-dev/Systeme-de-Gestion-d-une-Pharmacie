package com.gestionpharmacie.controllers;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;

import com.gestionpharmacie.Globals;
import com.gestionpharmacie.Globals.Tab;
import com.gestionpharmacie.exceptions.ProductNotFoundException;
import com.gestionpharmacie.exceptions.ShipmentNotFoundException;
import com.gestionpharmacie.managers.ProductManager;
import com.gestionpharmacie.managers.SaleManager;
import com.gestionpharmacie.managers.ShipmentManager;
import com.gestionpharmacie.managers.UserManager;
import com.gestionpharmacie.model.Product;
import com.gestionpharmacie.model.Sale;
import com.gestionpharmacie.model.SaleProduct;
import com.gestionpharmacie.model.Shipment;
import com.gestionpharmacie.model.ShipmentGood;
import com.gestionpharmacie.model.Supplier;
import com.gestionpharmacie.model.User;
import com.gestionpharmacie.model.Client;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MainController {

    @FXML private HBox sidePanelContainer;
    @FXML private VBox sidePanel;
    @FXML private Button toggleButton;
    @FXML private VBox togglePanel;

    @FXML private Button saleHistoryBtn;
    @FXML private Button productsBtn;
    @FXML private Button shipmentsBtn;
    @FXML private Button suppliersBtn;
    @FXML private Button adminDashboardBtn;
    @FXML private Button totalRevenueBtn;
    @FXML private Button userCreationBtn;

    @FXML private VBox saleHistoryPage;
    @FXML private VBox productsPage;
    @FXML private VBox shipmentsPage;
    @FXML private VBox suppliersPage;
    @FXML private VBox totalRevenuePage;
    @FXML private VBox userCreationPage;
    @FXML private HBox editingBtns_product;
    @FXML private HBox editingBtns_shipment;

    @FXML private ListView<String> saleHistoryList;
    @FXML private ListView<String> productsList;
    @FXML private ListView<String> shipmentsList;
    @FXML private ListView<String> suppliersList;

    @FXML private Button editSupplierBtn;
    @FXML private Label revenueLabel;
    @FXML private VBox flashBox;
    @FXML private Label iconLabel;
    @FXML private Label messageLabel;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> privilegeComboBox;
    @FXML private Label usernameError;
    @FXML private Label passwordError;
    @FXML private Label confirmPasswordError;
    @FXML private Label privilegeError;
    @FXML private Button signUpButton;

    public enum MessageType {
        SUCCESS("✓", "flash-success"),
        ERROR("✗", "flash-error"),
        WARNING("⚠", "flash-warning"),
        INFO("ℹ", "flash-info");

        private final String icon;
        private final String styleClass;

        MessageType(String icon, String styleClass) {
            this.icon = icon;
            this.styleClass = styleClass;
        }
    }

    private boolean isPanelVisible = true;
    private static final double PANEL_WIDTH = 200.0;
    private Queue<String> messageQueue = new LinkedList<>();
    private boolean isShowingMessage = false;
    private Scene scene;

    public MainController() {
        scene = Globals.scenes.loadScene("main.fxml", this);
    }

    public void updatePrivilege(){
        String privilege = Globals.privilege;
        if (privilege.equals("employee")){
            userCreationBtn.setVisible(false);
            totalRevenueBtn.setVisible(false);
            suppliersBtn.setVisible(false);
        }
        else if (privilege.equals("admin")){
            userCreationBtn.setVisible(true);
            totalRevenueBtn.setVisible(true);
            suppliersBtn.setVisible(true);
        }
    }

    void show(){
        updatePrivilege();
        switch(Globals.currentTab) {
            case Tab.SALE:
                showSaleHistory();
                break;
            case Tab.STOCK:
                showProductsPage();
                break;
            case Tab.SHIPMENTS:
                showShipmentsPage();
                break;
            case Tab.SUPPLIERS:
                showSuppliersPage();
                break;
            case Tab.REVENUE:
                showTotalRevenuePage();
                break;
            case Tab.ACCOUNT:
                showUserCreationPage();
                break;
        }
        Globals.stage.setTitle("Pharmacy Stock Management");
        Globals.stage.setScene(scene);
    }

    @FXML
    private void toggleSidePanel() {
        double start = isPanelVisible ? PANEL_WIDTH : 0;
        double end   = isPanelVisible ? 0 : PANEL_WIDTH;

        Timeline timeline = new Timeline();

        KeyFrame startFrame = new KeyFrame(
                Duration.ZERO,
                new KeyValue(sidePanel.prefWidthProperty(), start),
                new KeyValue(sidePanel.minWidthProperty(), start),
                new KeyValue(sidePanel.maxWidthProperty(), start)
                );

        KeyFrame endFrame = new KeyFrame(
                Duration.millis(300),
                new KeyValue(sidePanel.prefWidthProperty(), end),
                new KeyValue(sidePanel.minWidthProperty(), end),
                new KeyValue(sidePanel.maxWidthProperty(), end)
                );

        timeline.getKeyFrames().addAll(startFrame, endFrame);
        timeline.play();

        toggleButton.setText(isPanelVisible ? "▶" : "◀");
        isPanelVisible = !isPanelVisible;
    }

    @FXML
    public void showSaleHistory() {
        Globals.currentTab = Tab.SALE;
        try {
            ProductManager productManager = Globals.managers.product;
            SaleManager saleManager = Globals.managers.sale;

            ArrayList<Integer> sales = saleManager.getSaleIds();

            ObservableList<String> items = FXCollections.observableArrayList();

            for (int sale_id :sales ){

                Sale sale = saleManager.fetchSale(sale_id);
                int client_id = sale.getClientId();
                Client client = saleManager.fetchClient(client_id);

                ArrayList<SaleProduct> saleproducts = saleManager.getSaleProducts(sale_id);

                String Products_Info = "";
                for (SaleProduct sp : saleproducts){
                    int p_id = sp.getProductId();
                    Product p = null;
                    try {
                        p = productManager.fetchProduct(p_id);
                    }
                    catch (ProductNotFoundException e){
                        e.printStackTrace();
                        return;
                    }
                    int p_quantity = sp.getQuantity();
                    double p_price = p.getPrice();
                    String p_name = p.getName();

                    Products_Info += "Product: " + p_name + " | Price: $"+ p_price +" | Quantity: "+p_quantity;
                    if (sp != saleproducts.getLast() )Products_Info += "\n";

                }

                items.add(
                        "Sale ID: "+ sale_id +"\n" +
                        client.toString() + "\n" +
                        Products_Info
                        );
            }

            saleHistoryList.getItems().setAll(items);
            saleHistoryList.refresh();

            showPage(saleHistoryPage);
            setActiveButton(saleHistoryBtn);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void addSale(ActionEvent event){
        Globals.controllers.addSale.show();
    }

    private void editProductRedirection(ActionEvent event , String product){
        Globals.controllers.editProduct.showEdit(product);
        Globals.controllers.editProduct.show();
    }

    @FXML
    private void removeProduct(String product){
        if (product == null) return;
        String tmp = product.substring(13,product.indexOf("\n", 0)).strip();
        int id = 0;
        try {
            id = Integer.parseInt(tmp);
        }
        catch(NumberFormatException e) {
            System.out.println("error while parsing id ");
        }
        ProductManager pm = Globals.managers.product;
        try {
            pm.deleteProduct(id);
            System.out.println("product deleted !");
            productsList.getItems().remove(product);
            editingBtns_product.getChildren().remove(1);
            editingBtns_product.getChildren().remove(1);
            productsList.refresh();
        }
        catch (ProductNotFoundException e){
            return ;
        }

    }
    public void showMessage(String message, MessageType type) {
        showMessage(message, type, 3000);
    }

    public void showMessage(String message, MessageType type, int durationMs) {
        messageLabel.setText(message);
        iconLabel.setText(type.icon);

        flashBox.getStyleClass().removeIf(s -> s.startsWith("flash-"));
        flashBox.getStyleClass().add("flash-message");
        flashBox.getStyleClass().add(type.styleClass);

        flashBox.setOpacity(0);
        flashBox.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), flashBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        if (durationMs > 0) {
            PauseTransition pause = new PauseTransition(Duration.millis(durationMs));
            pause.setOnFinished(e -> hide());
            pause.play();
        }
    }

    private void hide() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), flashBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> flashBox.setVisible(false));
        fadeOut.play();
    }
    @FXML
    public void showProductsPage() {
        Globals.currentTab = Tab.STOCK;
        try {
            ProductManager productManager = Globals.managers.product;

            ArrayList<Product> products = productManager.viewStock();

            ObservableList<String> items = FXCollections.observableArrayList();

            for (Product p : products){
                String name = p.getName();
                int id = p.getId();
                double price = p.getPrice();
                int quantity = p.getQuantity();

                items.add(
                        "Product id : " + id + "\n" +
                        "Product name : " + name + " | Product price : " + price + " | Product quantity " + quantity
                        );

            }

            productsList.getItems().setAll(items);
            productsList.refresh();

            productsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = productsList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        if (editingBtns_product.getChildren().size() == 1){
                            Button editBtn = new Button();
                            editBtn.getStyleClass().add("edit-btn");
                            editBtn.setText("edit");
                            editBtn.setOnAction(editEvent -> {
                                editProductRedirection(editEvent,selected);
                            });

                            Button removeBtn = new Button();
                            removeBtn.getStyleClass().add("delete-btn");
                            removeBtn.setText("delete");
                            removeBtn.setOnAction(deleteEvent -> {
                                removeProduct(selected);
                            });

                            editingBtns_product.getChildren().addAll(editBtn,removeBtn);
                        }
                        if (editingBtns_product.getChildren().size() == 3){
                            Button editBtn = (Button)editingBtns_product.getChildren().get(1);
                            editBtn.setOnAction(editEvent -> {
                                editProductRedirection(editEvent,selected);
                            });
                            Button removeBtn = (Button)editingBtns_product.getChildren().getLast();
                            removeBtn.setOnAction(deleteEvent -> {
                                removeProduct(selected);
                            });
                        }

                    }
                }
            });

            showPage(productsPage);
            setActiveButton(productsBtn);

            ArrayList<Product> lowStockProducts = productManager.lowStockAlert();
            String errorMessage;
            for (Product p : lowStockProducts){
                errorMessage = p.getId() + ": " + p.getName() + " stock is low !";
                messageQueue.add(errorMessage);
            }
            showNextMessage();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void showNextMessage() {
        if (isShowingMessage || messageQueue.isEmpty()) {
            return;
        }

        isShowingMessage = true;
        String message = messageQueue.poll();
        showMessage(message, MessageType.ERROR);

        PauseTransition pause = new PauseTransition(Duration.millis(3500));
        pause.setOnFinished(e -> {
            isShowingMessage = false;
            showNextMessage();
        });
        pause.play();
    }
    @FXML
    private void addProduct(ActionEvent event){
        Globals.controllers.addProduct.show();
    }

    @FXML
    private void addShipment(ActionEvent event){
        Globals.controllers.addShipment.show();
    }

    @FXML
    public void showShipmentsPage() {
        Globals.currentTab = Tab.SHIPMENTS;
        try  {
            ShipmentManager sm = Globals.managers.shipment;
            ArrayList<Shipment> shipments = sm.fetchShipments();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Shipment s : shipments){
                int supplier_id = s.getSupplierId();
                Date requestDate = s.getRequestDate();
                Date receivalDate = s.getRecievalDate();
                boolean isReceived = s.isReceived();

                Supplier supplier = sm.fetchSupplier(supplier_id);
                String supplierName = supplier.getName();
                int supplierPhone = supplier.getNumerotel();

                ArrayList<ShipmentGood> shipmentGood ;

                shipmentGood = sm.fetchShipmentGood(s.getId());

                String shipmentGoodInfo = "";
                ProductManager pm = Globals.managers.product;
                for (ShipmentGood sg : shipmentGood){
                    double price = sg.getPrice();
                    int quantity = sg.getQuantity();

                    Product product =null ;
                    try {
                        product = pm.fetchProduct(sg.getProductId());
                    }
                    catch (ProductNotFoundException e){
                        e.printStackTrace();
                        return;
                    }
                    String name = product.getName();
                    shipmentGoodInfo += "Product id : " + product.getId() + "\n" +
                        "Product name : " + name + " | Product price : " + price + "$" + " | Product quantity " + quantity + "\n" ;
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                if (isReceived)
                    items.add(
                            "Shipment id : " + s.getId() + "\n" +
                            "Supplier id : " + supplier_id + "\n" +
                            "Supplier name : " + supplierName + " | Supplier phone : " + supplierPhone + "\n" +
                            shipmentGoodInfo +
                            "Date of request : " + formatter.format(requestDate) + " | Date of arrival : " + formatter.format(receivalDate) + "\n" +
                            "Shipment is received ✓"
                            );
                else
                    items.add(
                            "Shipment id : " + s.getId() + "\n" +
                            "Supplier id : " + supplier_id + "\n" +
                            "Supplier name : " + supplierName + " | Supplier phone : " + supplierPhone + "\n" +
                            shipmentGoodInfo +
                            "Date of request : " + formatter.format(requestDate) + " | Date of arrival : " + formatter.format(receivalDate) + "\n" +
                            "Shipment is not received 𐄂"
                            );
            }

            shipmentsList.getItems().setAll(items);
            shipmentsList.refresh();

            shipmentsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = shipmentsList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        if (editingBtns_shipment.getChildren().size() == 1){
                            Button editBtn = new Button();
                            editBtn.getStyleClass().add("edit-btn");
                            editBtn.setText("edit");
                            editBtn.setOnAction(editEvent -> {
                                editShipmentRedirection(editEvent,selected);
                            });

                            Button cancelBtn = new Button();
                            cancelBtn.getStyleClass().add("delete-btn");
                            cancelBtn.setText("cancel");
                            cancelBtn.setOnAction(deleteEvent -> {
                                removeShipment(selected);
                            });

                            Button receivedBtn = new Button();
                            receivedBtn.getStyleClass().add("add-btn");
                            receivedBtn.setText("mark received");
                            receivedBtn.setOnAction(receivedEvent -> {
                                markShipmentReceived(selected);
                            });

                            editingBtns_shipment.getChildren().addAll(editBtn,cancelBtn,receivedBtn);
                        }
                        if (editingBtns_shipment.getChildren().size() == 3){
                            Button editBtn = (Button)editingBtns_shipment.getChildren().get(1);
                            editBtn.setOnAction(editEvent -> {
                                editShipmentRedirection(editEvent,selected);
                            });
                            Button removeBtn = (Button)editingBtns_shipment.getChildren().getLast();
                            removeBtn.setOnAction(deleteEvent -> {
                                removeShipment(selected);
                            });
                            Button receivedBtn = (Button)editingBtns_shipment.getChildren().getLast();
                            receivedBtn.setOnAction(receivedEvent -> {
                                markShipmentReceived(selected);
                            });
                        }

                    }
                }
            });

            showPage(shipmentsPage);
            setActiveButton(shipmentsBtn);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void removeShipment(String shipment){
        if (shipment == null) return ;
        Pattern p = Pattern.compile("Shipment id : (\\d+)\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+");
        Matcher matcher = p.matcher(shipment);

        if (!matcher.find()){
            throw new IllegalArgumentException();
        }

        int shipment_id = Integer.parseInt(matcher.group(1));
        ShipmentManager sm = Globals.managers.shipment;
        try {
            sm.cancelShipment(shipment_id);
            shipmentsList.getItems().remove(shipment);
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            shipmentsList.refresh();
        }
        catch (ShipmentNotFoundException e){
            return ;
        }
    }

    private void editShipmentRedirection(ActionEvent event , String shipment){
        Globals.controllers.editShipment.showEdit(shipment);
        Globals.controllers.editShipment.show();
    }
    @FXML
    private void markShipmentReceived(String shipment){
        if (shipment == null) return ;
        Pattern p = Pattern.compile("Shipment id : (\\d+)\\n.+\\n.+\\n.+\\n.+\\n.+\\n.+");
        Matcher matcher = p.matcher(shipment);

        if (!matcher.find()){
            throw new IllegalArgumentException();
        }

        int shipment_id = Integer.parseInt(matcher.group(1));

        ShipmentManager sm = Globals.managers.shipment;
        try {
            sm.receiveShipment(
                    shipment_id,
                    Date.from(
                        LocalDate.now()
                        .atStartOfDay( ZoneId.systemDefault() )
                        .toInstant()
                        )
                    );
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            shipmentsList.refresh();
            showShipmentsPage();
        }
        catch (ShipmentNotFoundException e){
            return ;
        } catch (ProductNotFoundException e) {
            return ;
        }

    }

    private void editSupplierRedirection(ActionEvent event , String supplier){
        Globals.controllers.editSupplier.showEdit(supplier);
        Globals.controllers.editSupplier.show();
    }

    @FXML
    public void showSuppliersPage() {
        Globals.currentTab = Tab.SUPPLIERS;
        try {
            suppliersList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = suppliersList.getSelectionModel().getSelectedItem();

                    if (selected != null){
                        if (!editSupplierBtn.isVisible()){
                            editSupplierBtn.setVisible(true);
                            editSupplierBtn.setOnAction(editEvent -> {
                                editSupplierRedirection(editEvent , selected);
                            });
                        }
                        else {
                            editSupplierBtn.setOnAction(editEvent -> {
                                editSupplierRedirection(editEvent , selected);
                            });
                        }
                    }
                }
            });

            ObservableList<String> items = FXCollections.observableArrayList();

            ShipmentManager sm = Globals.managers.shipment;
            ArrayList<String> suppliers = sm.viewSuppliersPerfermance();
            for (String s : suppliers){
                items.add(s);
            }

            suppliersList.getItems().setAll(items);
            suppliersList.refresh();
            showPage(suppliersPage);
            setActiveButton(suppliersBtn);
        }
        catch (Exception e){
            return ;
        }

    }

    @FXML
    private void showTotalRevenuePage(){
        Globals.currentTab = Tab.REVENUE;
        try {
            double totalRevenue;
            ProductManager pm = Globals.managers.product;
            SaleManager sm = Globals.managers.sale;
            totalRevenue = sm.getTotalRevenue();

            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            revenueLabel.setText(nf.format(totalRevenue) + " TND");
            setActiveButton(totalRevenueBtn);
            showPage(totalRevenuePage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    private void handleSignUp(){
        clearAllErrors();
        boolean isValid = true;
        UserManager userManager = Globals.managers.user;

        if (usernameField.getText().trim().isEmpty()) {
            usernameField.getStyleClass().add("error");
            showError( usernameError, "Username is required");
            isValid = false;
        } else if (usernameField.getText().trim().length() < 3) {
            usernameField.getStyleClass().add("error");
            showError(usernameError, "Username must be at least 3 characters");
            isValid = false;
        } else if (userManager.fetchUser(usernameField.getText().trim()) != null){
            usernameField.getStyleClass().add("error");
            showError(usernameError, "Username already taken");
            isValid = false;
        }

        if (passwordField.getText().isEmpty()) {
            passwordField.getStyleClass().add("error");
            showError(passwordError, "Password is required");
            isValid = false;
        } else if (passwordField.getText().length() < 6) {
            passwordField.getStyleClass().add("error");
            showError( passwordError, "Password must be at least 6 characters");
            isValid = false;
        }

        if (confirmPasswordField.getText().isEmpty()) {
            confirmPasswordField.getStyleClass().add("error");
            showError( confirmPasswordError, "Please confirm your password");
            isValid = false;
        } else if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            confirmPasswordField.getStyleClass().add("error");
            showError( confirmPasswordError, "Passwords do not match");
            isValid = false;
        }

        if (privilegeComboBox.getValue() == null) {
            privilegeComboBox.getStyleClass().add("error");
            showError( privilegeError, "Please select a privilege level");
            isValid = false;
        }

        if (isValid) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String privilege = privilegeComboBox.getValue();

            userManager.addUser(new User(username, password, privilege));

            System.out.println("Sign Up Successful!");

            clearFields();
        }
    }

    private void showError( Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearError( Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void clearAllErrors() {
        usernameField.getStyleClass().remove("error");
        clearError(usernameError);
        passwordField.getStyleClass().remove("error");
        clearError(passwordError);
        confirmPasswordField.getStyleClass().remove("error");
        clearError(confirmPasswordError);
        privilegeComboBox.getStyleClass().remove("error");
        clearError( privilegeError);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        privilegeComboBox.setValue(null);
    }
    @FXML
    private void showUserCreationPage(){
        Globals.currentTab = Tab.ACCOUNT;
        ObservableList<String> items = FXCollections.observableArrayList(
                "admin",
                "employee"
                );
        privilegeComboBox.setItems(items);
        setActiveButton(userCreationBtn);
        showPage(userCreationPage);
    }

    private void showPage(VBox pageToShow) {
        if (editingBtns_product.getChildren().size() == 3 ){
            editingBtns_product.getChildren().removeLast();
            editingBtns_product.getChildren().removeLast();
        }

        if (editingBtns_shipment.getChildren().size() == 4 ){
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
            editingBtns_shipment.getChildren().removeLast();
        }
        editSupplierBtn.setVisible(false);
        saleHistoryPage.setVisible(false);
        productsPage.setVisible(false);
        shipmentsPage.setVisible(false);
        suppliersPage.setVisible(false);
        totalRevenuePage.setVisible(false);
        userCreationPage.setVisible(false);
        pageToShow.setVisible(true);
    }

    private void setActiveButton(Button activeBtn) {
        saleHistoryBtn.getStyleClass().remove("nav-btn-active");
        productsBtn.getStyleClass().remove("nav-btn-active");
        shipmentsBtn.getStyleClass().remove("nav-btn-active");
        suppliersBtn.getStyleClass().remove("nav-btn-active");
        totalRevenueBtn.getStyleClass().remove("nav-btn-active");
        userCreationBtn.getStyleClass().remove("nav-btn-active");

        if (!activeBtn.getStyleClass().contains("nav-btn-active")) {
            activeBtn.getStyleClass().add("nav-btn-active");
        }
    }
}
