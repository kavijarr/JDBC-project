package model;

import dto.Orderdto;

import java.sql.SQLException;

public interface OrderModel {
    boolean saveOrder(Orderdto dto) throws SQLException, ClassNotFoundException;

    Orderdto lastOrder() throws SQLException, ClassNotFoundException;
}


