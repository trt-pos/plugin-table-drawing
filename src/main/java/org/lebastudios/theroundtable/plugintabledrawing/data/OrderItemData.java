package org.lebastudios.theroundtable.plugintabledrawing.data;

import org.lebastudios.theroundtable.plugincashregister.cash.OrderItem;

import java.math.BigDecimal;

public class OrderItemData
{
    public String qty;
    
    // Product data:
    public ProductData productData;
    
    public static OrderItemData fromOrderItem(OrderItem orderItem)
    {
        OrderItemData data = new OrderItemData();
        
        data.qty = orderItem.getQuantity().toString();
        data.productData = ProductData.fromProduct(orderItem.intoProduct());
        
        return data;
    }
    
    public OrderItem intoOrderItem()
    {
        return new OrderItem(productData.intoProduct(), new BigDecimal(qty));
    }
}
