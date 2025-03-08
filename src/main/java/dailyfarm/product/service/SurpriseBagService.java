package dailyfarm.product.service;

import org.springframework.stereotype.Service;

import dailyfarm.accounting.entity.seller.SellerAccount;
import dailyfarm.accounting.repository.SurpriseBagRepository;
import dailyfarm.product.dto.SurpriseBagRequestDto;
import dailyfarm.product.dto.SurpriseBagResponseDto;
import dailyfarm.product.entity.surprisebag.SurpriseBag;
import jakarta.transaction.Transactional;
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
        
        if (request.price() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (request.quantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

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
    
    @Transactional
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
}
