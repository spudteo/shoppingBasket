package com.shopping.service

import java.time.format.DateTimeFormatter

import com.shopping.model._

object ReceiptFormatter {
  private val MinColumnWidth = 35
  private val PriceWidth     = 7
  private val DateFormatter  = DateTimeFormatter.ofPattern("yyyy/MM/dd")

  private def formatTableRow(
    description: String,
    quantity: Int,
    price: BigDecimal,
    total: BigDecimal,
    columnWidth: Int
  ): String = {
    val descCol = description.padTo(columnWidth, ' ')
    f"$descCol x $quantity%2d @ £$price%6.2f = £$total%6.2f"
  }

  private def formatPriceLine(
    description: String,
    amount: BigDecimal,
    columnWidth: Int = MinColumnWidth
  ): String = {
    val descCol = description.padTo(columnWidth, ' ')
    f"$descCol £$amount%6.2f"
  }

  def format(receipt: Receipt, detailed: Boolean = false): String = {
    val output = StringBuilder.newBuilder

    output.append(s"Date: ${receipt.date.format(DateFormatter)}\n\n")

    if (detailed) {
      val maxItemLength = math.max(
        MinColumnWidth,
        if (receipt.items.isEmpty) MinColumnWidth
        else receipt.items.keys.map(_.name.length).max
      )

      output.append("=== Detailed Receipt ===\n")
      output.append("=".repeat(maxItemLength + PriceWidth * 2 + 10)).append("\n")

      receipt.items.toList
        .sortBy(_._1.name)
        .foreach {
          case (item, quantity) =>
            output
              .append(
                formatTableRow(
                  item.name,
                  quantity,
                  item.displayPrice,
                  receipt.itemSubtotals(item),
                  maxItemLength
                )
              )
              .append("\n")
        }

      output.append("=".repeat(maxItemLength + PriceWidth * 2 + 10)).append("\n\n")
    }

    output.append(formatPriceLine("Subtotal:", receipt.subtotal)).append("\n")

    if (receipt.hasDiscounts) {
      receipt.appliedDiscounts.foreach {
        discount =>
          output
            .append(formatPriceLine(discount.description + ":", discount.amount.abs))
            .append("\n")
      }
    } else {
      output.append("(No offers available)\n")
    }

    output.append(formatPriceLine("Total price:", receipt.total))

    output.toString
  }
}
