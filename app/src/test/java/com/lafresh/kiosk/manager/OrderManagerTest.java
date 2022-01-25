package com.lafresh.kiosk.manager;

import com.lafresh.kiosk.model.Item;
import com.lafresh.kiosk.model.Order;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Kyle on 2021/2/1.
 */
public class OrderManagerTest {

    @Test
    public void checkUsePoint() {
        OrderManager manager = new OrderManager();
        manager.setLogin(true);
        Item.PriceBean priceBean = new Item.PriceBean();
        manager.checkUsePoint(true,30,priceBean);
        Assert.assertEquals(priceBean.getSpendPoints(), 30);
    }

    @Test
    public void setTicketsToOrder() {
        OrderManager manager = new OrderManager();
        Order order = new Order();
        manager.setLogin(true);
        Order.TicketsBean ticketsBean = new Order.TicketsBean();
        ticketsBean.setNumber("123");
        ticketsBean.setPrice(123);
        manager.addTickets(ticketsBean);
        manager.setTicketsToOrder(order);
        Assert.assertEquals(manager.getTickets().size(), 1);
    }
}
