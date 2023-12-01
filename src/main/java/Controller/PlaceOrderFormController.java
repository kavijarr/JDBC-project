package Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import dto.CustomerDto;
import dto.ItemDto;
import dto.Orderdto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelimpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {
    public AnchorPane pane;
    public ComboBox customerBox;
    public ComboBox itemBox;
    public ComboBox cmbID;
    public ComboBox cmbItemCode;
    public JFXTextField txtName;
    public JFXTextField txtDesc;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public JFXTreeTableView<OrderTm> tblOrders;
    public TreeTableColumn colCode;
    public TreeTableColumn colDesc;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;
    public TreeTableColumn colOption;
    public Label lblTotal;
    private List<Orderdto> dtoList;

    public void BackButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<CustomerDto> customers = new ArrayList<>();
    private List<ItemDto> items = new ArrayList<>();

    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel = new ItemModelimpl();
    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();
    public void initialize(){
        loadCustomerIds();
        loadItemCodes();

        cmbID.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, id) -> {
            for (CustomerDto dto:customers) {
                if(dto.getId().equals(id)){
                    txtName.setText(dto.getName());
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, code) -> {
            for (ItemDto dto:items) {
                if(dto.getCode().equals(code)){
                    txtDesc.setText(dto.getDesc());
                    txtUnitPrice.setText(String.format("%.2f",dto.getUnitPrice()));
                }
            }
        });

        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));
    }

    private void loadItemCodes() {
        try {
            items = itemModel.allItems();
            ObservableList list = FXCollections.observableArrayList();
            for (ItemDto dto: items) {
                list.add(dto.getCode());
            }
            cmbItemCode.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerIds() {
        try {
            customers = customerModel.allCustomers();
            ObservableList list = FXCollections.observableArrayList();
            for (CustomerDto dto: customers) {
                list.add(dto.getId());
            }
            cmbID.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void addToCartOnAction(ActionEvent actionEvent) {
        try {
            double amount = itemModel.getItem(cmbItemCode.getValue().toString()).getUnitPrice() * Integer.parseInt(txtQty.getText());
            JFXButton btn = new JFXButton("Delete");
            OrderTm tm = new OrderTm(
                    cmbItemCode.getValue().toString(),
                    txtDesc.getText(),
                    Integer.parseInt(txtQty.getText()),
                    amount,
                    btn
            );
            boolean isExist = false;
            for (OrderTm order:tmList) {
                if(order.getCode().equals(tm.getCode())){
                    order.setQty(order.getQty()+tm.getQty());
                    order.setAmount(order.getAmount()+tm.getAmount());
                    isExist = true;
                }
            }
            if (!isExist){
                tmList.add(tm);
            }

            TreeItem<OrderTm> treeObject = new RecursiveTreeItem<OrderTm>(tmList, RecursiveTreeObject::getChildren);
            tblOrders.setRoot(treeObject);
            tblOrders.setShowRoot(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {

    }
}
