package com.petcaresuite.inventory.infrastructure.persistence.repository

import com.petcaresuite.inventory.domain.model.Service
import com.petcaresuite.inventory.infrastructure.persistence.entity.ServiceEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaServiceRepository : JpaRepository<ServiceEntity, Long> {

     @Query(
        """
            SELECT s FROM ServiceEntity s
            WHERE s.companyId = :#{#filter.companyId}
            AND (
                :#{#filter.name} IS NULL 
                OR LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#filter.name}, '%'))
            ) 
            ORDER BY s.id desc 
            """
    )
    fun findAllByFilter(filter: Service, pageable: Pageable): Page<ServiceEntity>

    fun findByIdAndCompanyId(serviceId: Long?, companyId: Long): ServiceEntity

}
