package com.petcaresuite.inventory.application.dto

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class InventoryDTO(
    val inventoryId: Long?,
    val name: String,
    val description: String,
    val quantity: Int,
    val price: BigDecimal,
    var companyId: Long?
)