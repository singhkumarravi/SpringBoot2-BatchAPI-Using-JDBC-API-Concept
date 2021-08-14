package com.olive.processor;

import org.springframework.batch.item.ItemProcessor;

import com.olive.product.Product;

public class MyItemProcessor implements ItemProcessor<Product, Product> {

	
	public Product process(Product item) throws Exception {
		               double cost = item.getProdcost();
		               item.setProdGST(cost*12/100);
		               item.setProdDiscount(cost*20/100);
		return item;
	}

}
