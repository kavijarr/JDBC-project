package model.impl;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import db.DBConection;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.Orderdto;
import model.CustomerModel;
import model.ItemModel;
import model.OrderDetailsModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderModelimpl implements OrderModel {

    OrderDetailsModel orderDetailsModel = new OrderDetailsModelimpl();
    @Override
    public boolean saveOrder(Orderdto dto) throws SQLException {
        Connection connection=null;
        try {
            connection = DBConection.getInstance().getConnection();
            connection.setAutoCommit(false);
            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, dto.getOrderId());
            pstm.setString(2, dto.getDate());
            pstm.setString(3, dto.getCustId());
            if (pstm.executeUpdate() > 0) {
                boolean isDetailSaved = orderDetailsModel.saveOrderDetails(dto.getList());
                if (isDetailSaved) {
                    connection.commit();
                    return true;
                }
            }
        }catch (SQLException | ClassNotFoundException ex){
            connection.rollback();
        }finally {
            connection.setAutoCommit(true);
        }
        return false;
    }

    @Override
    public Orderdto lastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        if(resultSet.next()){
            return new Orderdto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null
            );
        }
        return null;
    }

    @Override
    public boolean removeFromStock(List<OrderDetailsDto> orders,List<ItemDto> itemList) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET qtyOnHand =? WHERE code =?";

        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
            for (OrderDetailsDto order:orders) {
                for (ItemDto item:itemList) {
                    if (order.getItemCode().equals(item.getCode())){
                        pstm.setInt(1,(item.getQty()-order.getQty()));
                        pstm.setString(2,order.getItemCode());
                        pstm.executeUpdate();
                    }
                }
            }
        return true;
    }

}
