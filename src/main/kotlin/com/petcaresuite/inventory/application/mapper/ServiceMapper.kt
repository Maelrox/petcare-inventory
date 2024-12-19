package com.petcaresuite.inventory.application.mapper

import com.petcaresuite.inventory.application.dto.ServiceDTO
import com.petcaresuite.inventory.application.dto.ServiceFilterDTO
import com.petcaresuite.inventory.domain.model.Service
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ServiceMapper {

    fun toDTO(service: Service): ServiceDTO

    fun toDTO(services: List<Service>): List<ServiceDTO>

    fun toDomain(serviceDTO: ServiceDTO): Service

    fun toDomain(serviceFilterDTO: ServiceFilterDTO): Service

}