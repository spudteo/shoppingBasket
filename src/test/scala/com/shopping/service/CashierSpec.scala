package com.shopping.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.shopping.model._
import java.time.LocalDate

class CashierSpec extends AnyFlatSpec with Matchers {

  val testDate: LocalDate = LocalDate.parse("2024-01-15")

  val testItems = Map(
    "apples" -> Item("Apples", BigDecimal("1.00")),
    "soup"   -> Item("Soup", BigDecimal("0.65")),
    "bread"  -> Item("Bread", BigDecimal("0.80")),
    "milk"   -> Item("Milk", BigDecimal("1.30"))
  )

  "Cashier" should "apply multiple discounts correctly" in {
    val cashier = new Cashier()

    val appleDiscount = new PercentageDiscount(
      item = testItems("apples"),
      percentage = BigDecimal("0.1"),
      startDate = testDate.minusDays(1),
      endDate = testDate.plusDays(1)
    )

    val soupBreadOffer = new DiscountItemWithEnoughQuantityOfSomeItem(
      itemToBuy = testItems("soup"),
      itemToDiscount = testItems("bread"),
      quantityToBuyForDiscount = 2,
      quantityDiscounted = 1,
      discountPercentage = BigDecimal("0.5"),
      startDate = testDate.minusDays(1),
      endDate = testDate.plusDays(1)
    )

    val basket = Basket(List("Apples", "Apples", "Soup", "Soup", "Bread"))(testItems)

    val receipt = cashier.generateReceipt(basket, testDate, List(appleDiscount, soupBreadOffer))

    receipt.appliedDiscounts.size shouldBe 2

    val appleDiscountAmount = receipt.appliedDiscounts
      .find(_.name.contains("Apples"))
      .map(_.amount)
      .getOrElse(BigDecimal("0"))
    val soupBreadDiscountAmount = receipt.appliedDiscounts
      .find(_.name.contains("Soup"))
      .map(_.amount)
      .getOrElse(BigDecimal("0"))

    appleDiscountAmount shouldBe BigDecimal("0.20")
    soupBreadDiscountAmount shouldBe BigDecimal("0.40")

    val expectedSubTotal = BigDecimal("4.10")
    receipt.subtotal shouldBe expectedSubTotal

    val expectedTotal = BigDecimal("3.50")
    receipt.total shouldBe expectedTotal
  }

  it should "not apply expired discounts" in {
    val cashier = new Cashier()

    val expiredDiscount = new PercentageDiscount(
      item = testItems("apples"),
      percentage = BigDecimal("0.1"),
      startDate = testDate.minusDays(10),
      endDate = testDate.minusDays(1)
    )

    val basket  = Basket(List("Apples", "Apples"))(testItems)
    val receipt = cashier.generateReceipt(basket, testDate, List(expiredDiscount))

    receipt.appliedDiscounts shouldBe empty
    receipt.total shouldBe BigDecimal("2.00")
  }
}
