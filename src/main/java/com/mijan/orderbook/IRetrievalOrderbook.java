package com.mijan.orderbook;

import java.util.List;

public interface IRetrievalOrderbook extends IOrderEntryOrderbook {

    List<OrderbookEntry> askOrders();

    List<OrderbookEntry> bidOrders();

}
