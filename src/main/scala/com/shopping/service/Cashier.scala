package com.shopping.service

import java.time.LocalDate

import com.shopping.model._

class Cashier {

  private def calculateItemSubtotals(items: Map[Item, Int]): Map[Item, BigDecimal] =
    items.map {
      case (item, quantity) =>
        item -> (item.price * quantity).setScale(2, BigDecimal.RoundingMode.HALF_DOWN)
    }

  private def calculateSubtotal(itemSubtotals: Map[Item, BigDecimal]): BigDecimal =
    itemSubtotals.values.sum.setScale(2, BigDecimal.RoundingMode.HALF_DOWN)

  private def calculateDiscounts(
    basket: Basket,
    date: LocalDate,
    offers: List[Offer]
  ): List[AppliedDiscount] =
    offers
      .filter(_.isValid(date))
      .flatMap {
        offer =>
          val amount = offer.apply(basket)
          if (amount != 0) {
            Some(
              AppliedDiscount(name = offer.name, amount = amount, description = offer.description)
            )
          } else None
      }

  def generateReceipt(basket: Basket, date: LocalDate, offers: List[Offer]): Receipt = {
    val itemSubtotals = calculateItemSubtotals(basket.items)
    val subtotal      = calculateSubtotal(itemSubtotals)
    val discounts     = calculateDiscounts(basket, date, offers)
    val total = (subtotal - discounts.map(_.amount).sum)
      .setScale(2, BigDecimal.RoundingMode.HALF_DOWN)

    Receipt(
      date = date,
      items = basket.items,
      itemSubtotals = itemSubtotals,
      subtotal = subtotal,
      appliedDiscounts = discounts,
      total = total
    )
  }
}
