package it.andrea.insula.pricing.internal.pricelist.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceListValidatorTest {

    @Mock
    private PriceListRepository priceListRepository;

    @InjectMocks
    private PriceListValidator validator;

    // ─── validateCreate ──────────────────────────────────────────────────

    @Test
    void validateCreate_shouldPassWhenValid() {
        when(priceListRepository.existsByName("Summer")).thenReturn(false);
        when(priceListRepository.existsByIsDefaultTrue()).thenReturn(false);

        assertThatCode(() -> validator.validateCreate("Summer", true, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCreate_shouldThrowWhenNameInUse() {
        when(priceListRepository.existsByName("Summer")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateCreate("Summer", false, null))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateCreate_shouldThrowWhenDefaultAlreadyExists() {
        when(priceListRepository.existsByName("Summer")).thenReturn(false);
        when(priceListRepository.existsByIsDefaultTrue()).thenReturn(true);

        assertThatThrownBy(() -> validator.validateCreate("Summer", true, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateCreate_shouldThrowWhenParentNotFound() {
        UUID parentId = UUID.randomUUID();
        when(priceListRepository.existsByName("Derived")).thenReturn(false);
        when(priceListRepository.findByPublicId(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> validator.validateCreate("Derived", false, parentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateCreate_shouldThrowWhenParentIsDeleted() {
        UUID parentId = UUID.randomUUID();
        PriceList deletedParent = new PriceList();
        deletedParent.setStatus(PriceListStatus.DELETED);

        when(priceListRepository.existsByName("Derived")).thenReturn(false);
        when(priceListRepository.findByPublicId(parentId)).thenReturn(Optional.of(deletedParent));

        assertThatThrownBy(() -> validator.validateCreate("Derived", false, parentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateCreate_shouldPassWithValidParent() {
        UUID parentId = UUID.randomUUID();
        PriceList activeParent = new PriceList();
        activeParent.setStatus(PriceListStatus.ACTIVE);

        when(priceListRepository.existsByName("Derived")).thenReturn(false);
        when(priceListRepository.findByPublicId(parentId)).thenReturn(Optional.of(activeParent));

        assertThatCode(() -> validator.validateCreate("Derived", false, parentId))
                .doesNotThrowAnyException();
    }

    // ─── validateUpdate ──────────────────────────────────────────────────

    @Test
    void validateUpdate_shouldPassWhenNameUnchanged() {
        assertThatCode(() -> validator.validateUpdate(1L, "Same Name", "Same Name", false, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUpdate_shouldThrowWhenNewNameInUse() {
        when(priceListRepository.existsByNameAndIdNot("New Name", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdate(1L, "New Name", "Old Name", false, null))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateUpdate_shouldThrowWhenDefaultAlreadyExistsForOther() {
        when(priceListRepository.existsByIsDefaultTrueAndIdNot(1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdate(1L, "Same", "Same", true, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    // ─── validateDelete ──────────────────────────────────────────────────

    @Test
    void validateDelete_shouldPassWhenNoDerivedPriceLists() {
        PriceList priceList = new PriceList();
        priceList.setDerivedPriceLists(new HashSet<>());

        assertThatCode(() -> validator.validateDelete(priceList))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDelete_shouldThrowWhenActiveDerivedExists() {
        PriceList derived = new PriceList();
        derived.setStatus(PriceListStatus.ACTIVE);

        PriceList priceList = new PriceList();
        priceList.setDerivedPriceLists(Set.of(derived));

        assertThatThrownBy(() -> validator.validateDelete(priceList))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateDelete_shouldPassWhenAllDerivedAreDeleted() {
        PriceList derived = new PriceList();
        derived.setStatus(PriceListStatus.DELETED);

        PriceList priceList = new PriceList();
        priceList.setDerivedPriceLists(Set.of(derived));

        assertThatCode(() -> validator.validateDelete(priceList))
                .doesNotThrowAnyException();
    }
}

