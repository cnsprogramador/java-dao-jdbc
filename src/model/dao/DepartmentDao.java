package model.dao;

import java.util.List;

import model.entities.Department;

public interface DepartmentDao {
	
	void insert(Department objDepartment);
	void update(Department objDepartment);
	void deleteById(Integer id);
	Department findById(Integer id);
	List<Department> findByAll();
}
