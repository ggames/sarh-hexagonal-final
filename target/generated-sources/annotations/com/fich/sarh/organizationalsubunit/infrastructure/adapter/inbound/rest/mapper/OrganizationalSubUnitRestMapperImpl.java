package com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.mapper;

import com.fich.sarh.organizationalsubunit.domain.model.OrganizationalSubUnit;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.request.OrganizationalSubunitRequest;
import com.fich.sarh.organizationalsubunit.infrastructure.adapter.inbound.rest.model.response.OrganizationalSubUnitResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T13:11:00-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class OrganizationalSubUnitRestMapperImpl implements OrganizationalSubUnitRestMapper {

    @Override
    public OrganizationalSubUnit toOrganizationalSubUnit(OrganizationalSubunitRequest request) {
        if ( request == null ) {
            return null;
        }

        OrganizationalSubUnit.OrganizationalSubUnitBuilder organizationalSubUnit = OrganizationalSubUnit.builder();

        organizationalSubUnit.nameSubUnit( request.getNameSubUnit() );
        organizationalSubUnit.guaraniCode( request.getGuaraniCode() );
        organizationalSubUnit.organizationalUnit( request.getOrganizationalUnit() );

        return organizationalSubUnit.build();
    }

    @Override
    public OrganizationalSubUnitResponse toOrganizationalSubUnit(OrganizationalSubUnit subunit) {
        if ( subunit == null ) {
            return null;
        }

        OrganizationalSubUnitResponse.OrganizationalSubUnitResponseBuilder organizationalSubUnitResponse = OrganizationalSubUnitResponse.builder();

        organizationalSubUnitResponse.nameSubUnit( subunit.getNameSubUnit() );
        organizationalSubUnitResponse.guaraniCode( subunit.getGuaraniCode() );
        organizationalSubUnitResponse.organizationalUnit( subunit.getOrganizationalUnit() );

        return organizationalSubUnitResponse.build();
    }
}
