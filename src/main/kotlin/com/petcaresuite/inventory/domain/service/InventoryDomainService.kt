package com.petcaresuite.inventory.domain.service

import com.petcaresuite.inventory.application.port.output.InventoryPersistencePort
import org.springframework.stereotype.Service

@Service
class InventoryDomainService(private val inventoryPersistencePort: InventoryPersistencePort) {

    fun validateAppointment(name: String, id: Long) {

    }

}