package edu.icet.service.interfaces;

import edu.icet.model.TopProduct;
import java.util.List;
import java.util.Map;

public interface ReportService {
    // Sales Reports
    List<Map<String, Object>> getDailySalesReport(String date);
    List<Map<String, Object>> getMonthlySalesReport(String year, String month);
    List<Map<String, Object>> getSalesReportByDateRange(String startDate, String endDate);

    // Product Reports
    List<TopProduct> getTopSellingProducts(int limit, String period);
    List<Map<String, Object>> getLowStockProducts(int threshold);
    List<Map<String, Object>> getProductPerformanceReport();

    // Financial Reports
    double getTotalRevenue(String period); // daily, monthly, yearly
    Map<String, Object> getSalesSummary(String startDate, String endDate);

    // Inventory Reports
    List<Map<String, Object>> getInventoryValuationReport();
    List<Map<String, Object>> getStockMovementReport(String productId);

    // Dashboard Data
    Map<String, Object> getDashboardSummary();
}