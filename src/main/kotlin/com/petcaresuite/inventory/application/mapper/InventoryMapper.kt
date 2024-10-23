package com.petcaresuite.inventory.application.mapper

import com.petcaresuite.inventory.application.dto.InventoryDTO
import com.petcaresuite.inventory.application.dto.InventoryFilterDTO
import com.petcaresuite.inventory.domain.model.Inventory
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface InventoryMapper {

    fun toDomain(inventoryDTO: InventoryDTO): Inventory

    fun toDTO(inventory: Inventory): InventoryDTO

    fun toDTO(inventories: List<Inventory>): List<InventoryDTO>

    fun toDomain(ownerDTO: InventoryFilterDTO): Inventory

}