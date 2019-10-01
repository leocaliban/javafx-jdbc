package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import model.dao.DepartmentDAO;
import model.entities.Department;

public class DepartmentDAOJDBC implements DepartmentDAO {

	private Connection connection;

	public DepartmentDAOJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement("INSERT INTO department (Name) VALUES (?)",
					Statement.RETURN_GENERATED_KEYS);

			statement.setString(1, obj.getName());

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
	public void update(Department obj) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");

			statement.setString(1, obj.getName());
			statement.setInt(2, obj.getId());

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
			statement = connection.prepareStatement("DELETE FROM department WHERE Id = ?");
			statement.setInt(1, id);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement("SELECT * FROM department WHERE Id = ?");
			statement.setInt(1, id);

			result = statement.executeQuery();

			if (result.next()) {
				Department department = instantiateDepartment(result);
				return department;
			}
			return null;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	private Department instantiateDepartment(ResultSet result) throws SQLException {
		Department department = new Department();
		department.setId(result.getInt("Id"));
		department.setName(result.getString("Name"));
		return department;
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement statement = null;
		ResultSet result = null;

		try {
			statement = connection.prepareStatement("SELECT * FROM department ORDER BY Name");

			result = statement.executeQuery();

			List<Department> list = new ArrayList<>();

			while (result.next()) {
				Department department = instantiateDepartment(result);
				list.add(department);
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
