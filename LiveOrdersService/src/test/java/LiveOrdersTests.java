import datasource.InMemOrdersDataSource;
import datasource.OrdersDataSource;
import model.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.LiveOrderService;
import java.util.List;

import static model.Order.OrderType.BUY;
import static model.Order.OrderType.SELL;

public class LiveOrdersTests {
    private LiveOrderService orderService;
    private OrdersDataSource dataSource;

    @Before
    public void setup() {
        dataSource = new InMemOrdersDataSource();
        orderService = new LiveOrderService(dataSource);
    }

    @Test
    public void testRegisterOrder() {
        orderService.registerOrder(new Order(1, 3.5, 306, SELL ));
        List<Order> orders = dataSource.findAllOrders();
        Assert.assertEquals(1, orders.size());

        Order order = orders.get(0);
        Assert.assertEquals(3.5, order.getOrderQty(), 0.01);
        Assert.assertEquals(306, order.getPricePerKg(), 0.01);
        Assert.assertEquals(1, order.getUserId());
        Assert.assertEquals(SELL, order.getOrderType());
    }

    @Test
    public void testRemoveOrder() {
        orderService.registerOrder(new Order(1, 3.5, 306, SELL ));
        orderService.registerOrder(new Order(2, 1.2, 310, SELL ));
        orderService.registerOrder(new Order(3, 1.5, 307, SELL ));
        orderService.registerOrder(new Order(4, 2.0, 306, SELL ));

        orderService.cancelOrder(new Order(3, 1.5, 307, SELL));

        Assert.assertEquals(3, dataSource.findAllOrders().size());
    }

    @Test
    public void testGetOrderSummarySELLOrder() {
        orderService.registerOrder(new Order(1, 3.5, 306, SELL ));
        orderService.registerOrder(new Order(2, 1.2, 310, SELL ));
        orderService.registerOrder(new Order(3, 1.5, 307, SELL ));
        orderService.registerOrder(new Order(4, 2.0, 306, SELL ));

        List<Order> orders = orderService.getOrderSummary();

        Assert.assertEquals(3, orders.size());

        // check ordering for SELL
        Assert.assertEquals(306, orders.get(0).getPricePerKg(), 0.01);
        Assert.assertEquals(307, orders.get(1).getPricePerKg(), 0.01);
        Assert.assertEquals(310, orders.get(2).getPricePerKg(), 0.01);

        // check orderQty totals
        Assert.assertEquals(5.5, orders.get(0).getOrderQty(), 0.01);
        Assert.assertEquals(1.5, orders.get(1).getOrderQty(), 0.01);
        Assert.assertEquals(1.2, orders.get(2).getOrderQty(), 0.01);

    }

    @Test
    public void testGetOrderSummaryBUYOrder() {
        orderService.registerOrder(new Order(1, 1.9, 302, BUY ));
        orderService.registerOrder(new Order(2, 1.5, 307, BUY ));
        orderService.registerOrder(new Order(3, 2.0, 306, BUY ));
        orderService.registerOrder(new Order(4, 15.0, 306, BUY ));

        List<Order> orders = orderService.getOrderSummary();

        Assert.assertEquals(3, orders.size());

        // check ordering for BUY
        Assert.assertEquals(307, orders.get(0).getPricePerKg(), 0.01);
        Assert.assertEquals(306, orders.get(1).getPricePerKg(), 0.01);
        Assert.assertEquals(302, orders.get(2).getPricePerKg(), 0.01);

        // check orderQty totals
        Assert.assertEquals(1.5, orders.get(0).getOrderQty(), 0.01);
        Assert.assertEquals(17, orders.get(1).getOrderQty(), 0.01);
        Assert.assertEquals(1.9, orders.get(2).getOrderQty(), 0.01);
    }

    @Test
    public void testGetOrderSummaryBUYSELLOrder() {
        orderService.registerOrder(new Order(1, 1.9, 302, BUY ));
        orderService.registerOrder(new Order(2, 1.5, 307, BUY ));
        orderService.registerOrder(new Order(3, 2.0, 306, BUY ));
        orderService.registerOrder(new Order(4, 15.0, 306, BUY ));
        orderService.registerOrder(new Order(1, 3.5, 306, SELL ));
        orderService.registerOrder(new Order(2, 1.2, 310, SELL ));
        orderService.registerOrder(new Order(3, 1.5, 307, SELL ));
        orderService.registerOrder(new Order(4, 2.0, 306, SELL ));

        List<Order> orders = orderService.getOrderSummary();

        Assert.assertEquals(6, orders.size());

        // check ordering or prices
        Assert.assertEquals(307, orders.get(0).getPricePerKg(), 0.01);
        Assert.assertEquals(306, orders.get(1).getPricePerKg(), 0.01);
        Assert.assertEquals(302, orders.get(2).getPricePerKg(), 0.01);
        Assert.assertEquals(306, orders.get(3).getPricePerKg(), 0.01);
        Assert.assertEquals(307, orders.get(4).getPricePerKg(), 0.01);
        Assert.assertEquals(310, orders.get(5).getPricePerKg(), 0.01);

        // check ordering of BUY/SELL - not specified but probably useful
        Assert.assertEquals(BUY, orders.get(0).getOrderType());
        Assert.assertEquals(BUY, orders.get(1).getOrderType());
        Assert.assertEquals(BUY, orders.get(2).getOrderType());
        Assert.assertEquals(SELL, orders.get(3).getOrderType());
        Assert.assertEquals(SELL, orders.get(4).getOrderType());
        Assert.assertEquals(SELL, orders.get(5).getOrderType());

        // check orderQty totals
        Assert.assertEquals(1.5, orders.get(0).getOrderQty(), 0.01);
        Assert.assertEquals(17, orders.get(1).getOrderQty(), 0.01);
        Assert.assertEquals(1.9, orders.get(2).getOrderQty(), 0.01);
        Assert.assertEquals(5.5, orders.get(3).getOrderQty(), 0.01);
        Assert.assertEquals(1.5, orders.get(4).getOrderQty(), 0.01);
        Assert.assertEquals(1.2, orders.get(5).getOrderQty(), 0.01);
    }
}
