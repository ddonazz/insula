package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.BusinessOwner;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.Owner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OwnerResponseMapper implements Function<Owner, OwnerResponseDto> {

    private final BusinessOwnerResponseMapper businessMapper;
    private final IndividualOwnerResponseMapper individualMapper;

    @Override
    public OwnerResponseDto apply(Owner owner) {
        return switch (owner) {
            case BusinessOwner bo -> businessMapper.apply(bo);
            case IndividualOwner io -> individualMapper.apply(io);
            default -> throw new IllegalArgumentException("Unknown owner type: " + owner.getClass().getSimpleName());
        };
    }
}

