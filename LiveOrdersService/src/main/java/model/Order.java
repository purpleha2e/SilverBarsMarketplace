package model;

public class Order {

    public enum OrderType {
        BUY,
        SELL
    }

    private int userId;
    private double orderQty;
    private double pricePerKg;
    private OrderType orderType;

    // zero user id assumed for merged orders
    public Order(double orderQty, double pricePerKg, OrderType orderType) {
        this(0, orderQty, pricePerKg, orderType);
    }

    public Order(int userId, double orderQty, double pricePerKg, OrderType orderType) {
        this.userId = userId;
        this.orderQty = orderQty;
        this.pricePerKg = pricePerKg;
        this.orderType = orderType;
    }

    public int getUserId() { return userId; }
    public double getOrderQty() { return orderQty; }
    public double getPricePerKg() { return pricePerKg; }
    public OrderType getOrderType() { return orderType; }

    public static Order combine(Order a, Order b) {
        return new Order(a.getOrderQty() + b.getOrderQty(), a.getPricePerKg(), a.getOrderType());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Order) {
            return
                this.userId == ((Order) other).userId &&
                this.orderType == ((Order) other).orderType &&
                this.pricePerKg == ((Order) other).pricePerKg &&
                this.orderQty == ((Order) other).orderQty;
        }

        return false;
    }

    // no hashCode necessary for scope
}
