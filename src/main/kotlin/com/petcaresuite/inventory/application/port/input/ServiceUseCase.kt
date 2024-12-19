package com.petcaresuite.inventory.application.port.input

import com.petcaresuite.inventory.application.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ServiceUseCase {
    
    fun save(serviceDTO: ServiceDTO): ResponseDTO

    fun update(serviceDTO: ServiceDTO): ResponseDTO?

    fun getAllByFilterPaginated(filterDTO: ServiceFilterDTO, pageable: Pageable): Page<ServiceDTO>

}