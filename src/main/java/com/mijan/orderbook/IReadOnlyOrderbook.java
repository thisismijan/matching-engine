package com.mijan.orderbook;

public interface IReadOnlyOrderbook {

    boolean containsOrder(long orderId);

    OrderbookSpread getSpread();

    int count();

}
