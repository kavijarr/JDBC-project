package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConection;
import dto.CustomerDto;
import dto.ItemDto;
import dto.tm.CustomerTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dto.tm.ItemTm;
import model.ItemModel;
import model.impl.ItemModelimpl;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class ItemFormController {

    @FXML
    private BorderPane pane;

    @FXML
    private JFXTextField txtPrice;

    @FXML
    private JFXTextField txtDesc;

    @FXML
    private JFXTextField txtCode;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private JFXTreeTableView<ItemTm> tblItem;

    @FXML
    private TreeTableColumn colCode;

    @FXML
    private TreeTableColumn colDesc;

    @FXML
    private TreeTableColumn colPrice;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colOption;
    private ItemModel itemModel = new ItemModelimpl();

    public void initialize(){
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
        loadItemTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });
    }

    private void setData(TreeItem<ItemTm> newValue) {
        if(newValue !=null){
            txtCode.setEditable(false);
            txtCode.setText(newValue.getValue().getCode());
            txtDesc.setText(newValue.getValue().getDesc());
            txtPrice.setText(String.valueOf(newValue.getValue().getUnitPrice()));
            txtQty.setText(String.valueOf(newValue.getValue().getQty()));
        }
    }

    private void loadItemTable() {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();

        try {
            List<ItemDto> itemList = itemModel.allItems();
            for (ItemDto dto : itemList){
                JFXButton btn = new JFXButton("Delete");
                ItemTm tm = new ItemTm(
                        dto.getCode(),
                        dto.getDesc(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteItem(tm.getCode());
                });
                tmList.add(tm);
            }

            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteItem(String code) {
        try {
            boolean isDeleted = itemModel.deleteItem(code);
            if (isDeleted) {
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadItemTable();
                clearFeilds();
            }else {
                new Alert(Alert.AlertType.ERROR,"Somthing went wrong").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void BackButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void SaveButtonOnAction(ActionEvent event) {
        ItemDto c = new ItemDto(txtCode.getText(),txtDesc.getText(),Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQty.getText()));
        try {
            boolean isSaved = itemModel.saveItem(c);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
                loadItemTable();
                clearFeilds();
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Value Entry").show();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void UpdateButtonOnAction(ActionEvent event) {
        ItemDto dto = new ItemDto(txtCode.getText(),txtDesc.getText(),Double.parseDouble(txtPrice.getText()),Integer.parseInt(txtQty.getText()));
        try {
            boolean isUpdated = itemModel.updateItem(dto);
            if(isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadItemTable();
                clearFeilds();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFeilds() {
        txtCode.setEditable(true);
        txtCode.clear();
        txtDesc.clear();
        txtPrice.clear();
        txtQty.clear();
        txtSearch.clear();
    }

    public void SearchOnAction(KeyEvent keyEvent) {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();
        try {
            List<ItemDto> dtoList = itemModel.searchItem(txtSearch.getText());
            for(ItemDto dto:dtoList){
                JFXButton btn = new JFXButton("DELETE");
                ItemTm tm = new ItemTm(dto.getCode(),
                        dto.getDesc(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteItem(tm.getCode());
                });
                tmList.add(tm);
            }
            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            tblItem.setRoot(treeItem);
            tblItem.setShowRoot(false);
            if(txtSearch==null){
                clearFeilds();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void BtnReloadOnAction(ActionEvent actionEvent) {
        loadItemTable();
        clearFeilds();
    }
}
