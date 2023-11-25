package com.mijan.order;

import com.mijan.common.Side;

public record OrderRecord(long orderId, long quantity, long price, Side side, String username, String tradingPair) {
}
