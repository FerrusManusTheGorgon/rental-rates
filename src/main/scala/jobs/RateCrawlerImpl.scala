package jobs

import com.typesafe.scalalogging.LazyLogging
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import models.Rates
import db.DefaultQuillContext

import io.getquill._
import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random


class RateCrawlerImpl()(implicit ec: ExecutionContext)
  extends RateCrawler with LazyLogging with DefaultQuillContext {

  val driver = new ChromeDriver()
  driver.get("https://www.uhaul.com")
  driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000))

  val random = new Random()

  // Find and input data into text fields
  val pickUpLocation = driver.findElement(By.id("PickupLocation-TruckOnly"))
  val pickUpLocationString = "85015"
  typeWithRandomDelay(pickUpLocation, pickUpLocationString)
  println("pick up location")

  val dropOffLocation = driver.findElement(By.id("DropoffLocation-TruckOnly"))
  val dropOffLocationString = "54115"
  typeWithRandomDelay(dropOffLocation, dropOffLocationString)
  println("drop off location")

  val pickUpDate = driver.findElement(By.id("PickupDate-TruckOnly"))
  val pickUpDateString = "06/22/2023"
  typeWithRandomDelay(pickUpDate, pickUpDateString)
  println("pick up date")

  // Submit the form
  val submitButton = driver.findElement(By.className("expanded"))
  submitButton.click()
  println("after click")

  // Wait for the new page to load
  Thread.sleep(3000)

  // Extract the raw HTML
  val rawHtml: String = driver.getPageSource()

  // Find the element by ID and extract the desired information
  val regex = """<form class="collapse" id="formProcessRequest_(.*?)".*?>(?:.|[\r\n])*?\$([\d,.]+)""".r
  val extractedInfo: Option[(String, String)] = regex.findFirstMatchIn(rawHtml).map { matchResult =>
    val modelCode = matchResult.group(1)
    val amount = matchResult.group(2)
    (modelCode, amount)
  }

  // Create an instance of TruckRates using the extracted information
  val truckRates: Option[Rates] = extractedInfo.map { case (modelCode, amount) =>
    Rates(model = modelCode, amount = amount, pickUpDate = pickUpDateString, originZip = pickUpLocationString, destinationZip = dropOffLocationString)
  }

  // Print the extracted information and the created instance of TruckRates
  extractedInfo.foreach { case (modelCode, amount) =>
    println(s"Model Code: $modelCode")
    println(s"Amount: $amount")
    println(pickUpDateString)
  }

  truckRates.foreach(println)

  // Define the concatenateStrings method
  def concatenateStrings(pickUpLocationString: String, dropOffLocationString: String, modelCode: String): String = {
    pickUpLocationString + dropOffLocationString + modelCode
  }

  val concatenatedString = concatenateStrings(pickUpLocationString, dropOffLocationString, extractedInfo.map(_._1).getOrElse(""))
  val lane = concatenatedString
  println("Concatenated String: " + concatenatedString)
  println("Lane: " + lane)

  // Implement the loadRateData method from the RateCrawler trait
  override def loadRateData(): Future[Unit] = Future {

    import ctx._
    val result: Future[List[Rates]] = run(query[Rates])

    result.foreach(println)
  }

  driver.quit()

  def typeWithRandomDelay(element: org.openqa.selenium.WebElement, text: String): Unit = {
    for (c <- text.toCharArray) {
      element.sendKeys(c.toString)
      Thread.sleep(random.nextInt(100) + 50) // Random wait between 50ms and 149ms
    }
  }
}


