package edu.icet.service.impl;

import edu.icet.model.EmployeeDTO;
import edu.icet.service.interfaces.EmployeeService;
import edu.icet.db.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";

        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO();
                employee.setEmployeeId(rs.getInt("employee_id"));
                employee.setFullName(rs.getString("full_name"));
                employee.setPosition(rs.getString("position"));
                employee.setContactNo(rs.getString("contact_no"));
                employee.setAddress(rs.getString("address"));
                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    @Override
    public EmployeeDTO getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new EmployeeDTO(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("position"),
                        rs.getString("contact_no"),
                        rs.getString("address")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean addEmployee(EmployeeDTO employee) {
        String sql = "INSERT INTO employee (full_name, position, contact_no, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPosition());
            ps.setString(3, employee.getContactNo());
            ps.setString(4, employee.getAddress());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateEmployee(EmployeeDTO employee) {
        String sql = "UPDATE employee SET full_name=?, position=?, contact_no=?, address=? WHERE employee_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getFullName());
            ps.setString(2, employee.getPosition());
            ps.setString(3, employee.getContactNo());
            ps.setString(4, employee.getAddress());
            ps.setInt(5, employee.getEmployeeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employee WHERE employee_id=?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employeeId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<EmployeeDTO> searchEmployees(String keyword) {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE full_name LIKE ? OR position LIKE ? OR contact_no LIKE ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ps.setString(3, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                employees.add(new EmployeeDTO(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("position"),
                        rs.getString("contact_no"),
                        rs.getString("address")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }
}
