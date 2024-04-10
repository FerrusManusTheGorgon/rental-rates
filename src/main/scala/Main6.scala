import org.openqa.selenium.{By, ElementNotInteractableException, WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.{ExpectedCondition, ExpectedConditions, Select, WebDriverWait}
import repositories.RateRepositoryImpl
import services.{BudgetCrawlerServiceImpl, BudgetParsingServiceImpl, ParsingServiceImpl, ZipCodeService}
import db.DbSetup
import repositories.RateRepositoryImpl
import scala.concurrent.ExecutionContext.Implicits.global

import java.time.{Duration, LocalDate, Month}
import scala.util.Random
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.interactions.Actions

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.concurrent.Future


object Main6 extends App with DbSetup {
  dbSetup()

  val rateRepository = new RateRepositoryImpl()
  val budgetCrawlerService = new BudgetCrawlerServiceImpl()
  val budgetParsingService = new BudgetParsingServiceImpl()

  val pickUpDate = "09/30/2024"

  val run = ZipCodeService.comboZips.map { case (originZip, destinationZip) =>
    scrapeRates(originZip, destinationZip, pickUpDate)
  }

  def scrapeRates(originZip: String, destinationZip: String, pickUpDate: String): Future[List[Int]] = {

    // Use the crawlerService to find truck rates
    val htmlString = budgetCrawlerService.findBudgetTruckRate(originZip, destinationZip, pickUpDate)

    // Use the parsingService to extract rates from the HTML
    val extractedRates = budgetParsingService.extractBudgetTruckRates(htmlString, originZip, destinationZip, pickUpDate)

    val ratesF = extractedRates.map { rate =>
      rateRepository.createRates(rate)
    }
    for {
      rates <- Future.sequence(ratesF)
    } yield rates
  }
}



