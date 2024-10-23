package com.petcaresuite.inventory.application.service

import com.petcaresuite.inventory.application.dto.*
import com.petcaresuite.inventory.application.mapper.InventoryMapper
import com.petcaresuite.inventory.application.port.input.InventoryUseCase
import com.petcaresuite.inventory.application.port.output.InventoryPersistencePort
import com.petcaresuite.inventory.application.service.messages.Responses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class InventoryService(
    private val inventoryPersistencePort: InventoryPersistencePort,
    private val inventoryMapper: InventoryMapper,
) :
    InventoryUseCase {

    override fun save(inventoryDTO: InventoryDTO): ResponseDTO {
        val veterinary = inventoryMapper.toDomain(inventoryDTO)
        inventoryPersistencePort.save(veterinary)
        return ResponseDTO(message = Responses.OWNER_CREATED)
    }

    override fun update(inventoryDTO: InventoryDTO): ResponseDTO? {
        val veterinary = inventoryMapper.toDomain(inventoryDTO)
        inventoryPersistencePort.findById(inventoryDTO.inventoryId!!)
        inventoryPersistencePort.update(veterinary)
        return ResponseDTO(message = Responses.OWNER_UPDATED)
    }

    override fun getAllByFilterPaginated(filterDTO: InventoryFilterDTO, pageable: Pageable): Page<InventoryDTO> {
        val filter = inventoryMapper.toDomain(filterDTO)
        return inventoryPersistencePort.findAllByFilterPaginated(filter, pageable)
            .map { inventoryMapper.toDTO(it) }
    }

}