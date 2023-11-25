package com.mijan.orderbook.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

import com.mijan.common.Side;
import com.mijan.order.Limit;
import com.mijan.order.Order;
import com.mijan.orderbook.IMatchingOrderbook;
import com.mijan.orderbook.Match;

public class MatchingOrderbook extends Orderbook implements IMatchingOrderbook {

    private final ReentrantLock lock = new ReentrantLock();

    public MatchingOrderbook(String tradingPair) {
        super(tradingPair);
    }

    public Set<Match> fill(Limit limit, Order order) {
        Set<Match> matches = new HashSet<>();

        for (var next = limit.getHead(); next != null; next = next.next()) {
            var match = fillOrder(next.currentOrder(), order, limit.getPrice());
            matches.add(match);
        }

        matches.forEach(m -> m.getOrdersToRemove().forEach(this::removeOrder));

        return matches;
    }

    private Match fillOrder(Order a, Order b, long price) {
        Order bid, ask;

        if (a.getSide() == Side.BID) {
            bid = a;
            ask = b;
        } else {
            bid = b;
            ask = a;
        }

        long filledQuantity = Math.min(a.getQuantity(), b.getQuantity());
        a.reduceQuantity(filledQuantity);
        b.reduceQuantity(filledQuantity);

        var match = new Match(bid, ask, filledQuantity, price);
        if (a.isFilled())
            match.addOrdersToRemove(a);
        if (b.isFilled())
            match.addOrdersToRemove(b);
        return match;
    }

    public Set<Match> placeMarketOrder(Order order) {
        lock.lock();
        try {
            Set<Match> matches = new HashSet<>();
            ConcurrentSkipListSet<Limit> orderBookSide = (order.getSide() == Side.BID) ? asks : bids;

            if (order.getQuantity() > getOrderBookLiquidity(order.getSide())) {
                System.err.println("Not enough liquidity");
                return null;
            }

            for (var limit : orderBookSide) {
                matches.addAll(match(order, limit));
            }

            return matches;
        } finally {
            lock.unlock();
        }
    }

    private long getOrderBookLiquidity(Side side) {
        return (side == Side.BID) ? askLiquidity : bidLiquidity;
    }

    @Override
    public Set<Match> match(Order order, Limit limit) {
        if (order.isFilled())
            return Collections.emptySet();
        var limitMatches = fill(limit, order);
        if (limit.getHead() == null) {
            removeLimit(limit);
        }
        return limitMatches;
    }

    private void removeLimit(Limit limit) {
        if (limit.getSide() == Side.BID) {
            bids.remove(limit);
        } else {
            asks.remove(limit);
        }
    }

    public Set<Match> placeLimitOrder(Order order) {
        lock.lock();
        var matches = new HashSet<Match>();
        try {
            tryMatch(order, matches);
            if (order.getQuantity() > 0) {
                addOrder(order);
            }
            return matches;
        } finally {
            lock.unlock();
        }
    }

    private synchronized void tryMatch(Order order, HashSet<Match> matches) {
        var orderBookSide = (order.getSide() == Side.BID) ? asks : bids;
        var limitPrice = order.getPrice();

        Limit limit = orderBookSide.lower(new Limit(limitPrice));

        if (limit != null) {
            var relevantLimits = orderBookSide.tailSet(limit, true);

            relevantLimits.forEach(l -> matches.addAll(match(order, l)));
        }
    }
}