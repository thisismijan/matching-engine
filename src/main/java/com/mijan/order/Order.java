package com.mijan.order;

import com.mijan.common.Side;

public class Order {

    private long orderId;
    private long quantity;
    private long price;
    private Side side;
    private String username;
    private String tradingPair;

    public Order(long orderId, long quantity, long price, Side side, String username, String tradingPair) {
        this.orderId = orderId;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.username = username;
        this.tradingPair = tradingPair;
    }

    public long getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getQuantity() {
        return quantity;
    }

    public String getUsername() {
        return username;
    }

    public String getTradingPair() {
        return tradingPair;
    }

    public boolean isFilled() {
        return quantity == 0;
    }

    public void reduceQuantity(long quantity) {
        this.quantity -= quantity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (orderId ^ (orderId >>> 32));
        result = prime * result + (int) (quantity ^ (quantity >>> 32));
        result = prime * result + (int) (price ^ (price >>> 32));
        result = prime * result + ((side == null) ? 0 : side.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((tradingPair == null) ? 0 : tradingPair.hashCode());
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
        Order other = (Order) obj;
        if (orderId != other.orderId)
            return false;
        if (quantity != other.quantity)
            return false;
        if (price != other.price)
            return false;
        if (side != other.side)
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (tradingPair == null) {
            if (other.tradingPair != null)
                return false;
        } else if (!tradingPair.equals(other.tradingPair))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Order [orderId=" + orderId + ", quantity=" + quantity + ", price=" + price + ", side=" + side
                + ", username=" + username + ", tradingPair=" + tradingPair + "]";
    }

}
