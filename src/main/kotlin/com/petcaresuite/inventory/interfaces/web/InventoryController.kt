package com.petcaresuite.inventory.interfaces.web

import com.petcaresuite.appointment.application.service.modules.ModuleActions
import com.petcaresuite.inventory.application.dto.*
import com.petcaresuite.inventory.application.port.input.InventoryUseCase
import com.petcaresuite.inventory.application.service.modules.Modules
import com.petcaresuite.inventory.infrastructure.security.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class InventoryController(private val inventoryUseCase: InventoryUseCase) {

    @PostMapping()
    @Permissions(Modules.INVENTORY, ModuleActions.CREATE)
    fun saveOwner(@RequestBody inventoryDTO: InventoryDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        inventoryDTO.companyId = companyId
        return ResponseEntity.ok(inventoryUseCase.save(inventoryDTO))
    }

    @PutMapping()
    @Permissions(Modules.INVENTORY, ModuleActions.UPDATE)
    fun updateOwner(@RequestBody inventoryDTO: InventoryDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        inventoryDTO.companyId = companyId
        return ResponseEntity.ok(inventoryUseCase.update(inventoryDTO))
    }

    @GetMapping("/search")
    @Permissions(Modules.INVENTORY, ModuleActions.VIEW)
    fun getAllPermissionsByFilter(@ModelAttribute filterDTO: InventoryFilterDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int, request: HttpServletRequest): ResponseEntity<PaginatedResponseDTO<InventoryDTO>> {
        val pageable = PageRequest.of(page, size)
        val companyId  = request.getAttribute("companyId").toString().toLong()
        filterDTO.companyId = companyId
        val result = inventoryUseCase.getAllByFilterPaginated(filterDTO, pageable)

        val pageDTO = PageDTO(
            page = result.number,
            size = result.size,
            totalElements = result.totalElements,
            totalPages = result.totalPages
        )

        val paginatedResponse = PaginatedResponseDTO(
            data = result.content,
            pagination = pageDTO
        )

        return ResponseEntity.ok(paginatedResponse)
    }

}