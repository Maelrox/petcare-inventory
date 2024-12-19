package com.petcaresuite.inventory.infrastructure.persistence.adapter

import com.petcaresuite.inventory.application.port.output.ServicePersistencePort
import com.petcaresuite.inventory.domain.model.Service
import com.petcaresuite.inventory.infrastructure.persistence.mapper.ServiceEntityMapper
import com.petcaresuite.inventory.infrastructure.persistence.repository.JpaServiceRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ServiceRepositoryAdapter(
    private val jpaServiceRepository: JpaServiceRepository,
    private val serviceMapper: ServiceEntityMapper
) : ServicePersistencePort {

    override fun findById(serviceId: Long): Service {
        val serviceEntity = jpaServiceRepository.findById(serviceId)
            .orElseThrow { EntityNotFoundException("Service with id $serviceId not found") }
        return serviceMapper.toDomain(serviceEntity)
    }

    override fun update(service: Service): Service {
        val serviceEntity = serviceMapper.toEntity(service)
        jpaServiceRepository.save(serviceEntity)
        return serviceMapper.toDomain(serviceEntity)
    }

    override fun findByIdAndCompanyId(serviceId: Long?, companyId: Long): Service? {
        return serviceMapper.toDomain(jpaServiceRepository.findByIdAndCompanyId(serviceId, companyId))
    }

    override fun save(service: Service): Service {
        val serviceEntity = serviceMapper.toEntity(service)
        jpaServiceRepository.save(serviceEntity)
        return serviceMapper.toDomain(serviceEntity)
    }

    override fun findAllByFilterPaginated(filter: Service, pageable: Pageable): Page<Service> {
        val pagedRolesEntity = jpaServiceRepository.findAllByFilter(filter, pageable)
        return pagedRolesEntity.map { serviceMapper.toDomain(it) }
    }

}