package edu.icet.service.interfaces;

import edu.icet.model.OrderDTO;
import edu.icet.model.OrderItemDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO order);
    OrderDTO getOrderById(String orderId);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getOrdersByDateRange(String startDate, String endDate);
    boolean updateOrderStatus(String orderId, String status);
    double calculateOrderTotal(String orderId);
    List<OrderItemDTO> getOrderItems(String orderId);
}