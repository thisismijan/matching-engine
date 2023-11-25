package com.mijan.orderbook;

import com.mijan.order.Order;

public interface IOrderEntryOrderbook extends IReadOnlyOrderbook {

    void addOrder(Order order);

    void removeOrder(Order order);

    void modifyOrder(Order order);

}
