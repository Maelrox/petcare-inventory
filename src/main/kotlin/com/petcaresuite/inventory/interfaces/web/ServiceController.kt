package com.petcaresuite.inventory.interfaces.web

import com.petcaresuite.appointment.application.service.modules.ModuleActions
import com.petcaresuite.inventory.application.dto.*
import com.petcaresuite.inventory.application.port.input.ServiceUseCase
import com.petcaresuite.inventory.application.service.modules.Modules
import com.petcaresuite.inventory.infrastructure.security.Permissions
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/service")
@RestController
class ServiceController(private val serviceUseCase: ServiceUseCase) {

    @PostMapping()
    @Permissions(Modules.SERVICE, ModuleActions.CREATE)
    fun saveOwner(@RequestBody serviceDTO: ServiceDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        serviceDTO.companyId = companyId
        return ResponseEntity.ok(serviceUseCase.save(serviceDTO))
    }

    @PutMapping()
    @Permissions(Modules.SERVICE, ModuleActions.UPDATE)
    fun updateOwner(@RequestBody serviceDTO: ServiceDTO, request: HttpServletRequest): ResponseEntity<ResponseDTO> {
        val companyId  = request.getAttribute("companyId").toString().toLong()
        serviceDTO.companyId = companyId
        return ResponseEntity.ok(serviceUseCase.update(serviceDTO))
    }

    @GetMapping("/search")
    @Permissions(Modules.SERVICE, ModuleActions.VIEW)
    fun getAllPermissionsByFilter(@ModelAttribute serviceDTO: ServiceFilterDTO, @RequestParam(defaultValue = "0") page: Int, @RequestParam(defaultValue = "30") size: Int, request: HttpServletRequest): ResponseEntity<PaginatedResponseDTO<ServiceDTO>> {
        val pageable = PageRequest.of(page, size)
        val companyId  = request.getAttribute("companyId").toString().toLong()
        serviceDTO.companyId = companyId
        val result = serviceUseCase.getAllByFilterPaginated(serviceDTO, pageable)

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