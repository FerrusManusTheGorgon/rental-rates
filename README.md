Rate Scraper Application
Project Overview

This Scala application is designed to asynchronously scrape truck rental rates from a specific website, parse the scraped data, and store the results in a database. The application uses Selenium for web scraping and leverages Scala's concurrency features, particularly Future, to handle asynchronous tasks.
Modules and Packages

    DbSetup: Initializes the database connections and schemas.
    Rates: Model representing rate data.
    RateRepositoryImpl: Repository implementation for storing rates into the database.
    services:
        CrawlerServiceImpl: Handles the scraping of the Uhaul web pages.
        ParsingServiceImpl: Parses the scraped Uhaul HTML to extract rate information.
        ZipCodeService: Manages zip code data list.
        Main5: Entry point of the application that orchestrates the Uhaul scraping, parsing, and database operations.
    
        BudgetCrawlerServiceImpl: Handles the scraping of the Uhaul web pages.
        BudgetParsingServiceImpl: Parses the scraped Uhaul HTML to extract rate information.
        ZipCodeService: Manages zip code data list.
        Main6: Entry point of the application that orchestrates the Budget scraping, parsing, and database operations.
    
        PenskeCrawlerServiceImpl: Handles the scraping of the Uhaul web pages.
        PenskeParsingServiceImpl: Parses the scraped Uhaul HTML to extract rate information.
        ZipCodeService: Manages zip code data list.
        Main7: Entry point of the application that orchestrates the Penske(currently down due to website change) scraping, parsing, and database operations.

Technologies Used

    Scala 2.13
    Selenium WebDriver for scraping.
    SBT for building and dependency management.
    MySQL or another RDBMS for data persistence.

Setup and Installation
Prerequisites

    JDK 8 or above.
    SBT (Scala Build Tool) installed.
    A compatible web browser installed (Chrome, Firefox).
    ChromeDriver or GeckoDriver for Selenium based on the browser.

Database Setup

    Ensure your RDBMS (e.g., MySQL) is running.
    Execute the SQL scripts provided in dbSetup() to prepare your database schema.

Configuring the Application

    Update the database connection settings in DbSetup to match your environment.
    Ensure that drivers for Selenium (ChromeDriver/GeckoDriver) are correctly placed in your system's PATH.

 Important Functions

    scrapeRates: Orchestrates the scraping of data from the web and storing it into the database.

