package model;

import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.Orderdto;

import java.sql.SQLException;
import java.util.List;

public interface OrderModel {
    boolean saveOrder(Orderdto dto) throws SQLException, ClassNotFoundException;

    Orderdto lastOrder() throws SQLException, ClassNotFoundException;

    boolean removeFromStock(List<OrderDetailsDto> orders,List<ItemDto> itemList) throws SQLException, ClassNotFoundException;
}


