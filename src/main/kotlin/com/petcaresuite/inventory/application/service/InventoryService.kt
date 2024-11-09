package com.petcaresuite.inventory.application.service

import com.petcaresuite.inventory.application.service.messages.InternalErrors
import com.petcaresuite.inventory.application.dto.*
import com.petcaresuite.inventory.application.mapper.InventoryMapper
import com.petcaresuite.inventory.application.port.input.InventoryUseCase
import com.petcaresuite.inventory.application.port.output.InventoryPersistencePort
import com.petcaresuite.inventory.application.service.messages.Responses
import com.petcaresuite.inventory.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.inventory.infrastructure.exception.LockAcquisitionException
import com.petcaresuite.inventory.interfaces.exception.RestExceptionHandler
import jakarta.transaction.Transactional
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class InventoryService(
    private val inventoryPersistencePort: InventoryPersistencePort,
    private val inventoryMapper: InventoryMapper,
    private val redissonClient: RedissonClient
) :
    InventoryUseCase {

    private val logger: Logger = LoggerFactory.getLogger(RestExceptionHandler::class.java)

    override fun save(inventoryDTO: InventoryDTO): ResponseDTO {
        val veterinary = inventoryMapper.toDomain(inventoryDTO)
        inventoryPersistencePort.save(veterinary)
        return ResponseDTO(message = Responses.INVENTORY_CREATED)
    }

    override fun update(inventoryDTO: InventoryDTO): ResponseDTO? {
        val veterinary = inventoryMapper.toDomain(inventoryDTO)
        inventoryPersistencePort.findById(inventoryDTO.inventoryId!!)
        inventoryPersistencePort.update(veterinary)
        return ResponseDTO(message = Responses.INVENTORY_UPDATED)
    }

    override fun getAllByFilterPaginated(filterDTO: InventoryFilterDTO, pageable: Pageable): Page<InventoryDTO> {
        val filter = inventoryMapper.toDomain(filterDTO)
        return inventoryPersistencePort.findAllByFilterPaginated(filter, pageable)
            .map { inventoryMapper.toDTO(it) }
    }

    @Transactional
    override fun updateInventory(inventories: List<InventoryDTO>, companyId: Long): ResponseDTO? {
        val lockKey = "inventory:${companyId}"
        val lock = redissonClient.getLock(lockKey)

        try {
            if (!lock.tryLock(5, 30, TimeUnit.SECONDS)) {
                throw LockAcquisitionException("Could not acquire lock for company $companyId")
            }

            inventories.forEach { inventoryDTO ->
                val inventory = inventoryPersistencePort.findByInventoryIdAndCompanyId(
                    inventoryDTO.inventoryId,
                    companyId
                ) ?: throw Exception("Inventory not found")

                if (inventory.quantity!! < inventoryDTO.quantity) {
                    throw InsufficientInventoryException()
                }

                inventory.quantity = inventory.quantity!! - inventoryDTO.quantity
                inventoryPersistencePort.save(inventory)
            }

            return ResponseDTO(success = true, message = Responses.INVENTORY_UPDATED)
        } finally {
            if (lock.isHeldByCurrentThread) {
                try {
                    lock.unlock()
                } catch (ex: Exception) {
                    logger.error(InternalErrors.REDIS_EXCEPTION.format(ex.message))
                }
            }
        }
    }


}