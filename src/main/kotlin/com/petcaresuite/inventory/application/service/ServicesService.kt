package com.petcaresuite.inventory.application.service

import com.petcaresuite.inventory.application.dto.*
import com.petcaresuite.inventory.application.mapper.ServiceMapper
import com.petcaresuite.inventory.application.port.input.ServiceUseCase
import com.petcaresuite.inventory.application.port.output.ServicePersistencePort
import com.petcaresuite.inventory.application.service.messages.Responses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ServicesService(
    private val servicePersistencePort: ServicePersistencePort,
    private val serviceMapper: ServiceMapper,
) :
    ServiceUseCase {

    override fun save(serviceDTO: ServiceDTO): ResponseDTO {
        val service = serviceMapper.toDomain(serviceDTO)
        servicePersistencePort.save(service)
        return ResponseDTO(message = Responses.SERVICE_CREATED)
    }

    override fun update(serviceDTO: ServiceDTO): ResponseDTO? {
        val service = serviceMapper.toDomain(serviceDTO)
        servicePersistencePort.findById(serviceDTO.id)
        servicePersistencePort.update(service)
        return ResponseDTO(message = Responses.INVENTORY_UPDATED)
    }

    override fun getAllByFilterPaginated(filterDTO: ServiceFilterDTO, pageable: Pageable): Page<ServiceDTO> {
        val filter = serviceMapper.toDomain(filterDTO)
        return servicePersistencePort.findAllByFilterPaginated(filter, pageable)
            .map { serviceMapper.toDTO(it) }
    }

}