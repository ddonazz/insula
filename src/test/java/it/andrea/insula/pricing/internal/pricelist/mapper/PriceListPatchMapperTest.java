package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListPatchDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceListPatchMapperTest {

    private final PriceListPatchMapper mapper = new PriceListPatchMapper();
    private PriceList priceList;

    @BeforeEach
    void setUp() {
        priceList = new PriceList();
        priceList.setName("Original Name");
        priceList.setDescription("Original Description");
        priceList.setDefault(false);
        priceList.setCurrency("EUR");
        priceList.setStatus(PriceListStatus.ACTIVE);
        priceList.setPercentageAdjustment(new BigDecimal("10.00"));
        priceList.setFlatAdjustment(new BigDecimal("5.00"));
    }

    @Test
    void apply_shouldUpdateAllFieldsWhenProvided() {
        PriceListPatchDto dto = new PriceListPatchDto(
                "New Name", "New Description", true,
                "usd", PriceListStatus.ARCHIVED, null,
                new BigDecimal("20.00"), new BigDecimal("15.00")
        );

        PriceList result = mapper.apply(dto, priceList);

        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.isDefault()).isTrue();
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo(PriceListStatus.ARCHIVED);
        assertThat(result.getPercentageAdjustment()).isEqualByComparingTo("20.00");
        assertThat(result.getFlatAdjustment()).isEqualByComparingTo("15.00");
    }

    @Test
    void apply_shouldSkipNullFields() {
        PriceListPatchDto dto = new PriceListPatchDto(
                null, null, null, null, null, null, null, null
        );

        PriceList result = mapper.apply(dto, priceList);

        assertThat(result.getName()).isEqualTo("Original Name");
        assertThat(result.getDescription()).isEqualTo("Original Description");
        assertThat(result.isDefault()).isFalse();
        assertThat(result.getCurrency()).isEqualTo("EUR");
        assertThat(result.getStatus()).isEqualTo(PriceListStatus.ACTIVE);
        assertThat(result.getPercentageAdjustment()).isEqualByComparingTo("10.00");
        assertThat(result.getFlatAdjustment()).isEqualByComparingTo("5.00");
    }

    @Test
    void apply_shouldUpdateOnlyName() {
        PriceListPatchDto dto = new PriceListPatchDto(
                "Updated Name", null, null, null, null, null, null, null
        );

        PriceList result = mapper.apply(dto, priceList);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Original Description");
    }

    @Test
    void apply_shouldReturnSameInstance() {
        PriceListPatchDto dto = new PriceListPatchDto(
                null, null, null, null, null, null, null, null
        );

        PriceList result = mapper.apply(dto, priceList);

        assertThat(result).isSameAs(priceList);
    }
}

