package com.mijan.orderbook.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.mijan.common.Side;
import com.mijan.order.Order;
import com.mijan.orderbook.Match;

public class MatchingOrderbookTest {
    @Test
    void placeMarketOrder_InsufficientLiquidity_ReturnsNull() {
        MatchingOrderbook orderbook = new MatchingOrderbook("BTC/USD");
        Order bidOrder = TestOrderUtil.newTestOrder(1, 10, 100, Side.BID);
        orderbook.placeLimitOrder(bidOrder);

        Order marketOrder = TestOrderUtil.newTestOrder(2, 15, 100, Side.ASK);
        Set<Match> matches = orderbook.placeMarketOrder(marketOrder);

        assertNull(matches);
    }

    @Test
    public void testMatchingOrderbook() {
        MatchingOrderbook orderbook = new MatchingOrderbook("BTC/USD");

        // Place bid order with price 110
        Order bidOrder = new Order(1, 10, 110, Side.BID, "user1", "BTC/USD");
        Set<Match> bidMatches = orderbook.placeLimitOrder(bidOrder);
        assertNotNull(bidMatches);
        assertEquals(0, bidMatches.size());

        // Place ask order with price 100
        Order askOrder = new Order(2, 10, 100, Side.ASK, "user2", "BTC/USD");
        Set<Match> askMatches = orderbook.placeLimitOrder(askOrder);
        assertNotNull(askMatches);
        assertEquals(1, askMatches.size());

        // Verify that the first bid order matched with the first ask order
        Match firstMatch = askMatches.iterator().next();
        assertEquals(bidOrder, firstMatch.getBid());
        assertEquals(askOrder, firstMatch.getAsk());
        assertEquals(10, firstMatch.getFilledQuantity());
        assertEquals(110, firstMatch.getPrice());

        // Place another bid order with price 90
        Order secondBidOrder = new Order(3, 5, 90, Side.BID, "user3", "BTC/USD");
        Set<Match> secondBidMatches = orderbook.placeLimitOrder(secondBidOrder);
        assertNotNull(secondBidMatches);
        assertEquals(0, secondBidMatches.size());

        // Place another ask order with price 95
        Order secondAskOrder = new Order(4, 8, 95, Side.ASK, "user4", "BTC/USD");
        Set<Match> secondAskMatches = orderbook.placeLimitOrder(secondAskOrder);
        assertNotNull(secondAskMatches);
        assertEquals(0, secondAskMatches.size());

        // place another bid order with price 96
        Order thirdBidOrder = new Order(5, 13, 96, Side.BID, "user5", "BTC/USD");
        Set<Match> thirdBidMatches = orderbook.placeLimitOrder(thirdBidOrder);
        assertNotNull(thirdBidMatches);
        assertEquals(1, thirdBidMatches.size());

        // Verify that the third bid order matched with the second ask order
        Match thirdMatch = thirdBidMatches.iterator().next();
        assertEquals(thirdBidOrder, thirdMatch.getBid());
        assertEquals(secondAskOrder, thirdMatch.getAsk());
        assertEquals(8, thirdMatch.getFilledQuantity());
        assertEquals(95, thirdMatch.getPrice());

        // Place another ask order with price 95
        Order thirdAskOrder = new Order(6, 8, 95, Side.ASK, "user4", "BTC/USD");
        Set<Match> thirdAskMatches = orderbook.placeLimitOrder(thirdAskOrder);
        System.out.println(thirdAskMatches);
        assertNotNull(thirdAskMatches);
        assertEquals(2, thirdAskMatches.size());

        // Verify that the third ask order matched with the third and second bid order
        Iterator<Match> iterator = thirdAskMatches.iterator();
        Match fourthMatch = iterator.next();
        assertEquals(thirdBidOrder, fourthMatch.getBid());
        assertEquals(thirdAskOrder, fourthMatch.getAsk());
        assertEquals(5, fourthMatch.getFilledQuantity());
        assertEquals(96, fourthMatch.getPrice());
        Match fifthMatch = iterator.next();
        assertEquals(secondBidOrder, fifthMatch.getBid());
        assertEquals(thirdAskOrder, fifthMatch.getAsk());
        assertEquals(3, fifthMatch.getFilledQuantity());
        assertEquals(90, fifthMatch.getPrice());

    }
}
