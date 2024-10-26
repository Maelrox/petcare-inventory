package com.petcaresuite.inventory.infrastructure.persistence.adapter

import com.petcaresuite.inventory.application.port.output.InventoryPersistencePort
import com.petcaresuite.inventory.domain.model.Inventory
import com.petcaresuite.inventory.infrastructure.persistence.mapper.InventoryEntityMapper
import com.petcaresuite.inventory.infrastructure.persistence.repository.JpaInventoryRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class InventoryRepositoryAdapter(
    private val jpaInventoryRepository: JpaInventoryRepository,
    private val inventoryMapper: InventoryEntityMapper
) : InventoryPersistencePort {

    override fun findById(inventoryId: Long): Inventory {
        val ownerEntity = jpaInventoryRepository.findById(inventoryId)
            .orElseThrow { EntityNotFoundException("Inventory with id $inventoryId not found") }
        return inventoryMapper.toDomain(ownerEntity)
    }

    override fun update(inventory: Inventory): Inventory {
        val inventoryEntity = inventoryMapper.toEntity(inventory)
        jpaInventoryRepository.save(inventoryEntity)
        return inventoryMapper.toDomain(inventoryEntity)
    }

    override fun findByInventoryIdAndCompanyId(inventoryId: Long?, companyId: Long): Inventory? {
        return inventoryMapper.toDomain(jpaInventoryRepository.findByInventoryIdAndCompanyId(inventoryId, companyId))
    }

    override fun save(inventory: Inventory): Inventory {
        val inventoryEntity = inventoryMapper.toEntity(inventory)
        jpaInventoryRepository.save(inventoryEntity)
        return inventoryMapper.toDomain(inventoryEntity)
    }

    override fun findAllByFilterPaginated(filter: Inventory, pageable: Pageable): Page<Inventory> {
        val pagedRolesEntity = jpaInventoryRepository.findAllByFilter(filter, pageable)
        return pagedRolesEntity.map { inventoryMapper.toDomain(it) }
    }

}