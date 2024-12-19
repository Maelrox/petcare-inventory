package com.petcaresuite.inventory.application.dto

import java.math.BigDecimal

data class ServiceFilterDTO(
    val id: Long?,
    val name: String?,
    val description: String? = null,
    val price: BigDecimal?,
    var companyId: Long? = null
) {

}