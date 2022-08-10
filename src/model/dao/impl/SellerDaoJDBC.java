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

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.DbDoc;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller objSeller) {
		
		PreparedStatement st = null;		
		
		try {
			String strSql = " INSERT INTO SELLER (NAME, EMAIL, BIRTHDATE, BASESALARY, DEPARTMENTID) VALUES(?, ?, ?, ?, ?)";
			
			st = conn.prepareStatement(strSql, Statement.RETURN_GENERATED_KEYS);
			st.setString(1, objSeller.getName());
			st.setString(2, objSeller.getEmail());
			st.setDate(3, new java.sql.Date(objSeller.getBirthDate().getTime()));
			st.setDouble(4, objSeller.getBaseSalary());
			st.setInt(5, objSeller.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					objSeller.setId(id);
				}
				
				DB.closeResultSet(rs);
			}else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());			
		} finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void update(Seller objSeller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			var strSql = "SELECT S.*, D.NAME AS DEPNAME "+
				         "  FROM SELLER S "+ 
				         " INNER JOIN DEPARTMENT D ON D.ID = S.DEPARTMENTID "+
				         " WHERE S.ID = ? ";
			st = conn.prepareStatement(strSql);
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				/*
				Department dep = new Department();
				dep.setId(rs.getInt("DepartmentId"));
				dep.setName(rs.getString("DepName"));
				
				Seller seller = new Seller();
				seller.setId(rs.getInt("Id"));
				seller.setName(rs.getString("Name"));
				seller.setEmail(rs.getString("Email"));
				seller.setBaseSalary(rs.getDouble("BaseSalary"));
				seller.setBirthDate(rs.getDate("BirthDate"));
				seller.setDepartment(dep);
				return seller;
				*/
				
				Department dep = instantiateDepartment(rs);				
				Seller seller = instantiateSeller(rs, dep);
				return seller;
			}
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);			
		}		
	}

	@Override
	public List<Seller> findAll() {
		
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			var strSql = "SELECT S.*, D.NAME AS DEPNAME "+
				         "  FROM SELLER S "+ 
				         " INNER JOIN DEPARTMENT D ON D.ID = S.DEPARTMENTID "+
				         " ORDER BY NAME";
			
			st = conn.prepareStatement(strSql);
			rs = st.executeQuery();
			
			List<Seller> listSeller = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {				
			  var depto = map.get(rs.getInt("DepartmentId"));
			  
			  if(depto == null) {
			    depto = instantiateDepartment(rs);
				map.put(rs.getInt("DepartmentId"), depto);
			  }
				
			  Seller seller = instantiateSeller(rs, depto);
			  listSeller.add(seller);
			}
			return listSeller;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());

		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);			
		}	
	}
	
	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			var strSql = "SELECT S.*, D.NAME AS DEPNAME "+
				         "  FROM SELLER S "+ 
				         " INNER JOIN DEPARTMENT D ON D.ID = S.DEPARTMENTID "+
				         " WHERE D.DEPARTMENTID = ? "+
				         " ORDER BY Name ";
			
			st = conn.prepareStatement(strSql);
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> listSeller = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {				
				var depto = map.get(rs.getInt("DepartmentId"));
				
				if(depto == null) {
					depto = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), depto);
				}
				
				// pode passar o department que está sendo passado como parametro no próprio método
				Seller seller = instantiateSeller(rs, depto);
				listSeller.add(seller);
			}			
			return listSeller;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		var dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));		
		return dep;
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		var seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setBirthDate(rs.getDate("BirthDate"));
		seller.setDepartment(dep);
		return seller;
	}

}
