package com.mijan.orderbook;

public class OrderbookSpread {

    private Long bid;
    private Long ask;

    public OrderbookSpread(Long bid, Long ask) {
        this.bid = bid;
        this.ask = ask;
    }

    public Long get() {
        if (bid != null && ask != null) {
            return ask - bid;
        } else
            return null;
    }

}
