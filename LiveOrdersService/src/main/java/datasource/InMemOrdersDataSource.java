package datasource;

import model.Order;

import java.util.ArrayList;
import java.util.List;

public class InMemOrdersDataSource implements OrdersDataSource {
    private List<Order> orderData = new ArrayList<>();

    public void saveOrder(Order order) {
        orderData.add(order);
    }

    // no consideration given to multi-threaded access
    public void deleteOrder(Order order) {
        orderData.stream()
                .filter(p -> p.equals(order))
                .findFirst()
                .ifPresent(p -> orderData.remove(p));
    }

    public List<Order> findAllOrders() {
        return orderData;
    }
}
