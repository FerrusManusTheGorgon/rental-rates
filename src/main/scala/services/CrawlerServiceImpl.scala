package services

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver

import java.time.Duration
import scala.util.Random

class CrawlerServiceImpl extends CrawlerService {

  lazy val random = new Random()

  override def findTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String = {
    val driver = getRandomDriver()
    driver.get("https://www.uhaul.com")
    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(6000))
    // Find and input data into text fields
    val pickUpLocation = driver.findElement(By.id("PickupLocation-TruckOnly"))
    typeWithRandomDelay(pickUpLocation, originZip)

    Thread.sleep(5000)

    val dropOffLocation = driver.findElement(By.id("DropoffLocation-TruckOnly"))
    typeWithRandomDelay(dropOffLocation, destinationZip)

    Thread.sleep(6000)

    val pickUpDateField = driver.findElement(By.id("PickupDate-TruckOnly"))
    typeWithRandomDelay(pickUpDateField, pickUpDate)

    Thread.sleep(5000)
    // Submit the form
    val submitButton = driver.findElement(By.className("expanded"))
    submitButton.click()

    // Wait for the new page to load
    Thread.sleep(5000)

    // Extract the raw HTML
    val htmlString = driver.getPageSource

    // Check if the pattern "$0.79/mile" is present in the HTML
    val pattern = """\$\d+\.\d+\/mile""".r
    val hasPattern = pattern.findFirstIn(htmlString).isDefined

    if (hasPattern) {
      driver.quit()
      return "Local Rate" // Skip extraction and return empty string
    }

    driver.quit()
    htmlString
  }

  def getRandomDriver(): WebDriver = {
    val browserIndex = random.nextInt(2)
    browserIndex match {
      case 0 => new ChromeDriver()
      case 1 => new FirefoxDriver()
    }
  }

  def typeWithRandomDelay(element: org.openqa.selenium.WebElement, text: String): Unit = {
    for (c <- text.toCharArray) {
      element.sendKeys(c.toString)
      Thread.sleep(random.nextInt(700) + 350) // Random wait between 100ms and 399ms
    }
  }
}

