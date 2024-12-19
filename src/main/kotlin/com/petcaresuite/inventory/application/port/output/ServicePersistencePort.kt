package com.petcaresuite.inventory.application.port.output

import com.petcaresuite.inventory.domain.model.Service
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ServicePersistencePort {

     fun save(service: Service): Service

     fun findAllByFilterPaginated(filter: Service, pageable: Pageable): Page<Service>

     fun findById(serviceId: Long): Service

     fun update(service: Service): Service

     fun findByIdAndCompanyId(serviceId: Long?, companyId: Long): Service?

}