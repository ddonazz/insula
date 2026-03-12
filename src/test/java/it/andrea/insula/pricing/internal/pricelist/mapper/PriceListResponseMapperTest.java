package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.pricing.internal.pricelist.dto.response.PriceListResponseDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceListResponseMapperTest {

    @Mock
    private EnumTranslator enumTranslator;

    private PriceListResponseMapper mapper;
    private PriceList priceList;

    @BeforeEach
    void setUp() {
        mapper = new PriceListResponseMapper(enumTranslator);

        priceList = new PriceList();
        priceList.setPublicId(UUID.randomUUID());
        priceList.setName("Summer 2025");
        priceList.setDescription("Summer season prices");
        priceList.setDefault(true);
        priceList.setCurrency("EUR");
        priceList.setStatus(PriceListStatus.ACTIVE);
        priceList.setPercentageAdjustment(new BigDecimal("10.00"));
        priceList.setFlatAdjustment(new BigDecimal("5.00"));
    }

    @Test
    void apply_shouldMapAllFields() {
        when(enumTranslator.translate(PriceListStatus.ACTIVE))
                .thenReturn(new TranslatedEnum("ACTIVE", "Active"));

        PriceListResponseDto result = mapper.apply(priceList);

        assertThat(result.publicId()).isEqualTo(priceList.getPublicId());
        assertThat(result.name()).isEqualTo("Summer 2025");
        assertThat(result.description()).isEqualTo("Summer season prices");
        assertThat(result.isDefault()).isTrue();
        assertThat(result.currency()).isEqualTo("EUR");
        assertThat(result.status().code()).isEqualTo("ACTIVE");
        assertThat(result.status().label()).isEqualTo("Active");
        assertThat(result.percentageAdjustment()).isEqualByComparingTo("10.00");
        assertThat(result.flatAdjustment()).isEqualByComparingTo("5.00");
        assertThat(result.parentPriceListPublicId()).isNull();
        assertThat(result.parentPriceListName()).isNull();
    }

    @Test
    void apply_shouldIncludeParentPriceListInfo() {
        PriceList parent = new PriceList();
        parent.setPublicId(UUID.randomUUID());
        parent.setName("Base Price List");
        priceList.setParentPriceList(parent);

        when(enumTranslator.translate(PriceListStatus.ACTIVE))
                .thenReturn(new TranslatedEnum("ACTIVE", "Active"));

        PriceListResponseDto result = mapper.apply(priceList);

        assertThat(result.parentPriceListPublicId()).isEqualTo(parent.getPublicId());
        assertThat(result.parentPriceListName()).isEqualTo("Base Price List");
    }

    @Test
    void apply_shouldHandleNullDescription() {
        priceList.setDescription(null);
        priceList.setPercentageAdjustment(null);
        priceList.setFlatAdjustment(null);

        when(enumTranslator.translate(PriceListStatus.ACTIVE))
                .thenReturn(new TranslatedEnum("ACTIVE", "Active"));

        PriceListResponseDto result = mapper.apply(priceList);

        assertThat(result.description()).isNull();
        assertThat(result.percentageAdjustment()).isNull();
        assertThat(result.flatAdjustment()).isNull();
    }
}

