package dailyfarm.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dailyfarm.order.dto.OrderResponseDto;
import dailyfarm.product.dto.SurpriseBagRequestDto;
import dailyfarm.product.dto.SurpriseBagResponseDto;
import dailyfarm.product.service.SurpriseBagService;

@RestController
@RequestMapping("/surprisebag")
public class SurpriseBagController {

	@Autowired
	private SurpriseBagService service;;
	
	@PostMapping
    @PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<SurpriseBagResponseDto> addSurpriseBag(@RequestBody SurpriseBagRequestDto request) {
	    SurpriseBagResponseDto response = service.addSurpriseBag(request);
	    return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{bagId}")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<String> deleteSurpriseBag(@PathVariable Long bagId) {
	    service.deleteSurpriseBag(bagId);
	    return ResponseEntity.ok("SurpriseBag with ID " + bagId + " has been deleted successfully");
	}

	@GetMapping("/{bagId}")
    public ResponseEntity<SurpriseBagResponseDto> getSurpriseBag(@PathVariable Long bagId) {
        SurpriseBagResponseDto response = service.getSurpriseBag(bagId);
        return ResponseEntity.ok(response);
    }

	@PatchMapping("/{bagId}")
	@PreAuthorize("hasRole('SELLER')")
	public ResponseEntity<SurpriseBagResponseDto> updateSurpriseBag(@PathVariable Long bagId, @RequestBody SurpriseBagRequestDto request) {
	    SurpriseBagResponseDto response = service.updateSurpriseBag(bagId, request);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/all")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<SurpriseBagResponseDto>> getAllSurpriseBags() {
        List<SurpriseBagResponseDto> response = service.getAllSurpriseBags();
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/price")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<SurpriseBagResponseDto>> findSurpriseBagsByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        List<SurpriseBagResponseDto> response = service.findSurpriseBagsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/seller/{sellerName}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<SurpriseBagResponseDto>> findByCompanyName(@PathVariable String sellerName) {
        List<SurpriseBagResponseDto> bags = service.findBySellerCompanyName(sellerName);
        return ResponseEntity.ok(bags);
    }
    
    @PostMapping("/order/{bagId}")
	@PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDto> addSurpriseBagToOrder(@PathVariable Long bagId) {
        OrderResponseDto response = service.addSurpriseBagToOrder(bagId);
        return ResponseEntity.ok(response);
    }
	}