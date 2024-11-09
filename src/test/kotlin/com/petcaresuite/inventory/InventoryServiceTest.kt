package com.petcaresuite.inventory

import com.petcaresuite.inventory.application.dto.InventoryDTO
import com.petcaresuite.inventory.application.dto.InventoryFilterDTO
import com.petcaresuite.inventory.application.mapper.InventoryMapper
import com.petcaresuite.inventory.application.port.output.InventoryPersistencePort
import com.petcaresuite.inventory.application.service.InventoryService
import com.petcaresuite.inventory.application.service.messages.Responses
import com.petcaresuite.inventory.domain.model.Inventory
import com.petcaresuite.inventory.infrastructure.exception.InsufficientInventoryException
import com.petcaresuite.inventory.infrastructure.exception.LockAcquisitionException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@ExtendWith(MockitoExtension::class)
class InventoryServiceTest {

    @Mock
    private lateinit var inventoryPersistencePort: InventoryPersistencePort

    @Mock
    private lateinit var inventoryMapper: InventoryMapper

    @Mock
    private lateinit var redissonClient: RedissonClient

    @Mock
    private lateinit var rLock: RLock

    private lateinit var inventoryService: InventoryService
    private lateinit var mockInventoryDTO: InventoryDTO
    private lateinit var mockInventory: Inventory

    @BeforeEach
    fun setUp() {
        mockInventoryDTO = InventoryDTO(
            inventoryId = null,
            name = "Test Product",
            description = "Test Description",
            quantity = 100,
            price = BigDecimal("10.00"),
            companyId = 1L
        )

        mockInventory = Inventory(
            inventoryId = null,
            name = "Test Product",
            description = "Test Description",
            quantity = 100,
            price = BigDecimal("10.00"),
            companyId = 1L
        )

        inventoryService = InventoryService(
            inventoryPersistencePort,
            inventoryMapper,
            redissonClient
        )
    }

    @Test
    fun `save - successful inventory creation`() {
        // Given
        Mockito.`when`(inventoryMapper.toDomain(mockInventoryDTO)).thenReturn(mockInventory)
        Mockito.`when`(inventoryPersistencePort.save(mockInventory)).thenReturn(mockInventory)

        // When
        val result = inventoryService.save(mockInventoryDTO)

        // Then
        assert(result.message == Responses.INVENTORY_CREATED)
        Mockito.verify(inventoryMapper).toDomain(mockInventoryDTO)
        Mockito.verify(inventoryPersistencePort).save(mockInventory)
    }

    @Test
    fun `update - successful inventory update`() {
        // Given
        val updateDTO = mockInventoryDTO.copy(inventoryId = 1L)
        val updateInventory = mockInventory.copy(inventoryId = 1L)

        Mockito.`when`(inventoryMapper.toDomain(updateDTO)).thenReturn(updateInventory)
        Mockito.`when`(inventoryPersistencePort.findById(1L)).thenReturn(updateInventory)
        Mockito.`when`(inventoryPersistencePort.update(updateInventory)).thenReturn(updateInventory)

        // When
        val result = inventoryService.update(updateDTO)

        // Then
        assert(result?.message == Responses.INVENTORY_UPDATED)
        Mockito.verify(inventoryPersistencePort).findById(1L)
        Mockito.verify(inventoryPersistencePort).update(updateInventory)
    }

    @Test
    fun `getAllByFilterPaginated - returns filtered inventory successfully`() {
        // Given
        val pageable = PageRequest.of(0, 10)
        val filterDTO = InventoryFilterDTO(
            inventoryId = null,
            name = "Test",
            companyId = 1L
        )
        val inventoryList = listOf(mockInventory)
        val inventoryPage: Page<Inventory> = PageImpl(inventoryList)
        val inventoryDTOList = listOf(mockInventoryDTO)
        val expectedPage: Page<InventoryDTO> = PageImpl(inventoryDTOList)

        Mockito.`when`(inventoryMapper.toDomain(filterDTO)).thenReturn(mockInventory)
        Mockito.`when`(inventoryPersistencePort.findAllByFilterPaginated(mockInventory, pageable))
            .thenReturn(inventoryPage)
        Mockito.`when`(inventoryMapper.toDTO(mockInventory)).thenReturn(mockInventoryDTO)

        // When
        val result = inventoryService.getAllByFilterPaginated(filterDTO, pageable)

        // Then
        assert(result.content == expectedPage.content)
        Mockito.verify(inventoryMapper).toDomain(filterDTO)
        Mockito.verify(inventoryPersistencePort).findAllByFilterPaginated(mockInventory, pageable)
    }

    @Test
    fun `updateInventory - successful inventory quantity update with lock`() {
        // Given
        val companyId = 1L
        val inventoryId = 1L
        val updateDTO = mockInventoryDTO.copy(inventoryId = inventoryId, quantity = 10)
        val existingInventory = mockInventory.copy(inventoryId = inventoryId, quantity = 100)
        val updatedInventory = existingInventory.copy(quantity = 90)

        Mockito.`when`(redissonClient.getLock("inventory:$companyId")).thenReturn(rLock)
        Mockito.`when`(rLock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true)
        Mockito.`when`(rLock.isHeldByCurrentThread).thenReturn(true)
        Mockito.`when`(inventoryPersistencePort.findByInventoryIdAndCompanyId(inventoryId, companyId))
            .thenReturn(existingInventory)
        Mockito.`when`(inventoryPersistencePort.save(updatedInventory)).thenReturn(updatedInventory)

        // When
        val result = inventoryService.updateInventory(listOf(updateDTO), companyId)

        // Then
        assert(result?.success == true)
        assert(result?.message == Responses.INVENTORY_UPDATED)
        Mockito.verify(rLock).tryLock(5, 30, TimeUnit.SECONDS)
        Mockito.verify(inventoryPersistencePort).findByInventoryIdAndCompanyId(inventoryId, companyId)
        Mockito.verify(rLock).unlock()
    }

    @Test
    fun `updateInventory - throws LockAcquisitionException when lock cannot be acquired`() {
        // Given
        val companyId = 1L
        Mockito.`when`(redissonClient.getLock("inventory:$companyId")).thenReturn(rLock)
        Mockito.`when`(rLock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(false)

        // When/Then
        assertThrows<LockAcquisitionException> {
            inventoryService.updateInventory(listOf(mockInventoryDTO), companyId)
        }
    }

    @Test
    fun `updateInventory - throws InsufficientInventoryException when quantity is insufficient`() {
        // Given
        val companyId = 1L
        val inventoryId = 1L
        val updateDTO = mockInventoryDTO.copy(inventoryId = inventoryId, quantity = 150)
        val existingInventory = mockInventory.copy(inventoryId = inventoryId, quantity = 100)

        Mockito.`when`(redissonClient.getLock("inventory:$companyId")).thenReturn(rLock)
        Mockito.`when`(rLock.tryLock(5, 30, TimeUnit.SECONDS)).thenReturn(true)
        Mockito.`when`(rLock.isHeldByCurrentThread).thenReturn(true)
        Mockito.`when`(inventoryPersistencePort.findByInventoryIdAndCompanyId(inventoryId, companyId))
            .thenReturn(existingInventory)

        // When/Then
        assertThrows<InsufficientInventoryException> {
            inventoryService.updateInventory(listOf(updateDTO), companyId)
        }
        Mockito.verify(rLock).unlock()
    }
}