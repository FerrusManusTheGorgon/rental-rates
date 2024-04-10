package services

import models.Rates
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters.CollectionHasAsScala


class BudgetParsingServiceImpl extends BudgetParsingService {
  override def extractBudgetTruckRates(pageSource: String, pickUpDate: String, originZip: String, destinationZip: String): List[Rates] = {
    // Parse the pageSource with Jsoup
    val doc: Document = Jsoup.parse(pageSource)

    // Find all elements with the class "truck-name"
    val truckNameElements = doc.select("[id^=item] > div:nth-child(2) > div:nth-child(1) > div:nth-child(2) > h2:nth-child(1)").asScala

    // Extract the truck names from the elements
val truckNames = truckNameElements.map(elem => "B " + elem.text()).toList

    truckNames.foreach(println)

    // Find all elements with the class "truckprice-info-tooltip"
    val truckPriceElements = doc.select("[id^=item] > div:nth-child(3) > div:nth-child(4) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > span:nth-child(1)").asScala

    if (truckPriceElements.nonEmpty) {
      println("Found truck prices:")
      truckPriceElements.foreach(println)

      val ratesList = truckNames.zip(truckPriceElements).map { case (truckName, element) =>
        val text = element.text()
        val pricePattern = """\$([\d.]+)""".r
        pricePattern.findFirstMatchIn(text) match {
          case Some(matchResult) if matchResult.group(1) == "0.00" =>
            Rates(
              model = truckName,
              amount = "Sold Out",
              pickUpDate = pickUpDate,
              originZip = originZip,
              destinationZip = destinationZip
            )
          case Some(matchResult) =>
            val price = matchResult.group(1)
            Rates(
              model = truckName,
              amount = price,
              pickUpDate = pickUpDate,
              originZip = originZip,
              destinationZip = destinationZip
            )
          case None =>
            Rates(
              model = truckName,
              amount = "Price pattern not found",
              pickUpDate = pickUpDate,
              originZip = originZip,
              destinationZip = destinationZip
            )
        }
      }

      ratesList.foreach(println)

      ratesList
    } else {
      println("No truck prices found.")
      Nil // Return an empty list if no truck prices found.
    }
  }
}







