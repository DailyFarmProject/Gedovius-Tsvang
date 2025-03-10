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
import dailyfarm.product.dto.SurpriseBagRequestDto;
import dailyfarm.product.dto.SurpriseBagResponseDto;

@RestController
@RequestMapping("/seller/surprise-bag")
public class SurpriseBagController {

	@Autowired
	private SellerService service;
	
	@PostMapping("")
	public ResponseEntity<SurpriseBagResponseDto> addSurpriseBag(@RequestBody SurpriseBagRequestDto request,
			Principal principal) {
		SurpriseBagResponseDto response = service.addSurpriseBag(request, principal.getName());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{bagId}")
	public ResponseEntity<String> deleteSurpriseBag(@PathVariable Long bagId, Principal principal) {
		service.deleteSurpriseBag(bagId, principal.getName());
		return ResponseEntity.ok("SurpriseBag with ID " + bagId + " has been deleted successfully");
	}

	@GetMapping("/{bagId}")
	public ResponseEntity<SurpriseBagResponseDto> getSurpriseBag(@PathVariable Long bagId) {
		SurpriseBagResponseDto response = service.getSurpriseBag(bagId);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{bagId}")
	public ResponseEntity<SurpriseBagResponseDto> updateSurpriseBag(@PathVariable Long bagId,
			@RequestBody SurpriseBagRequestDto request, Principal principal) {
		SurpriseBagResponseDto response = service.updateSurpriseBag(bagId, request, principal.getName());
		return ResponseEntity.ok(response);
	}

}
