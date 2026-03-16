package it.andrea.insula.pricing.internal.season.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonGenerateDto;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonGenerateResultDto;
import it.andrea.insula.pricing.internal.season.exception.SeasonErrorCodes;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeasonGenerationService {

    private final SeasonPeriodRepository seasonRepository;
    private final UnitRateDayRepository unitRateDayRepository;
    private final PriceListRepository priceListRepository;

    @Transactional
    public SeasonGenerateResultDto generate(UUID priceListPublicId, UUID seasonPublicId, SeasonGenerateDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        SeasonPeriod season = seasonRepository.findByPublicId(seasonPublicId)
                .orElseThrow(() -> new ResourceNotFoundException(SeasonErrorCodes.SEASON_NOT_FOUND, seasonPublicId));

        if (season.getPriceList() == null || !priceListPublicId.equals(season.getPriceList().getPublicId())) {
            throw new BusinessRuleException(SeasonErrorCodes.SEASON_PRICE_LIST_MISMATCH);
        }
        if (season.getStatus() == SeasonStatus.DELETED) {
            throw new BusinessRuleException(SeasonErrorCodes.SEASON_ALREADY_DELETED, seasonPublicId);
        }

        List<UUID> unitPublicIds = Optional.ofNullable(dto.unitPublicIds()).orElse(List.of()).stream().distinct().toList();
        if (unitPublicIds.isEmpty()) {
            throw new BusinessRuleException(SeasonErrorCodes.SEASON_UNIT_LIST_EMPTY);
        }

        int generated = 0;
        int skippedManual = 0;

        for (UUID unitPublicId : unitPublicIds) {
            if (dto.overwriteManual()) {
                unitRateDayRepository.deleteBySourceSeasonIdAndUnitPublicId(season.getId(), unitPublicId);
            }

            List<UnitRateDay> existingDays = unitRateDayRepository
                    .findByPriceListPublicIdAndUnitPublicIdAndStayDateBetweenOrderByStayDate(
                            priceListPublicId,
                            unitPublicId,
                            season.getStartDate(),
                            season.getEndDate()
                    );

            Map<LocalDate, UnitRateDay> byDate = new HashMap<>();
            for (UnitRateDay day : existingDays) {
                byDate.put(day.getStayDate(), day);
            }

            List<UnitRateDay> toSave = new ArrayList<>();
            for (LocalDate date = season.getStartDate(); !date.isAfter(season.getEndDate()); date = date.plusDays(1)) {
                UnitRateDay current = byDate.get(date);

                if (current != null && current.getSourceSeason() == null && !dto.overwriteManual()) {
                    skippedManual++;
                    continue;
                }

                UnitRateDay target = current != null ? current : new UnitRateDay();
                if (target.getId() == null) {
                    target.setPriceList(priceList);
                    target.setUnitPublicId(unitPublicId);
                    target.setStayDate(date);
                }

                target.setPricePerNight(dto.pricePerNight());
                target.setExtraGuestPrice(dto.extraGuestPrice());
                target.setMinStay(dto.minStay());
                target.setMaxStay(dto.maxStay());
                target.setSourceSeason(season);

                toSave.add(target);
            }

            if (!toSave.isEmpty()) {
                unitRateDayRepository.saveAll(toSave);
                generated += toSave.size();
            }
        }

        return SeasonGenerateResultDto.builder()
                .generated(generated)
                .skippedManual(skippedManual)
                .unitsProcessed(unitPublicIds.size())
                .from(season.getStartDate())
                .to(season.getEndDate())
                .build();
    }

    private PriceList findActivePriceList(UUID publicId) {
        return priceListRepository.findByPublicId(publicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, publicId));
    }
}

