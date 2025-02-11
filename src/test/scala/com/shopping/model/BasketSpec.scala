package com.shopping.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BasketSpec extends AnyFlatSpec with Matchers {

  val testItems = Map(
    "apples" -> Item("Apples", BigDecimal("1.00")),
    "soup"   -> Item("Soup", BigDecimal("0.65")),
    "bread"  -> Item("Bread", BigDecimal("0.80")),
    "milk"   -> Item("Milk", BigDecimal("1.30"))
  )

  "Basket" should "handle case insensitive item names" in {
    val basket1 = Basket(List("APPLES"))(testItems)
    val basket2 = Basket(List("apples"))(testItems)
    val basket3 = Basket(List("Apples"))(testItems)

    basket1.items shouldBe basket2.items
    basket2.items shouldBe basket3.items
    basket1.items.keys.head.name shouldBe "Apples"
  }

  it should "throw IllegalArgumentException for invalid items name that dosen't have a real item" in {
    val exception = intercept[IllegalArgumentException] {
      Basket(List("unknown"))(testItems)
    }
    exception.getMessage should include("Invalid item")
  }
}
