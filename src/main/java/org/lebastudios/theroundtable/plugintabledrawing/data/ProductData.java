package org.lebastudios.theroundtable.plugintabledrawing.data;

import org.lebastudios.theroundtable.plugincashregister.entities.Product;

import java.math.BigDecimal;

public class ProductData
{
    // TODO: Need to save the IVA tax rate for each product
    public int productId;
    public String productName;
    public String productPrice;
    public String imgPath;
    public boolean taxesIncluded;
    public boolean enabled;
    public String categoryName;
    public String subCategoryName;
    
    public static ProductData fromProduct(Product product)
    {
        ProductData data = new ProductData();
        
        data.productId = product.getId();
        data.productName = product.getName();
        data.productPrice = product.getPrice().toString();
        data.imgPath = product.getImgPath();
        data.taxesIncluded = product.getTaxesIncluded();
        data.enabled = product.isEnabled();
        data.categoryName = product.getCategoryName();
        data.subCategoryName = product.getSubCategoryName();
        
        return data;
    }
    
    public Product intoProduct()
    {
        Product product = new Product();
        
        product.setId(productId);
        product.setName(productName);
        product.setPrice(new BigDecimal(productPrice));
        product.setImgPath(imgPath);
        product.setTaxesIncluded(taxesIncluded);
        product.setEnabled(enabled);
        product.setCategoryName(categoryName);
        product.setSubCategoryName(subCategoryName);
        
        return product;
    }
}
