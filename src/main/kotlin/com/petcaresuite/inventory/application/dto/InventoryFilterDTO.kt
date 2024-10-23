package com.petcaresuite.inventory.application.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class InventoryFilterDTO(
    val inventoryId: Long?,
    val name: String?,
    var companyId: Long?
)