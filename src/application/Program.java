package application;

import java.util.Date;

import model.dao.DaoFactory;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		//var dep = new Department(1, "books");
		//var seller = new Seller(1, "bob","teste@teste.com.br", new Date(), 3000.0, dep);
		//System.out.println(dep);
		//System.out.println(seller);
		
		var sellerDao = DaoFactory.createSellerDao();
		var seller = sellerDao.findById(3);		
		System.out.println(seller);
	}
}