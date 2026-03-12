package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListCreateDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PriceListCreateMapperTest {

    private final PriceListCreateMapper mapper = new PriceListCreateMapper();

    @Test
    void apply_shouldMapAllFields() {
        UUID parentId = UUID.randomUUID();
        PriceListCreateDto dto = new PriceListCreateDto(
                "Summer 2025", "Summer season prices", true,
                "EUR", parentId, new BigDecimal("10.00"), new BigDecimal("5.00")
        );

        PriceList result = mapper.apply(dto);

        assertThat(result.getName()).isEqualTo("Summer 2025");
        assertThat(result.getDescription()).isEqualTo("Summer season prices");
        assertThat(result.isDefault()).isTrue();
        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getPercentageAdjustment()).isEqualByComparingTo("10.00");
        assertThat(result.getFlatAdjustment()).isEqualByComparingTo("5.00");
        assertThat(result.getStatus()).isEqualTo(PriceListStatus.ACTIVE);
    }

    @Test
    void apply_shouldDefaultCurrencyToEur() {
        PriceListCreateDto dto = new PriceListCreateDto(
                "Winter 2025", null, false,
                null, null, null, null
        );

        PriceList result = mapper.apply(dto);

        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.isDefault()).isFalse();
        assertThat(result.getDescription()).isNull();
        assertThat(result.getPercentageAdjustment()).isNull();
        assertThat(result.getFlatAdjustment()).isNull();
    }

    @Test
    void apply_shouldUppercaseCurrency() {
        PriceListCreateDto dto = new PriceListCreateDto(
                "Test", null, false,
                "usd", null, null, null
        );

        PriceList result = mapper.apply(dto);

        assertThat(result.getCurrency()).isEqualTo("USD");
    }
}

