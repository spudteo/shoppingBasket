package com.shopping

import java.time.LocalDate

import com.shopping.model._
import com.shopping.service._

object Main extends App {
  if (args.length == 0) {
    println("Usage: PriceBasket [-d|-v] item1 item2 item3 ...")
    System.exit(1)
  }

  val (isDebug, items) = args(0) match {
    case "-d" | "-v" if args.length > 1 => (true, args.tail.toList)
    case _                              => (false, args.toList)
  }

  val availableItems = Items.loadAvailableItems("items.properties")
  val offers         = Offers.loadActiveOffers("offers.properties", availableItems)

  val basket  = Basket(items)(availableItems)
  val cashier = new Cashier()
  val receipt = cashier.generateReceipt(basket = basket, date = LocalDate.now(), offers = offers)

  val output = ReceiptFormatter.format(receipt, detailed = isDebug)
  println(output)
}
