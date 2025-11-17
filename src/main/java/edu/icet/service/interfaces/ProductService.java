package edu.icet.service.interfaces;

import edu.icet.model.CategoryDTO;
import edu.icet.model.ProductDTO;

import java.util.List;

public interface ProductService {

    // Product operations
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(String productId);
    boolean addProduct(ProductDTO product);
    boolean updateProduct(ProductDTO product);
    boolean deleteProduct(String productId);
    List<ProductDTO> getProductsByCategory(String categoryId);
    List<ProductDTO> searchProducts(String keyword);

    // Category operations
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(String categoryId);
    boolean addCategory(CategoryDTO category);
    boolean updateCategory(CategoryDTO category);
    boolean deleteCategory(String categoryId);
}