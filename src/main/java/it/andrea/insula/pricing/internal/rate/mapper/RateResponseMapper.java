package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import it.andrea.insula.property.PropertyQueryService;
import it.andrea.insula.property.UnitSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RateResponseMapper implements Function<UnitRatePeriod, RateResponseDto> {

    private final PropertyQueryService propertyQueryService;

    @Override
    public RateResponseDto apply(UnitRatePeriod rate) {
        return RateResponseDto.builder()
                .publicId(rate.getPublicId())
                .priceListPublicId(rate.getPriceList() != null ? rate.getPriceList().getPublicId() : null)
                .priceListName(rate.getPriceList() != null ? rate.getPriceList().getName() : null)
                .unitPublicId(rate.getUnitPublicId())
                .unit(resolveUnitSummary(rate.getUnitPublicId()))
                .startDate(rate.getStartDate())
                .endDate(rate.getEndDate())
                .pricePerNight(rate.getPricePerNight())
                .extraGuestPrice(rate.getExtraGuestPrice())
                .minStay(rate.getMinStay())
                .maxStay(rate.getMaxStay())
                .stopSell(rate.isStopSell())
                .closedToArrival(rate.isClosedToArrival())
                .closedToDeparture(rate.isClosedToDeparture())
                .allowedCheckInDays(rate.getAllowedCheckInDays())
                .allowedCheckOutDays(rate.getAllowedCheckOutDays())
                .build();
    }

    private RateResponseDto.UnitSummaryDto resolveUnitSummary(java.util.UUID unitPublicId) {
        if (unitPublicId == null) return null;
        return propertyQueryService.findUnitByPublicId(unitPublicId)
                .map(this::toUnitSummaryDto)
                .orElse(null);
    }

    private RateResponseDto.UnitSummaryDto toUnitSummaryDto(UnitSummary summary) {
        return RateResponseDto.UnitSummaryDto.builder()
                .publicId(summary.publicId())
                .propertyPublicId(summary.propertyPublicId())
                .propertyName(summary.propertyName())
                .internalName(summary.internalName())
                .type(summary.type())
                .floor(summary.floor())
                .internalNumber(summary.internalNumber())
                .build();
    }
}

