package dailyfarm.product.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dailyfarm.accounting.service.seller.SellerService;
import dailyfarm.product.dto.ProductRequsetDto;
import dailyfarm.product.dto.ProductResponseDto;


@RestController
@RequestMapping("/seller/product")
public class ProductController {
	
	@Autowired
	private SellerService service;
	
	@PostMapping("")
	public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductRequsetDto request,
			Principal principal) {
		ProductResponseDto response = service.addProduct(request, principal.getName());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId, Principal principal) {
		service.deleteProduct(productId, principal.getName());
		return ResponseEntity.ok("Product with ID " + productId + " has been deleted successfully");
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long productId) {
		ProductResponseDto response = service.getProduct(productId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productId,
			@RequestBody ProductRequsetDto request, Principal principal) {
		ProductResponseDto response = service.updateProduct(productId, request, principal.getName());
		return ResponseEntity.ok(response);
	}

}
