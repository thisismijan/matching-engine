package com.mijan.orderbook;

import java.util.Set;

import com.mijan.order.Limit;
import com.mijan.order.Order;

public interface IMatchingOrderbook extends IRetrievalOrderbook {

    Set<Match> match(Order o, Limit limit);

}
