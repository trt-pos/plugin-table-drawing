package org.lebastudios.theroundtable.plugintabledrawing.data;

import com.sun.javafx.collections.ObservableListWrapper;
import org.lebastudios.theroundtable.plugincashregister.cash.Order;
import org.lebastudios.theroundtable.plugincashregister.cash.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderData
{
    public List<OrderItemData> orderItemsData = new ArrayList<>();
    
    public static OrderData fromOrder(Order order)
    {
        OrderData orderData = new OrderData();
        
        for (OrderItem orderItem : order.getOrderItems())
        {
            orderData.orderItemsData.add(OrderItemData.fromOrderItem(orderItem));
        }
        
        return orderData;
    }
    
    public Order intoOrder(RoomObjData roomObjData)
    {
        Order order = new Order();
        order.setOrderName(roomObjData.tableName);
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemData orderItemData : orderItemsData)
        {
            orderItems.add(orderItemData.intoOrderItem());
        }
        
        order.setOrderItems(new ObservableListWrapper<>(orderItems));
        return order;
    }
}
