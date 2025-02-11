package com.shopping.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ItemLoaderSpec extends AnyFlatSpec with Matchers {

  "ItemLoader" should "load items correctly from a valid properties file" in {
    val items = ItemLoader.loadItems("items_good.properties")

    items.size shouldBe 3

    val apple = items("apple")
    apple.name shouldBe "Apple"
    apple.price shouldBe BigDecimal("1.99")

    val banana = items("banana")
    banana.name shouldBe "Banana"
    banana.price shouldBe BigDecimal("0.99")

    val orange = items("orange")
    orange.name shouldBe "Orange"
    orange.price shouldBe BigDecimal("2.50")
  }

  it should "throw IllegalArgumentException for invalid properties file" in {
    val exception = intercept[IllegalArgumentException] {
      ItemLoader.loadItems("items_bad.properties")
    }

    exception.getMessage should include("Error loading item")
  }

  it should "truncate prices to 2 decimal places without rounding" in {
    val items = ItemLoader.loadItems("items_with_decimals.properties")

    val apple = items("apple")
    apple.price shouldBe BigDecimal("1.99")
  }
}
