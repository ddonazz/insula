package it.andrea.insula.pricing;

import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Public query service for the Pricing module.
 * <p>
 * Exposes read-only pricing information to other modules (e.g. booking)
 * without leaking internal entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingQueryService {

    private final PriceListRepository priceListRepository;

    /**
     * Returns a lightweight summary of an active price list, if it exists.
     */
    public Optional<PriceListSummary> findPriceListByPublicId(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .map(this::toSummary);
    }

    /**
     * Checks whether an active price list with the given public ID exists.
     */
    public boolean priceListExistsByPublicId(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(p -> p.getStatus() != PriceListStatus.DELETED)
                .isPresent();
    }

    private PriceListSummary toSummary(PriceList priceList) {
        return PriceListSummary.builder()
                .publicId(priceList.getPublicId())
                .name(priceList.getName())
                .currency(priceList.getCurrency())
                .isDefault(priceList.isDefault())
                .status(priceList.getStatus().name())
                .build();
    }
}

