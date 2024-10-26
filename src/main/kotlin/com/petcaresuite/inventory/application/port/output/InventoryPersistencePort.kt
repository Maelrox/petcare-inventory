package com.petcaresuite.inventory.application.port.output

import com.petcaresuite.inventory.domain.model.Inventory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InventoryPersistencePort {

     fun save(inventory: Inventory): Inventory

     fun findAllByFilterPaginated(filter: Inventory, pageable: Pageable): Page<Inventory>

     fun findById(inventoryId: Long): Inventory

     fun update(inventory: Inventory): Inventory

     fun findByInventoryIdAndCompanyId(inventoryId: Long?, companyId: Long): Inventory?

}