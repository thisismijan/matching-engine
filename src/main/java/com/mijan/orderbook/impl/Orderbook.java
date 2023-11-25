package com.mijan.orderbook.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

import com.mijan.common.Side;
import com.mijan.order.Limit;
import com.mijan.order.LimitComparator;
import com.mijan.order.Order;
import com.mijan.orderbook.IRetrievalOrderbook;
import com.mijan.orderbook.OrderbookEntry;
import com.mijan.orderbook.OrderbookSpread;

public class Orderbook implements IRetrievalOrderbook {

    private String tradingPair;
    protected Map<Long, OrderbookEntry> orders = new HashMap<>();
    protected ConcurrentSkipListSet<Limit> asks = new ConcurrentSkipListSet<>(
            new LimitComparator().askLimitComparator());
    protected ConcurrentSkipListSet<Limit> bids = new ConcurrentSkipListSet<>(
            new LimitComparator().bidLimitComparator());
    protected long askLiquidity = 0;
    protected long bidLiquidity = 0;

    public Orderbook(String tradingPair) {
        this.tradingPair = tradingPair;
    }

    @Override
    public void addOrder(Order order) {
        var limit = new Limit(order.getPrice());
        addOrder(order, limit, order.getSide() == Side.BID ? bids : asks);
    }

    private void addOrder(Order order, Limit limit, ConcurrentSkipListSet<Limit> limits) {

        if (!limits.contains(limit)) {
            limits.add(limit);
            limit.setHead(null);
            limit.setTail(null);
        }

        var _limit = limits.ceiling(limit);
        var orderbookEntry = new OrderbookEntry(order, limit);

        if (_limit.getHead() == null) {
            _limit.setHead(orderbookEntry);
            _limit.setTail(orderbookEntry);
        } else {
            var tail = _limit.getTail();
            tail.next(orderbookEntry);
            orderbookEntry.previous(tail);
            _limit.setTail(orderbookEntry);
        }

        orders.put(order.getOrderId(), orderbookEntry);
        updateLiquidity(order);
    }

    @Override
    public void removeOrder(Order order) {
        long orderId = order.getOrderId();
        if (orders.containsKey(orderId)) {
            OrderbookEntry orderbookEntry = orders.get(order.getOrderId());
            if (orderbookEntry.previous() != null && orderbookEntry.next() != null) {
                orderbookEntry.previous().next(orderbookEntry.next());
                orderbookEntry.next().previous(orderbookEntry.previous());
            } else if (orderbookEntry.previous() != null) {
                orderbookEntry.previous().next(null);
            } else if (orderbookEntry.next() != null) {
                orderbookEntry.next().previous(null);
            }
            if (orderbookEntry.parentLimit().getHead() == orderbookEntry
                    && orderbookEntry.parentLimit().getTail() == orderbookEntry) {
                orderbookEntry.parentLimit().setHead(null);
                orderbookEntry.parentLimit().setTail(null);
                if (order.getSide() == Side.BID)
                    bids.remove(orderbookEntry.parentLimit());
                else
                    asks.remove(orderbookEntry.parentLimit());
            } else if (orderbookEntry.parentLimit().getHead() == orderbookEntry) {
                orderbookEntry.parentLimit().setHead(orderbookEntry.next());
            } else if (orderbookEntry.parentLimit().getTail() == orderbookEntry) {
                orderbookEntry.parentLimit().setTail(orderbookEntry.previous());
            }
            orders.remove(orderId);
            updateLiquidity(orderbookEntry.currentOrder());
        }
    }

    @Override
    public void modifyOrder(Order order) {
        if (orders.containsKey(order.getOrderId())) {
            removeOrder(order);
            addOrder(order, new Limit(order.getPrice()), order.getSide() == Side.BID ? bids : asks);
        }

    }

    @Override
    public boolean containsOrder(long orderId) {
        return orders.containsKey(orderId);
    }

    @Override
    public OrderbookSpread getSpread() {
        Long bestAsk = null, bestBid = null;
        if (!asks.isEmpty() && !asks.last().isEmpty()) {
            bestAsk = asks.last().getPrice();
        }
        if (!bids.isEmpty() && !bids.first().isEmpty()) {
            bestBid = bids.first().getPrice();
        }
        return new OrderbookSpread(bestBid, bestAsk);
    }

    @Override
    public int count() {
        return orders.size();
    }

    @Override
    public List<OrderbookEntry> askOrders() {
        List<OrderbookEntry> orderbookEntries = new ArrayList<OrderbookEntry>();
        for (Limit askLimit : asks) {
            if (askLimit.isEmpty())
                continue;
            var limit = askLimit.getHead();
            while (limit != null) {
                orderbookEntries.add(limit);
                limit = limit.next();
            }
        }
        return orderbookEntries;
    }

    @Override
    public List<OrderbookEntry> bidOrders() {
        List<OrderbookEntry> orderbookEntries = new ArrayList<OrderbookEntry>();
        for (Limit bidLimit : bids) {
            if (bidLimit.isEmpty())
                continue;
            var limit = bidLimit.getHead();
            while (limit != null) {
                orderbookEntries.add(limit);
                limit = limit.next();
            }
        }
        return orderbookEntries;
    }

    public long getAskLiquidity() {
        return askLiquidity;
    }

    public long getBidLiquidity() {
        return bidLiquidity;
    }

    ConcurrentSkipListSet<Limit> getBids() {
        return bids;
    }

    ConcurrentSkipListSet<Limit> getAsks() {
        return asks;
    }

    private void updateLiquidity(Order order) {
        if (order.getSide() == Side.BID) {
            bidLiquidity += order.getQuantity();
        } else {
            askLiquidity += order.getQuantity();
        }
    }

}
