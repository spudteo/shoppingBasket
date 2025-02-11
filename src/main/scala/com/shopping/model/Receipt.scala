package com.shopping.model

import java.time.LocalDate

case class AppliedDiscount(name: String, amount: BigDecimal, description: String)

case class Receipt(
  date: LocalDate,
  items: Map[Item, Int],
  itemSubtotals: Map[Item, BigDecimal],
  subtotal: BigDecimal,
  appliedDiscounts: List[AppliedDiscount],
  total: BigDecimal
) {
  def hasDiscounts: Boolean     = appliedDiscounts.nonEmpty
  def totalDiscount: BigDecimal = appliedDiscounts.map(_.amount).sum

  def itemQuantity(item: Item): Int = items.getOrElse(item, 0)
}
