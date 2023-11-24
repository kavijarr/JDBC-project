package model.impl;

import db.DBConection;
import dto.CustomerDto;
import model.CustomerModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerModelImpl implements CustomerModel {
    @Override
    public boolean saveCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO customer VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, dto.getId());
        pstm.setString(2, dto.getName());
        pstm.setString(3, dto.getAddress());
        pstm.setDouble(4, dto.getSalary());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateCustomer(CustomerDto dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE customer SET name=?,address=?,salary=? WHERE id=?";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,dto.getName());
        pstm.setString(2,dto.getAddress());
        pstm.setDouble(3,dto.getSalary());
        pstm.setString(4,dto.getId());
        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM customer WHERE id=?";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        return pstm.executeUpdate()>0;
    }

    @Override
    public List<CustomerDto> allCustomers() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM customer";
        List<CustomerDto> list = new ArrayList<>();
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        while(resultSet.next()){
            list.add(new CustomerDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            ));
        }
        return list;
    }

    @Override
    public List<CustomerDto> searchCustomer(String id) throws SQLException, ClassNotFoundException {
        List<CustomerDto> dtoList = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE id LIKE ? '%'";
        PreparedStatement pstm = DBConection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet rslt = pstm.executeQuery();
        while (rslt.next()){
            dtoList.add(new CustomerDto(rslt.getString(1),
                    rslt.getString(2),
                    rslt.getString(3),
                    rslt.getDouble(4)
            ));
        }
        return dtoList;
    }
}
