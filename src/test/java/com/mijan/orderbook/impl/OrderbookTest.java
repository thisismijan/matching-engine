package com.mijan.orderbook.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mijan.common.Side;
import com.mijan.order.Limit;
import com.mijan.order.Order;
import com.mijan.orderbook.OrderbookSpread;

class OrderbookTest {

    private Orderbook orderbook;

    @BeforeEach
    void setUp() {
        orderbook = new Orderbook("BTC/USD");
    }

    @Test
    void testAddOrder() {
        // Create an order and add it to the order book
        Order order = TestOrderUtil.newTestOrder(1, 1000, 125, Side.BID);
        orderbook.addOrder(order);

        // Assert that the order is added successfully
        assertTrue(orderbook.containsOrder(order.getOrderId()));
    }

    @Test
    void testRemoveOrder() {
        // Create an order and add it to the order book
        Order order = TestOrderUtil.newTestOrder(1, 1000, 125, Side.BID);
        orderbook.addOrder(order);

        // Remove the order and assert that it is removed
        orderbook.removeOrder(order);
        assertFalse(orderbook.containsOrder(order.getOrderId()));
    }

    @Test
    void testModifyOrder() {
        // Create an order and add it to the order book
        Order order = TestOrderUtil.newTestOrder(1, 1000, 125, Side.BID);
        orderbook.addOrder(order);

        assertTrue(orderbook.containsOrder(order.getOrderId()));

        order.reduceQuantity(100);
        orderbook.modifyOrder(order);
        assertTrue(orderbook.containsOrder(order.getOrderId()));
        assertEquals(900, orderbook.orders.get(order.getOrderId()).currentOrder().getQuantity());
    }

    @Test
    void testGetSpread() {
        // Add some bid and ask orders to the order book
        Order bidOrder = TestOrderUtil.newTestOrder(1, 1000, 123, Side.BID);
        Order askOrder = TestOrderUtil.newTestOrder(2, 1000, 125, Side.ASK);
        orderbook.addOrder(bidOrder);
        orderbook.addOrder(askOrder);

        // Assert that the spread is calculated correctly
        OrderbookSpread spread = orderbook.getSpread();
        assertNotNull(spread);
        assertEquals(2, spread.get());
    }

    @Test
    void testAddOrdersAndRemoveOrders() {
        // Create an order and add it to the order book
        Order order1 = TestOrderUtil.newTestOrder(1, 1000, 123, Side.BID);
        orderbook.addOrder(order1);

        // Assert that the first order is added successfully
        assertTrue(orderbook.containsOrder(order1.getOrderId()));

        // Assert that the Limit object and limits collection are updated correctly for
        // the first order
        ConcurrentSkipListSet<Limit> bids = orderbook.getBids();

        assertTrue(bids.stream().anyMatch(limit -> !limit.isEmpty()));
        assertTrue(bids.stream().anyMatch(limit -> limit.getPrice() == order1.getPrice()));

        // Create another order and add it to the order book
        Order order2 = TestOrderUtil.newTestOrder(2, 1000, 123, Side.BID);
        orderbook.addOrder(order2);

        // Assert that the second order is added successfully
        assertTrue(orderbook.containsOrder(order2.getOrderId()));

        // check the bids collection
        assertTrue(bids.stream().anyMatch(limit -> !limit.isEmpty()));
        assertTrue(bids.stream().anyMatch(limit -> limit.getPrice() == order2.getPrice()));

        // Assert the head and tail of the Limit for the second bid order
        Limit bidLimit2 = bids.stream().filter(limit -> limit.getPrice() == order2.getPrice()).findFirst().orElse(null);
        assertNotNull(bidLimit2);
        assertEquals(order1.getOrderId(), bidLimit2.getHead().currentOrder().getOrderId());
        assertEquals(order2.getOrderId(), bidLimit2.getTail().currentOrder().getOrderId());

        // Create another order and add it to the order book
        Order order3 = TestOrderUtil.newTestOrder(3, 1000, 123, Side.BID);
        orderbook.addOrder(order3);

        // Assert that the third order is added successfully
        assertTrue(orderbook.containsOrder(order3.getOrderId()));

        // check the bids collection
        assertTrue(bids.stream().anyMatch(limit -> !limit.isEmpty()));
        assertTrue(bids.stream().anyMatch(limit -> limit.getPrice() == order3.getPrice()));

        // Assert the head and tail of the Limit for the third bid order
        Limit bidLimit3 = bids.stream().filter(limit -> limit.getPrice() == order3.getPrice()).findFirst().orElse(null);
        assertNotNull(bidLimit3);
        assertEquals(order1.getOrderId(), bidLimit3.getHead().currentOrder().getOrderId());
        assertEquals(order3.getOrderId(), bidLimit3.getTail().currentOrder().getOrderId());

        // Remove order 1
        orderbook.removeOrder(order1);

        // Assert that the third order is removed successfully
        assertTrue(!orderbook.containsOrder(order1.getOrderId()));

        // Assert the head and tail of the Limit for the third bid order
        Limit bidLimit4 = bids.stream().filter(limit -> limit.getPrice() == order3.getPrice()).findFirst().orElse(null);
        assertNotNull(bidLimit4);
        assertEquals(order2.getOrderId(), bidLimit3.getHead().currentOrder().getOrderId());
        assertEquals(order3.getOrderId(), bidLimit3.getTail().currentOrder().getOrderId());

    }
}