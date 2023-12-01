package model.impl;

import com.jfoenix.controls.JFXButton;
import db.DBConection;
import dto.ItemDto;
import model.ItemModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

public class ItemModelimpl implements ItemModel {
    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {

        List<ItemDto> itemList = new ArrayList<>();
        String sql = "SELECT * FROM item";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        ResultSet rslt = pstm.executeQuery();

        while (rslt.next()){
            itemList.add(new ItemDto(
                    rslt.getString(1),
                    rslt.getString(2),
                    rslt.getDouble(3),
                    rslt.getInt(4)
                    ));
        }
        return itemList;
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM item WHERE code=?";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, code);
        int rst = pstm.executeUpdate();
        return rst>0;
    }

    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,dto.getCode());
        pstm.setString(2, dto.getDesc());
        pstm.setDouble(3,dto.getUnitPrice());
        pstm.setInt(4,dto.getQty());
        int rst = pstm.executeUpdate();
        return rst>0;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET description=?, unitPrice=?, qtyOnHand=? WHERE code=?";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, dto.getDesc());
        pstm.setDouble(2,dto.getUnitPrice());
        pstm.setInt(3,dto.getQty());
        pstm.setString(4, dto.getCode());
        int rst = pstm.executeUpdate();
        return rst>0;
    }

    @Override
    public List<ItemDto> searchItem(String code) throws SQLException, ClassNotFoundException {
        List<ItemDto> dtoList = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE code LIKE ? '%'";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet rslt = pstm.executeQuery();
        while (rslt.next()){
            ItemDto dto = new ItemDto(rslt.getString(1),
                    rslt.getString(2),
                    rslt.getDouble(3),
                    rslt.getInt(4)
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public ItemDto getItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM item WHERE code=?";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()){
            return new ItemDto(
            resultSet.getString(1),
            resultSet.getString(2),
            resultSet.getDouble(3),
            resultSet.getInt(4)
            );
        }
        return null;
    }
}
