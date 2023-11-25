package com.mijan.order;

import java.util.ArrayList;
import java.util.List;

import com.mijan.common.Side;
import com.mijan.orderbook.OrderbookEntry;

public class Limit {

    private long price;
    private OrderbookEntry head;
    private OrderbookEntry tail;

    public Limit(long price) {
        this.price = price;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public Side getSide() {
        if (head == null) {
            return Side.UNKNOWN;
        }
        return head.currentOrder().getSide();
    }

    public int getOrderCount() {
        var count = 0;
        var headPointer = head;
        while (headPointer != null) {
            if (headPointer.currentOrder().getQuantity() != 0) {
                count++;
            }
            headPointer = headPointer.next();
        }
        return count;
    }

    public int getQuantity() {
        var count = 0;
        var headPointer = head;
        while (headPointer != null) {
            count += headPointer.currentOrder().getQuantity();
            headPointer = headPointer.next();
        }
        return count;
    }

    public List<OrderRecord> getOrderRecords() {
        var records = new ArrayList<OrderRecord>();
        var headPointer = head;
        while (headPointer != null) {
            var currentOrder = headPointer.currentOrder();
            if (currentOrder.getQuantity() != 0) {
                records.add(new OrderRecord(currentOrder.getOrderId(), headPointer.currentOrder().getQuantity(), price,
                        getSide(), headPointer.currentOrder().getUsername(),
                        headPointer.currentOrder().getTradingPair()));
            }
            headPointer = headPointer.next();
        }
        return records;
    }

    public long getPrice() {
        return price;
    }

    public void setHead(OrderbookEntry orderbookEntry) {
        this.head = orderbookEntry;
    }

    public void setTail(OrderbookEntry orderbookEntry) {
        this.tail = orderbookEntry;
    }

    public OrderbookEntry getHead() {
        return head;
    }

    public OrderbookEntry getTail() {
        return tail;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (price ^ (price >>> 32));
        result = prime * result + ((head == null) ? 0 : head.hashCode());
        result = prime * result + ((tail == null) ? 0 : tail.hashCode());
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
        Limit other = (Limit) obj;
        if (price != other.price)
            return false;
        if (head == null) {
            if (other.head != null)
                return false;
        } else if (!head.equals(other.head))
            return false;
        if (tail == null) {
            if (other.tail != null)
                return false;
        } else if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Limit [price=" + price + ", head=" + head + ", tail=" + tail + "]";
    }

}
