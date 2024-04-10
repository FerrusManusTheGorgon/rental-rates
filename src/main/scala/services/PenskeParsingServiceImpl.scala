package services

import models.Rates
import scala.collection.JavaConverters._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class PenskeParsingServiceImpl extends PenskeParsingService {
  def extractPenskeTruckRates(pageSource: String, pickUpDate: String, originZip: String, destinationZip: String): List[Rates] = {
    // Parse the pageSource with Jsoup
    val doc: Document = Jsoup.parse(pageSource)

    // Find all elements with the class "truck-name"
    val truckNameElements = doc.select(".truck-name").asScala

    if (truckNameElements.nonEmpty) {
      println("Found truck names:")
      truckNameElements.foreach(println)

      // Extract the truck names from the elements
      val truckNames = truckNameElements.map(elem => "B " + elem.text()).toList


      // Find all elements with the class "truckprice-info-tooltip"
      val truckPriceElements = doc.select(".truckprice-info-tooltip").asScala

      if (truckPriceElements.nonEmpty) {
        println("Found truck prices:")
        truckPriceElements.foreach(println)

        // Extract the prices from the elements
        val pricePattern = """\$([\d.]+)""".r
        val prices = truckPriceElements.flatMap { element =>
          pricePattern.findFirstMatchIn(element.text()).map(_.group(1))
        }.toList

        // Create a list of Rates objects
        val ratesList = truckNames.zip(prices).map { case (truckName, price) =>
          Rates(
            model = truckName,
            amount = s"$$$price",
            pickUpDate = pickUpDate,
            originZip = originZip,
            destinationZip = destinationZip
          )
        }

        ratesList
      } else {
        println("No truck prices found.")
        Nil // Return an empty list if no truck prices found.
      }
    } else {
      println("No truck names found.")
      Nil // Return an empty list if no truck names found.
    }
  }
}





