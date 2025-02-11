package com.shopping.model

import java.time.LocalDate

import com.shopping.service.OfferLoader

trait Offer {
  def apply(basket: Basket): BigDecimal
  val name: String
  val description: String
  val startDate: LocalDate
  val endDate: LocalDate
  val enabled: Boolean
  val stackable: Boolean

  def isValid(currentDate: LocalDate): Boolean =
    enabled && currentDate.isAfter(startDate) && currentDate.isBefore(endDate)
}

/**
 * Represents a percentage discount on a specific item
 *
 * For example: "20% off on bananas" or "50% discount on milk"
 *
 * @param item The item to apply the discount to
 * @param percentage The discount percentage (e.g., 0.2 for 20% off)
 * @param startDate When the offer begins
 * @param endDate When the offer ends
 * @param enabled Whether the offer is currently active
 * @param stackable Whether this offer can be combined with other offers
 */
class PercentageDiscount(
  val item: Item,
  val percentage: BigDecimal,
  val startDate: LocalDate,
  val endDate: LocalDate,
  val enabled: Boolean = true,
  val stackable: Boolean = true
) extends Offer {
  val name        = s"${item.name} ${(percentage * 100).setScale(0)}% off"
  val description = s"${item.name} ${(percentage * 100).setScale(0)}% off"

  def apply(basket: Basket): BigDecimal =
    (item.price * basket.quantityOf(item) * percentage).setScale(2, BigDecimal.RoundingMode.DOWN)
}

/**
 * Represents a "Buy X Get Y" type of offer where buying a certain quantity of one item
 * gives a discount on another item (or the same item)
 *
 * For example: "Buy 2 apples, get 1 orange 50% off" or "Buy 2 get 1 free"
 *
 * @param itemToBuy The item that needs to be purchased to qualify for the discount
 * @param itemToDiscount The item that will receive the discount
 * @param quantityToBuyForDiscount How many items need to be bought to qualify for the discount
 * @param quantityDiscounted How many items will receive the discount
 * @param discountPercentage Amount of discount (1.0 for free, 0.5 for 50% off)
 * @param startDate When the offer begins
 * @param endDate When the offer ends
 * @param enabled Whether the offer is currently active
 * @param stackable Whether this offer can be combined with other offers
 */
class DiscountItemWithEnoughQuantityOfSomeItem(
  val itemToBuy: Item,
  val itemToDiscount: Item,
  val quantityToBuyForDiscount: Int,
  val quantityDiscounted: Int,
  val discountPercentage: BigDecimal,
  val startDate: LocalDate,
  val endDate: LocalDate,
  val enabled: Boolean = true,
  val stackable: Boolean = true
) extends Offer {

  val name =
    s"Buy $quantityToBuyForDiscount ${itemToBuy.name} Get $quantityDiscounted ${itemToDiscount.name} ${if (discountPercentage == 1) "Free"
      else s"${(discountPercentage * 100).setScale(0)}% off"}"
  val description = name

  def apply(basket: Basket): BigDecimal = {
    val totalItemBought     = basket.quantityOf(itemToBuy)
    val totalItemDiscounted = basket.quantityOf(itemToDiscount)
    val totalEligibleItemToDiscount =
      (totalItemBought / quantityToBuyForDiscount) * quantityDiscounted
    val totalDiscounted = totalItemDiscounted.min(totalEligibleItemToDiscount)

    (itemToDiscount.price * totalDiscounted * discountPercentage)
      .setScale(2, BigDecimal.RoundingMode.DOWN)
  }

}

object Offers {

  def loadActiveOffers(path: String, availableItems: Map[String, Item]): List[Offer] =
    OfferLoader.loadOffers(path, availableItems).filter(_.enabled)
}
