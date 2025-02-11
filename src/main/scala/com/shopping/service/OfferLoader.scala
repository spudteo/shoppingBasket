package com.shopping.service

import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.util.Properties

import scala.io.Source

import com.shopping.model.{ DiscountItemWithEnoughQuantityOfSomeItem, Offer, PercentageDiscount, _ }

object OfferLoader {

  private def getItem(name: String, availableItems: Map[String, Item]): Item = {
    if (name == null || name.trim.isEmpty) {
      throw new IllegalArgumentException("Item name cannot be null or empty")
    }

    availableItems.getOrElse(
      name.toLowerCase,
      throw new IllegalArgumentException(s"Invalid item name: $name")
    )
  }

  private def parseDate(dateStr: String): LocalDate =
    try
      LocalDate.parse(dateStr)
    catch {
      case _: DateTimeParseException =>
        throw new IllegalArgumentException(
          s"Invalid date format: $dateStr. Date must be in YYYY-MM-DD format (e.g. 2024-01-31)"
        )
    }

  def loadOffers(configPath: String, availableItems: Map[String, Item]): List[Offer] = {
    val properties = new Properties()
    val source     = Source.fromResource(configPath)

    try {
      properties.load(source.bufferedReader())

      properties
        .stringPropertyNames()
        .toArray
        .map(_.toString)
        .filter(_.endsWith(".type"))
        .map(_.split("\\.")(0))
        .map {
          id =>
            try {
              val offerType = Option(properties.getProperty(s"$id.type"))
                .getOrElse(throw new IllegalArgumentException(s"Missing type for offer $id"))
                .trim
                .toUpperCase

              offerType match {
                case "DISCOUNT_WITH_QUANTITY" =>
                  new DiscountItemWithEnoughQuantityOfSomeItem(
                    itemToBuy = getItem(properties.getProperty(s"$id.itemToBuy"), availableItems),
                    itemToDiscount =
                      getItem(properties.getProperty(s"$id.itemToDiscount"), availableItems),
                    quantityToBuyForDiscount =
                      Option(properties.getProperty(s"$id.quantityToBuyForDiscount"))
                        .map(_.trim.toInt)
                        .getOrElse(
                          throw new IllegalArgumentException(
                            s"Missing quantityToBuyForDiscount for offer $id"
                          )
                        ),
                    quantityDiscounted = Option(properties.getProperty(s"$id.quantityDiscounted"))
                      .map(_.trim.toInt)
                      .getOrElse(
                        throw new IllegalArgumentException(
                          s"Missing quantityDiscounted for offer $id"
                        )
                      ),
                    discountPercentage = Option(properties.getProperty(s"$id.discountPercentage"))
                      .map(v => BigDecimal(v.trim))
                      .getOrElse(
                        throw new IllegalArgumentException(
                          s"Missing or invalid discountPercentage for offer $id"
                        )
                      ),
                    startDate = parseDate(properties.getProperty(s"$id.startDate")),
                    endDate = parseDate(properties.getProperty(s"$id.endDate")),
                    enabled =
                      properties.getProperty(s"$id.enabled", "true").trim.toLowerCase == "true",
                    stackable =
                      properties.getProperty(s"$id.stackable", "true").trim.toLowerCase == "true"
                  )

                case "PERCENTAGE_DISCOUNT" =>
                  new PercentageDiscount(
                    item = getItem(properties.getProperty(s"$id.item"), availableItems),
                    percentage = BigDecimal(properties.getProperty(s"$id.percentage").trim),
                    startDate = parseDate(properties.getProperty(s"$id.startDate")),
                    endDate = parseDate(properties.getProperty(s"$id.endDate")),
                    enabled =
                      properties.getProperty(s"$id.enabled", "true").trim.toLowerCase == "true",
                    stackable =
                      properties.getProperty(s"$id.stackable", "true").trim.toLowerCase == "true"
                  )

                case _ =>
                  throw new IllegalArgumentException(s"Unknown offer type: $offerType")
              }
            } catch {
              case e: IllegalArgumentException => throw e
              case e: Exception =>
                throw new IllegalArgumentException(s"Error loading offer $id: ${e.getMessage}")
            }
        }
        .toList

    } finally
      source.close()
  }
}
