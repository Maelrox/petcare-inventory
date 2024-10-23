package com.petcaresuite.inventory.infrastructure.persistence.mapper

import com.petcaresuite.inventory.domain.model.Inventory
import com.petcaresuite.inventory.infrastructure.persistence.entity.InventoryEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface InventoryEntityMapper {

    fun toEntity(inventory: Inventory): InventoryEntity

    fun toDomain(inventoryEntity: InventoryEntity): Inventory

    fun toDomain(owners: List<InventoryEntity>): List<Inventory>

}