package org.lebastudios.theroundtableplugins.floorplan.data;

import org.lebastudios.theroundtableplugins.cr.cash.Order;
import org.lebastudios.theroundtableplugins.cr.cash.OrderItem;

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
    
    public Order intoOrder(String tableName)
    {
        Order order = new Order();
        order.setOrderName(tableName);
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (OrderItemData orderItemData : orderItemsData)
        {
            orderItems.add(orderItemData.intoOrderItem());
        }
        
        order.getObservableOrderItems().addAll(orderItems);
        return order;
    }
}
