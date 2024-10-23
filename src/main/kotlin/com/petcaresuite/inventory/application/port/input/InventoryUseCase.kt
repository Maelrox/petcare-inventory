package com.petcaresuite.inventory.application.port.input

import com.petcaresuite.inventory.application.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryUseCase {
    
    fun save(inventoryDTO: InventoryDTO): ResponseDTO

    fun update(inventoryDTO: InventoryDTO): ResponseDTO?

    fun getAllByFilterPaginated(filterDTO: InventoryFilterDTO, pageable: Pageable): Page<InventoryDTO>

}