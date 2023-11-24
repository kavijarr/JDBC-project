package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import db.DBConection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import dto.CustomerDto;
import dto.tm.CustomerTm;
import model.CustomerModel;
import model.impl.CustomerModelImpl;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class CustomerFormController {

    public JFXTextField txtSearch;
    @FXML
    private JFXTextField txtID;

    @FXML
    private JFXTextField txtName;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtSalary;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TableColumn colID;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colSalary;

    @FXML
    private TableColumn colOption;

    private CustomerModel customerModel = new CustomerModelImpl();

    public void loadCustomerTable(){
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerModel.allCustomers();

            for(CustomerDto dto:dtoList){
                JFXButton btn = new JFXButton("Delete");
                CustomerTm c = new CustomerTm(dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });
                tmList.add(c);
            }
            tblCustomer.setItems(tmList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteCustomer(String id) {

        try {
            boolean isDeleted = customerModel.deleteCustomer(id);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
                loadCustomerTable();
            }else {
                new Alert(Alert.AlertType.ERROR,"Somthing went wrong").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveButtonOnAction(ActionEvent actionEvent) {
        try {
            boolean isSaved = customerModel.saveCustomer(new CustomerDto(txtID.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText())));
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
                loadCustomerTable();
                clearFields();
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Value Entry").show();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadButtonOnAction(ActionEvent actionEvent) {
        loadCustomerTable();
        tblCustomer.refresh();
        clearFields();
    }

    private void clearFields() {
        tblCustomer.refresh();
        txtID.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
        txtID.setEditable(true);
    }

    public void updateButtonOnAction(ActionEvent actionEvent) {
        try {
            boolean isUpdated = customerModel.updateCustomer(new CustomerDto(txtID.getText(), txtName.getText(), txtAddress.getText(), Double.parseDouble(txtSalary.getText())));
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadCustomerTable();
                clearFields();
            }
        } catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void initialize(){
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(CustomerTm newValue) {
        if(newValue !=null){
            txtID.setEditable(false);
            txtID.setText(newValue.getId());
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            txtSalary.setText(String.valueOf(newValue.getSalary()));
        }
    }

    public void BackButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) tblCustomer.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void SearchOnAction(KeyEvent keyEvent) {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        try {
            List<CustomerDto> dtoList = customerModel.searchCustomer(txtSearch.getText());
            for (CustomerDto dto : dtoList){
                JFXButton btn = new JFXButton("Delete");
                CustomerTm c  = new CustomerTm(dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteCustomer(c.getId());
                });
                tmList.add(c);
            }
            tblCustomer.setItems(tmList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
