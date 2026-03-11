package it.andrea.insula.owner.internal.owner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class BankAccount {

    @Column(name = "bank_iban", length = 34)
    private String iban;

    @Column(name = "bank_swift_bic", length = 11)
    private String swiftBic;

    @Column(name = "bank_extra_eu_account")
    private String extraEuAccountNumber;

    @Column(name = "bank_routing_code")
    private String routingCode;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_country_code", length = 2)
    private String bankCountryCode;

    @Column(name = "bank_holder_name")
    private String holderName;
}