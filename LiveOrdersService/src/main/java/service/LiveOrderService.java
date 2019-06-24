package service;

import datasource.OrdersDataSource;
import model.Order;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static model.Order.OrderType.BUY;
import static model.Order.OrderType.SELL;

public class LiveOrderService {
    private OrdersDataSource dataSource;

    public LiveOrderService(OrdersDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void registerOrder(Order order) {
        dataSource.saveOrder(order);
    }

    public void cancelOrder(Order order) {
        dataSource.deleteOrder(order);
    }

    // no consideration given to multi-threaded access
    public List<Order> getOrderSummary() {
        List<Order> orders = dataSource.findAllOrders();
        List<Order> buyOrders = new ArrayList<>();
        List<Order> sellOrders = new ArrayList<>();

        // group off BUY orders
        Map<Double, List<Order>> groupedBUYOrders = orders
                .stream()
                .filter(o -> o.getOrderType().equals(BUY))
                .collect(groupingBy(o -> o.getPricePerKg()));

        // group off SELL orders
        Map<Double, List<Order>> groupedSELLOrders = orders
                .stream()
                .filter(o -> o.getOrderType().equals(SELL))
                .collect(groupingBy(o -> o.getPricePerKg()));

        // reduce BUY orders
        groupedBUYOrders.values().forEach(o ->
                        o.stream()
                        .reduce((a, b) -> Order.combine(a, b))
                        .ifPresent(buyOrders::add));

        // reduce SELL orders
        groupedSELLOrders.values().forEach(o ->
                        o.stream()
                        .reduce((a, b) -> Order.combine(a, b))
                        .ifPresent(sellOrders::add));

        // sort BUY and SELL lists appropriately
        buyOrders.sort(Comparator.comparingDouble(Order::getPricePerKg).reversed());
        sellOrders.sort(Comparator.comparingDouble(Order::getPricePerKg));

        // combine and return (assumes non-specified ordering of order type)
        return Stream.concat(buyOrders.stream(), sellOrders.stream())
                .collect(Collectors.toList());
    }
}
