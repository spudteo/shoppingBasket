package com.shopping.model

case class Basket(items: Map[Item, Int]) {

  def quantityOf(item: Item): Int =
    items.find(_._1.name.equalsIgnoreCase(item.name)).map(_._2).getOrElse(0)

  def contains(item: Item): Boolean =
    items.keys.exists(_.name.equalsIgnoreCase(item.name))
}

object Basket {

  def apply(itemNames: List[String])(availableItems: Map[String, Item]): Basket = {
    val items = itemNames
      .map(_.toLowerCase)
      .map(
        name =>
          availableItems.getOrElse(
            name,
            throw new IllegalArgumentException(
              s"Invalid item: $name. Please check the item name and try again."
            )
          )
      )
      .groupBy(identity)
      .map { case (item, items) => (item, items.size) }

    new Basket(items)
  }
}
