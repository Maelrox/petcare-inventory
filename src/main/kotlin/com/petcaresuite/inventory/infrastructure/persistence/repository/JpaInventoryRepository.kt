package com.petcaresuite.inventory.infrastructure.persistence.repository

import com.petcaresuite.inventory.domain.model.Inventory
import com.petcaresuite.inventory.infrastructure.persistence.entity.InventoryEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface JpaInventoryRepository : JpaRepository<InventoryEntity, Long> {

     @Query(
        """
            SELECT i FROM InventoryEntity i
            WHERE i.companyId = :#{#filter.companyId}
            AND (
                :#{#filter.name} IS NULL 
                OR LOWER(i.name) LIKE LOWER(CONCAT('%', :#{#filter.name}, '%'))
            ) 
            ORDER BY i.inventoryId desc 
            """
    )
    fun findAllByFilter(filter: Inventory, pageable: Pageable): Page<InventoryEntity>
}
