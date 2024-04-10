package services

import org.openqa.selenium.{By, ElementNotInteractableException, WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import zio.Schedule.count.driver

import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDate, Month}
import java.util.Random
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

class BudgetCrawlerServiceImpl extends BudgetCrawlerService {
  lazy val random = new Random()

  override def findBudgetTruckRate(originZip: String, destinationZip: String, pickUpDate: String): String = {
    //    val driver = getRandomDriver()
    val driver = new FirefoxDriver()
    driver.get("https://www.budgettruck.com/")
    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000))


    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val parsedDate = LocalDate.parse(pickUpDate, formatter)

    val pickUpMonth = parsedDate.getMonth
    val pickUpYear = parsedDate.getYear
    val pickUpDay = parsedDate.getDayOfMonth


    try {


      val pickUpLocation = driver.findElement(By.id("txtPickupLocation"))
      typeWithRandomDelay(pickUpLocation, originZip)

      val dropOffLocation = driver.findElement(By.id("txtDropoffLocation"))
      typeWithRandomDelay(dropOffLocation, destinationZip)

      // Select pick-up time
      val dropDownElement = driver.findElement(By.id("ddlPickupTimes"))
      val select = new Select(dropDownElement)
      select.selectByIndex(6) // Select by index

      var pickUpDateField = driver.findElement(By.id("pickUpDate"))
      pickUpDateField.click()

      // Calculate the number of clicks required to reach the desired month
      val desiredMonth = Month.valueOf(pickUpMonth.toString.toUpperCase)
      val desiredYear = pickUpYear //2023
      val currentMonth = LocalDate.now().getMonth
      val currentYear = LocalDate.now().getYear
      val clicksRequired = ChronoUnit.MONTHS.between(LocalDate.of(currentYear, currentMonth, 1), LocalDate.of(desiredYear, desiredMonth, 1))

      var clickCount = 0

      while (clickCount < clicksRequired) {
        val nextButton = driver.findElement(By.id("datepicker-popup-nextDiv-pickUpDate"))
        clickWithRandomDelay(nextButton, 500, 1000) // Set your desired delay range here (in milliseconds)
        clickCount += 1
      }
      // Find the target element
      val targetElement = driver.findElement(By.cssSelector("#datepicker-popup-content-pickUpDate > div > div.ui-datepicker-group.ui-datepicker-group-first"))

      // Check if there are clickable elements with text within the target element
      val areClickableElementsWithText = areClickableElementsWithTextPresent(driver, targetElement)

      if (areClickableElementsWithText) {
        // Find all the clickable elements with text
        val clickableElements = targetElement.findElements(By.xpath(".//*")).filter(_.isEnabled)
        val clickableElementsWithText = clickableElements.filter(_.getText.trim.nonEmpty)

        // Find the first clickable element that contains the text "15" and click on it
        val desiredText = "15"
        val elementWithDesiredText = clickableElementsWithText.find(_.getText.trim.contains(desiredText))

        elementWithDesiredText match {
          case Some(element) =>
            println(s"Clickable Element with Text '$desiredText' found. Clicking...")
            element.click()
          case None =>
            println(s"Clickable Element with Text '$desiredText' not found.")
        }
      } else {
        println("No clickable elements with text found within the target element.")
      }

      // Random wait before clicking the element with id "#btnFindTruck"
      clickWithRandomDelay(driver.findElement(By.cssSelector("#btnFindTruck")), 1000, 5000)
      //

      //     Wait for the last page to load completely
      val waitObject = new WebDriverWait(driver, Duration.ofSeconds(30))
      waitObject.until(ExpectedConditions.urlContains("/truck"))

      Thread.sleep(9000)
      //


      //      def extractInformation(driver: WebDriver): String = {
      //        try {
      // Extract everything from the page
      val pageSource = driver.getPageSource()
      // Check if the pattern "$0.##/mile" is present in the HTML
      val pattern = """0\.\d{2}/Mile""".r
      val hasPattern = pattern.findFirstIn(pageSource).isDefined

      if (hasPattern) {
        driver.quit()
        "Local Rate" // Skip extraction and return "Local Rate"
      } else {
        "Pattern Not Found" // Or return any other appropriate value
      }

    } catch {
      case e: Exception =>
        "Error Occurred" // Return an error message or throw the exception
    } finally {
      try {
        // Perform any cleanup actions for the driver or resources
      } catch {
        case e: Exception => println("Error while closing the browser: " + e.getMessage)
      }
    }
  }


  def typeWithRandomDelay(element: org.openqa.selenium.WebElement, text: String): Unit = {
    for (c <- text.toCharArray) {
      element.sendKeys(c.toString)
      Thread.sleep(random.nextInt(400) + 150) // Random wait between 100ms and 399ms
    }
  }

  def clickWithRandomDelay(element: org.openqa.selenium.WebElement, minDelay: Int, maxDelay: Int): Unit = {
    val random = new Random()
    val delay = random.nextInt(maxDelay - minDelay + 1) + minDelay
    Thread.sleep(delay)
    element.click()
  }

  def areClickableElementsWithTextPresent(driver: WebDriver, element: WebElement): Boolean = {
    val clickableElements = element.findElements(By.xpath(".//*")).filter(_.isEnabled)
    val clickableElementsWithText = clickableElements.filter(_.getText.trim.nonEmpty)
    clickableElementsWithText.nonEmpty
  }
}














