import db.DbSetup
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions, Select, WebDriverWait}
import repositories.RateRepositoryImpl

import java.time.{Duration, LocalDate, Month}
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.util.Calendar
import services.{PenskeCrawlerServiceImpl, PenskeParsingServiceImpl, ZipCodeService}

import java.time.Duration
import java.time.format.DateTimeFormatter
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random
import models.Rates
import zio.Schedule.count.driver

import java.time.temporal.ChronoUnit
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`




object Main7 extends App with DbSetup {
  dbSetup()


  lazy implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  val rateRepository = new RateRepositoryImpl()
  val penskeParsingService = new PenskeParsingServiceImpl()
  val penskeCrawlerService = new PenskeCrawlerServiceImpl()
  val pickUpDate = "11/15/2024"
  //  val originZip = "85014"
  //  val destinationZip = "85015"
  ////  val pickUpMonth = "November"
  ////  val pickUpYear ="2023"
  ////  val pickUpDay = "25"
  //
  //  val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
  //  val parsedDate = LocalDate.parse(pickUpDate, formatter)
  //
  //
  //  val pickUpMonth = parsedDate.getMonth
  //  val pickUpYear = parsedDate.getYear
  //  val pickUpDay = parsedDate.getDayOfMonth
  //  val pickUpMonthFormatted = parsedDate.getMonth.toString.toLowerCase.capitalize

  val run = ZipCodeService.comboZips.map { case (originZip, destinationZip) =>
    scrapeRates(originZip, destinationZip, pickUpDate)
  }

  def scrapeRates(originZip: String, destinationZip: String, pickUpDate: String): Future[List[Int]] = {

    // Use the crawlerService to find truck rates
    val htmlString = penskeCrawlerService.findPenskeTruckRate(originZip, destinationZip, pickUpDate)

    val extractedRates = penskeParsingService.extractPenskeTruckRates(htmlString, originZip, destinationZip, pickUpDate)

    val ratesF = extractedRates.map { rate =>
      rateRepository.createRates(rate)
    }
    for {
      rates <- Future.sequence(ratesF)
    } yield rates
  }
}




