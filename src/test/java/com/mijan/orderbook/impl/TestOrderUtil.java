package com.mijan.orderbook.impl;

import com.mijan.common.Side;
import com.mijan.order.Order;

public class TestOrderUtil {
    public static Order newTestOrder(int orderId, int quantity, int price, Side side) {
        return new Order(orderId, quantity, price, side, "test", "BTC/USD");
    }
}
