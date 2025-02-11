package com.shopping.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.time.LocalDate

class OfferSpec extends AnyFlatSpec with Matchers {

  val testDate: LocalDate = LocalDate.parse("2024-01-15")

  val testItems = Map(
    "apples" -> Item("Apples", BigDecimal("1.00")),
    "soup"   -> Item("Soup", BigDecimal("0.65")),
    "bread"  -> Item("Bread", BigDecimal("0.80")),
    "milk"   -> Item("Milk", BigDecimal("1.30"))
  )

  "PercentageDiscount" should "apply correct percentage discount on items" in {
    val appleDiscount = new PercentageDiscount(
      item = testItems("apples"),
      percentage = BigDecimal("0.1"),
      startDate = testDate,
      endDate = testDate
    )

    val basket1 = Basket(List("Apples"))(testItems)
    appleDiscount.apply(basket1) shouldBe BigDecimal("0.10")

    val basket3 = Basket(List("Apples", "Soup", "Bread", "Apples"))(testItems)
    appleDiscount.apply(basket3) shouldBe BigDecimal("0.20")

    val basket4 = Basket(List("Soup", "Bread"))(testItems)
    appleDiscount.apply(basket4) shouldBe BigDecimal("0.00")
  }

  "DiscountItemWithEnoughQuantityOfSomeItem" should "correctly apply discounts based on quantity conditions" in {
    val soupBreadOffer = new DiscountItemWithEnoughQuantityOfSomeItem(
      itemToBuy = testItems("soup"),
      itemToDiscount = testItems("bread"),
      quantityToBuyForDiscount = 2,
      quantityDiscounted = 1,
      discountPercentage = BigDecimal("0.5"),
      startDate = testDate,
      endDate = testDate,
      enabled = true,
      stackable = true
    )

    val notEnoughSoupBasket = Basket(List("Soup", "Bread"))(testItems)
    soupBreadOffer.apply(notEnoughSoupBasket) shouldBe BigDecimal("0.00")

    val exactlySoupBasket = Basket(List("Soup", "Soup", "Bread"))(testItems)
    soupBreadOffer.apply(exactlySoupBasket) shouldBe BigDecimal("0.40")

    val moreSoupBasket = Basket(List("Soup", "Soup", "Soup", "Bread"))(testItems)
    soupBreadOffer.apply(moreSoupBasket) shouldBe BigDecimal("0.40")

    val Basket2Bread = Basket(List("Soup", "Soup", "Soup", "Soup", "Bread", "Bread"))(testItems)
    soupBreadOffer.apply(Basket2Bread) shouldBe BigDecimal("0.80")

    val milkOffer = new DiscountItemWithEnoughQuantityOfSomeItem(
      itemToBuy = testItems("milk"),
      itemToDiscount = testItems("milk"),
      quantityToBuyForDiscount = 3,
      quantityDiscounted = 1,
      discountPercentage = BigDecimal("1.0"),
      startDate = testDate,
      endDate = testDate,
      enabled = true,
      stackable = true
    )

    val fourMilkBasket = Basket(List("Milk", "Milk", "Milk", "Milk"))(testItems)
    milkOffer.apply(fourMilkBasket) shouldBe BigDecimal("1.30")
  }

}
