package com.shopping.model

import com.shopping.service.ItemLoader

case class Item(name: String, price: BigDecimal) {
  def displayPrice: BigDecimal = price.setScale(2, BigDecimal.RoundingMode.DOWN)
}

object Items {

  def loadAvailableItems(path: String): Map[String, Item] =
    ItemLoader.loadItems(path)
}
