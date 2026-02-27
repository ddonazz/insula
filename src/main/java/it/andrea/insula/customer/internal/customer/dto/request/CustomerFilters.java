package it.andrea.insula.customer.internal.customer.dto.request;

import it.andrea.insula.customer.internal.customer.model.CustomerType;

public record CustomerFilters(
        String name,
        String email,
        String fiscalCode,
        CustomerType customerType
) {
}
