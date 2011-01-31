package com.socialcomputing.wps.services;

import java.util.ArrayList;
import java.util.List;
import com.socialcomputing.wps.test.Product;

public class ProductService {

	public List<Product> getProducts() {
		Product product = new Product();
		product.setProductId(1);
		product.setPrice(0.2);
		List<Product> list = new ArrayList<Product>();
		list.add(product);
		return list;
	}

	public List<Product> getProductsByName(String name) {
		Product product = new Product();
		product.setProductId(1);
		product.setPrice(0.2);
		List<Product> list = new ArrayList<Product>();
		list.add(product);
		return list;
	}
	
	public Product getProduct(int productId) {
		Product product = new Product();
		return product;
	}

	public Product create(Product product) {
		return product;
	}

	public boolean update(Product product) {
		return true;
	}

	public boolean remove(Product product) {
		return true;
	}

	public boolean delete(Product product) {
		return remove(product);
	}

}