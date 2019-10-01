package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDAO;
import model.entities.Department;
import model.entities.Seller;

public class SellerDAOJDBC implements SellerDAO {

	private Connection connection;

	public SellerDAOJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
					"INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES (?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, obj.getName());
			statement.setString(2, obj.getEmail());
			statement.setDate(3, new Date(obj.getBirthdate().getTime()));
			statement.setDouble(4, obj.getBaseSalary());
			statement.setInt(5, obj.getDepartment().getId());

			int rowsAffected = statement.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet result = statement.getGeneratedKeys();
				if (result.next()) {
					int id = result.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(result);
			} else {
				throw new DBException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
					"UPDATE seller SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? WHERE Id = ?");

			statement.setString(1, obj.getName());
			statement.setString(2, obj.getEmail());
			statement.setDate(3, new Date(obj.getBirthdate().getTime()));
			statement.setDouble(4, obj.getBaseSalary());
			statement.setInt(5, obj.getDepartment().getId());
			statement.setInt(6, obj.getId());

			statement.executeUpdate();

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("DELETE FROM seller WHERE Id = ?");
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement(
					"SELECT seller.*, department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE seller.Id = ?");
			statement.setInt(1, id);

			result = statement.executeQuery();

			if (result.next()) {
				Department department = instantiateDepartment(result);
				Seller seller = instantiateSeller(result, department);
				return seller;
			}
			return null;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	private Seller instantiateSeller(ResultSet result, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(result.getInt("Id"));
		seller.setName(result.getString("Name"));
		seller.setEmail(result.getString("Email"));
		seller.setBaseSalary(result.getDouble("BaseSalary"));
		seller.setBirthdate(result.getDate("BirthDate"));
		seller.setDepartment(department);
		return seller;
	}

	private Department instantiateDepartment(ResultSet result) throws SQLException {
		Department department = new Department();
		department.setId(result.getInt("DepartmentId"));
		department.setName(result.getString("DepName"));
		return department;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id ORDER BY Name");

			result = statement.executeQuery();

			List<Seller> list = new ArrayList<Seller>();
			Map<Integer, Department> map = new HashMap<Integer, Department>();

			while (result.next()) {
				Department dep = map.get(result.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(result);
					map.put(result.getInt("DepartmentId"), dep);
				}

				Seller seller = instantiateSeller(result, dep);
				list.add(seller);
			}
			return list;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement(
					"SELECT seller.*,department.Name as DepName FROM seller INNER JOIN department ON seller.DepartmentId = department.Id WHERE DepartmentId = ? ORDER BY Name");
			statement.setInt(1, department.getId());

			result = statement.executeQuery();

			List<Seller> list = new ArrayList<Seller>();
			Map<Integer, Department> map = new HashMap<Integer, Department>();

			while (result.next()) {
				Department dep = map.get(result.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(result);
					map.put(result.getInt("DepartmentId"), dep);
				}

				Seller seller = instantiateSeller(result, dep);
				list.add(seller);
			}
			return list;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

}
