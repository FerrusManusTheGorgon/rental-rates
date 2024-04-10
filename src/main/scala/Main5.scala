import db.DbSetup
import models.Rates
import repositories.RateRepositoryImpl
import services.{BudgetCrawlerServiceImpl, CrawlerServiceImpl, ParsingServiceImpl, ZipCodeService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, SECONDS}
import scala.concurrent.{Await, Future}


///Latest code
object Main5 extends App with DbSetup {
  dbSetup()

  val rateRepository = new RateRepositoryImpl()
  val crawlerService = new CrawlerServiceImpl()
  val parsingService = new ParsingServiceImpl()
//  val findBudgetTruckRate = new BudgetCrawlerServiceImpl()
  // Test Zip Codes
  //  val combineZipCodes = List(("85015", "54115"), ("85015", "86016"), ("85017", "54117"))

  // Define the initial pickup date
  val pickUpDate = "09/30/2024"
  val pickupDates = List(
    pickUpDate,
    calculatePickupDate(pickUpDate, 7),
    calculatePickupDate(pickUpDate, 6 * 30)
  )

  val run = ZipCodeService.comboZips.map { case (originZip, destinationZip) =>
    scrapeRates(originZip, destinationZip, pickUpDate)
  }

  Await.result(Future.sequence(run), Duration(60000, SECONDS))

  def scrapeRates(originZip: String, destinationZip: String, pickUpDate: String): Future[List[Int]] = {

    // Use the crawlerService to find truck rates
    val htmlString = crawlerService.findTruckRate(originZip, destinationZip, pickUpDate)

    // Use the parsingService to extract rates from the HTML
    val extractedRates = parsingService.extractTruckRates(htmlString, originZip, destinationZip, pickUpDate)

    val ratesF = extractedRates.map { rate =>
      rateRepository.createRates(rate)
    }
    for {
      rates <- Future.sequence(ratesF)
    } yield rates
  }


  def calculatePickupDate(pickUpDate: String, daysToAdd: Int): String = {
    val dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy")
    val calendar = java.util.Calendar.getInstance()
    calendar.setTime(dateFormat.parse(pickUpDate))
    calendar.add(java.util.Calendar.DAY_OF_MONTH, daysToAdd)
    dateFormat.format(calendar.getTimeInMillis)
  }
}


