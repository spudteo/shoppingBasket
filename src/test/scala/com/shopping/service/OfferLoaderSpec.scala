package com.shopping.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.shopping.model.{ DiscountItemWithEnoughQuantityOfSomeItem, Item }
import java.time.LocalDate

class OfferLoaderSpec extends AnyFlatSpec with Matchers {

  val testItems = Map(
    "apples" -> Item("Apples", BigDecimal("1.00")),
    "soup"   -> Item("Soup", BigDecimal("0.65")),
    "bread"  -> Item("Bread", BigDecimal("0.80"))
  )

  "OfferLoader" should "load offers correctly from a valid properties file" in {
    val offers = OfferLoader
      .loadOffers("offers_good.properties", testItems)
      .sortBy(_.asInstanceOf[DiscountItemWithEnoughQuantityOfSomeItem].itemToBuy.name)

    offers.size shouldBe 2

    val applesOffer = offers.head.asInstanceOf[DiscountItemWithEnoughQuantityOfSomeItem]
    applesOffer.itemToBuy.name shouldBe "Apples"
    applesOffer.itemToDiscount.name shouldBe "Apples"
    applesOffer.quantityToBuyForDiscount shouldBe 1
    applesOffer.quantityDiscounted shouldBe 1
    applesOffer.discountPercentage shouldBe BigDecimal(10)

    val soupOffer = offers(1).asInstanceOf[DiscountItemWithEnoughQuantityOfSomeItem]
    soupOffer.itemToBuy.name shouldBe "Soup"
    soupOffer.itemToDiscount.name shouldBe "Bread"
    soupOffer.quantityToBuyForDiscount shouldBe 2
    soupOffer.quantityDiscounted shouldBe 1
    soupOffer.discountPercentage shouldBe BigDecimal(50)
    soupOffer.startDate shouldBe LocalDate.parse("2024-01-01")
    soupOffer.endDate shouldBe LocalDate.parse("2024-12-31")
  }

  it should "throw IllegalArgumentException for invalid item in offers" in {
    val exception = intercept[IllegalArgumentException] {
      OfferLoader.loadOffers("offers_bad.properties", testItems)
    }

    exception.getMessage should (
      include("Item name cannot be null or empty") or
        include("Invalid item name:")
    )
  }

  it should "throw IllegalArgumentException for missing required properties" in {
    val exception = intercept[IllegalArgumentException] {
      OfferLoader.loadOffers("offers_bad.properties", testItems)
    }

    exception.getMessage should (
      include("Item name cannot be null or empty") or
        include("Invalid item name:") or
        include("Missing") or
        include("invalid")
    )
  }
}
