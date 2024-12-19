package com.petcaresuite.inventory.infrastructure.persistence.mapper

import com.petcaresuite.inventory.domain.model.Service
import com.petcaresuite.inventory.infrastructure.persistence.entity.ServiceEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ServiceEntityMapper {

    fun toEntity(service: Service): ServiceEntity

    fun toDomain(serviceEntity: ServiceEntity): Service

    fun toDomain(services: List<ServiceEntity>): List<Service>

}