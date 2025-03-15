package dailyfarm.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dailyfarm.accounting.entity.customer.CustomerAccount;
import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.repository.customer.CustomerRepository;
import dailyfarm.accounting.repository.seller.SellerRepository;
import dailyfarm.order.dto.OrderResponseDto;
import dailyfarm.order.entity.Order;
import dailyfarm.order.repository.OrderRepository;
import dailyfarm.product.dto.SurpriseBagRequestDto;
import dailyfarm.product.dto.SurpriseBagResponseDto;
import dailyfarm.product.entity.surprisebag.SurpriseBag;
import dailyfarm.product.repository.SurpriseBagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurpriseBagService {

	private final SurpriseBagRepository bagRepo;
	private final OrderRepository orderRepo;
	private final SellerRepository sellerRepo;
	private final CustomerRepository customerRepo;

	@Transactional
    public SurpriseBagResponseDto addSurpriseBag(SurpriseBagRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String sellerLogin = authentication.getName();
        log.info("Adding SurpriseBag by seller: {}", sellerLogin);

        SellerAccount seller = sellerRepo.findByLogin(sellerLogin)
            .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + sellerLogin));
        
        SurpriseBag surpriseBag = SurpriseBag.of(request, seller);
        SurpriseBag savedBag = bagRepo.save(surpriseBag);
        log.info("SurpriseBag added successfully: ID={}", savedBag.getId());
        return SurpriseBagResponseDto.build(savedBag);
    }

	@Transactional
	public void deleteSurpriseBag(Long bagId) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String sellerLogin = authentication.getName();
	    log.info("Deleting SurpriseBag with ID: {} by seller: {}", bagId, sellerLogin);

	    SurpriseBag bag = bagRepo.findById(bagId)
	        .orElseThrow(() -> new IllegalArgumentException("SurpriseBag not found: " + bagId));
	    if (!bag.getSeller().getLogin().equals(sellerLogin)) {
	        throw new SecurityException("You can only delete your own SurpriseBags");
	    }
	    bagRepo.delete(bag);
	    log.info("SurpriseBag deleted successfully: ID={}", bagId);
	}
    
	@Transactional(readOnly = true)
    public SurpriseBagResponseDto getSurpriseBag(Long bagId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        log.info("Fetching SurpriseBag with ID: {} for user: {}", bagId, login);

        SurpriseBag bag = bagRepo.findById(bagId)
            .orElseThrow(() -> new IllegalArgumentException("SurpriseBag not found: " + bagId));
        return SurpriseBagResponseDto.build(bag);
    }

	@Transactional
	public SurpriseBagResponseDto updateSurpriseBag(Long bagId, SurpriseBagRequestDto request) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String sellerLogin = authentication.getName();
	    log.info("Updating SurpriseBag with ID: {} by seller: {}", bagId, sellerLogin);

	    SurpriseBag bag = bagRepo.findById(bagId)
	        .orElseThrow(() -> new IllegalArgumentException("SurpriseBag not found: " + bagId));
	    if (!bag.getSeller().getLogin().equals(sellerLogin)) {
	        throw new SecurityException("You can only update your own SurpriseBags");
	    }

	    bag.setName(request.name());
	    bag.setPrice(request.price());
	    bag.setDescription(request.description());
	    bag.setQuantity(request.quantity());
	    bag.setImageUrl(request.imageUrl());

	    SurpriseBag updatedBag = bagRepo.save(bag);
	    log.info("SurpriseBag updated successfully: ID={}", updatedBag.getId());
	    return SurpriseBagResponseDto.build(updatedBag);
	}
    
    
	@Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> getAllSurpriseBags() {
        log.info("Fetching all SurpriseBags");
        return bagRepo.findAll().stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
    
	@Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> findSurpriseBagsByPriceRange(Double minPrice, Double maxPrice) {
        log.info("Fetching SurpriseBags by price range: {} - {}", minPrice, maxPrice);
        return bagRepo.findByPriceBetween(minPrice, maxPrice).stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
    
	@Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> findBySellerCompanyName(String sellerName) {
        log.info("Fetching SurpriseBags by seller company name: {}", sellerName);
        SellerAccount seller = sellerRepo.findByCompanyName(sellerName)
            .orElseThrow(() -> new IllegalArgumentException("Seller not found with company name: " + sellerName));
        return bagRepo.findBySeller(seller).stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
    
    
	@Transactional
	public OrderResponseDto addSurpriseBagToOrder(Long bagId) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String customerLogin = authentication.getName();
	    log.info("Adding SurpriseBag with ID: {} to order for customer: {}", bagId, customerLogin);

	    CustomerAccount customer = customerRepo.findByLogin(customerLogin)
	        .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerLogin));
	    SurpriseBag bag = bagRepo.findById(bagId)
	        .orElseThrow(() -> new IllegalArgumentException("SurpriseBag not found: " + bagId));
	    
	    Order order = Order.of(customer, bag);
	    Order savedOrder = orderRepo.save(order);
	    log.info("Order created successfully: ID={}", savedOrder.getId());
	    return OrderResponseDto.build(savedOrder);
	}
}