package datasource;

import model.Order;
import java.util.List;

public interface OrdersDataSource {
    void saveOrder(Order order);
    void deleteOrder(Order order);
    List<Order> findAllOrders();
}
