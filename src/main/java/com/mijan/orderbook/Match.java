package com.mijan.orderbook;

import java.util.HashSet;
import java.util.Set;

import com.mijan.order.Order;

public class Match {

    private Order bid;
    private Order ask;
    private long filledQuantity;
    private long price;
    private Set<Order> ordersToRemove = new HashSet<>();

    public Match(Order bid, Order ask, long filledQuantity, long price) {
        this.bid = bid;
        this.ask = ask;
        this.filledQuantity = filledQuantity;
        this.price = price;
    }

    public Order getBid() {
        return bid;
    }

    public Order getAsk() {
        return ask;
    }

    public long getFilledQuantity() {
        return filledQuantity;
    }

    public long getPrice() {
        return price;
    }

    public Set<Order> getOrdersToRemove() {
        return ordersToRemove;
    }

    public void addOrdersToRemove(Order order) {
        ordersToRemove.add(order);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bid == null) ? 0 : bid.hashCode());
        result = prime * result + ((ask == null) ? 0 : ask.hashCode());
        result = prime * result + (int) (filledQuantity ^ (filledQuantity >>> 32));
        result = prime * result + (int) (price ^ (price >>> 32));
        result = prime * result + ((ordersToRemove == null) ? 0 : ordersToRemove.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Match other = (Match) obj;
        if (bid == null) {
            if (other.bid != null)
                return false;
        } else if (!bid.equals(other.bid))
            return false;
        if (ask == null) {
            if (other.ask != null)
                return false;
        } else if (!ask.equals(other.ask))
            return false;
        if (filledQuantity != other.filledQuantity)
            return false;
        if (price != other.price)
            return false;
        if (ordersToRemove == null) {
            if (other.ordersToRemove != null)
                return false;
        } else if (!ordersToRemove.equals(other.ordersToRemove))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Match [bid=" + bid + ", ask=" + ask + ", filledQuantity=" + filledQuantity + ", price=" + price
                + ", ordersToRemove=" + ordersToRemove + "]";
    }

}
