package dailyfarm.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dailyfarm.accounting.entity.seller.SellerAccount;
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

	private final SurpriseBagRepository repo;
    
    @Transactional
    public SurpriseBagResponseDto addSurpriseBag(SurpriseBagRequestDto request, SellerAccount seller) {
        log.info("Adding SurpriseBag by seller: {}", seller.getLogin());
        
        SurpriseBag surpriseBag = SurpriseBag.of(request, seller);

        SurpriseBag savedBag = repo.save(surpriseBag);
        log.info("SurpriseBag added successfully: ID={}", savedBag.getId());
        return SurpriseBagResponseDto.build(savedBag);
    }

    @Transactional
    public void deleteSurpriseBag(Long bagId, SellerAccount seller) {
        log.info("Deleting SurpriseBag ID={} by seller: {}", bagId, seller.getLogin());
        SurpriseBag surpriseBag = repo.findById(bagId)
            .orElseThrow(() -> new IllegalArgumentException("SurpriseBag with ID: " + bagId + " not found"));

        if (!surpriseBag.getSeller().getLogin().equals(seller.getLogin())) {
            throw new IllegalStateException("Only the seller who created the SurpriseBag can delete it");
        }

        repo.delete(surpriseBag);
        log.info("SurpriseBag deleted successfully: ID={}", bagId);
    }
    
    @Transactional(readOnly = true)
    public SurpriseBagResponseDto getSurpriseBag(Long bagId) {
        log.info("Fetching SurpriseBag ID={}", bagId);
        SurpriseBag surpriseBag = repo.findById(bagId)
            .orElseThrow(() -> new IllegalArgumentException("SurpriseBag not found with ID: " + bagId));
        return SurpriseBagResponseDto.build(surpriseBag);
    }

    @Transactional
    public SurpriseBagResponseDto updateSurpriseBag(Long bagId, SurpriseBagRequestDto request, SellerAccount seller) {
        log.info("Updating SurpriseBag ID={} by seller: {}", bagId, seller.getLogin());
        SurpriseBag surpriseBag = repo.findById(bagId)
            .orElseThrow(() -> new IllegalArgumentException("SurpriseBag with ID: " + bagId + " not found"));

           if (!surpriseBag.getSeller().getLogin().equals(seller.getLogin())) {
            throw new IllegalStateException("Only the seller who created the SurpriseBag can update it");
        }

        if (request.name() != null) surpriseBag.setName(request.name());
        if (request.price() > 0) surpriseBag.setPrice(request.price());
        else if (request.price() <= 0) throw new IllegalArgumentException("Price must be greater than 0");
        if (request.description() != null) surpriseBag.setDescription(request.description());
        if (request.quantity() >= 0) surpriseBag.setQuantity(request.quantity());
        else 
        	throw new IllegalArgumentException("Quantity cannot be negative");
        if (request.imageUrl() != null) surpriseBag.setImageUrl(request.imageUrl());

        SurpriseBag updatedBag = repo.save(surpriseBag);
        log.info("SurpriseBag updated successfully: ID={}", updatedBag.getId());
        return SurpriseBagResponseDto.build(updatedBag);
    }
    
    @Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> getAllSurpriseBags() {
        log.info("Fetching all SurpriseBags");
        List<SurpriseBag> surpriseBags = repo.findAll();
        log.info("Found {} SurpriseBags", surpriseBags.size());
        return surpriseBags.stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
 }
    
    @Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> findByBagName(String name) {
        log.info("Fetching SurpriseBags by name: {}", name);
        List<SurpriseBag> surpriseBags = repo.findByNameContainingIgnoreCase(name);
        log.info("Found {} SurpriseBags with name containing '{}'", surpriseBags.size(), name);
        return surpriseBags.stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> findSurpriseBagsByPriceRange(Double minPrice, Double maxPrice) {
        log.info("Fetching SurpriseBags by price range: {} - {}", minPrice, maxPrice);
        List<SurpriseBag> surpriseBags = repo.findByPriceBetween(minPrice, maxPrice);
        log.info("Found {} SurpriseBags with price between {} and {}", surpriseBags.size(), minPrice, maxPrice);
        return surpriseBags.stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
   
    
    @Transactional(readOnly = true)
    public List<SurpriseBagResponseDto> findBySeller(String sellerLogin) {
        log.info("Fetching SurpriseBags by seller: {}", sellerLogin);
        List<SurpriseBag> surpriseBags = repo.findBySellerLogin(sellerLogin);
        log.info("Found {} SurpriseBags by seller '{}'", surpriseBags.size(), sellerLogin);
        return surpriseBags.stream()
            .map(SurpriseBagResponseDto::build)
            .collect(Collectors.toList());
    }
    
}
