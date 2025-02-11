package com.shopping.service

import java.util.Properties

import scala.collection.immutable.Map
import scala.io.Source

import com.shopping.model.Item

object ItemLoader {

  def loadItems(configPath: String): Map[String, Item] = {
    val properties = new Properties()
    val source     = Source.fromResource(configPath)

    try {
      properties.load(source.bufferedReader())

      val itemIds = properties
        .stringPropertyNames()
        .toArray
        .map(_.toString)
        .filter(_.endsWith(".name"))
        .map(_.split("\\.")(0))
        .distinct

      itemIds.foldLeft(Map.empty[String, Item]) {
        (items, id) =>
          try {
            val name = properties.getProperty(s"$id.name").trim
            val price = BigDecimal(properties.getProperty(s"$id.price").trim)
              .setScale(2, BigDecimal.RoundingMode.DOWN)
            items + (name.toLowerCase -> Item(name, price))
          } catch {
            case e: Exception =>
              throw new IllegalArgumentException(s"Error loading item $id: ${e.getMessage}")
          }
      }
    } finally
      source.close()
  }
}
