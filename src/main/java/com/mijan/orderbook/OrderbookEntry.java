package com.mijan.orderbook;

import com.mijan.order.Limit;
import com.mijan.order.Order;

public class OrderbookEntry {
    private Order currentOrder;
    private Limit parentLimit;
    private long ts;
    private OrderbookEntry next;
    private OrderbookEntry previous;

    public OrderbookEntry(Order currentOrder, Limit parentLimit) {
        this.currentOrder = currentOrder;
        this.parentLimit = parentLimit;
        ts = System.currentTimeMillis();

    }

    public Order currentOrder() {
        return currentOrder;
    }

    public OrderbookEntry next() {
        return next;
    }

    public OrderbookEntry previous() {
        return previous;
    }

    public void next(OrderbookEntry orderbookEntry) {
        this.next = orderbookEntry;
    }

    public void previous(OrderbookEntry orderbookEntry) {
        this.previous = orderbookEntry;
    }

    public Limit parentLimit() {
        return parentLimit;
    }
}
