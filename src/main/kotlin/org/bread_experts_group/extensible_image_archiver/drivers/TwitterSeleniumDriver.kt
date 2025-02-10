package org.bread_experts_group.extensible_image_archiver.drivers

import org.bread_experts_group.extensible_image_archiver.SeleniumDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Path
import java.time.Duration

class TwitterSeleniumDriver(usernameToSearch: String, private val twitterUsername: String, private val twitterPassword: String, override val seleniumDriver: WebDriver) : SeleniumDriver {
    override val url: String = "https://x.com/$usernameToSearch"

    override fun run(outputDirectory: Path) {
        this.seleniumDriver.get("https://x.com/i/flow/login")

        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[autocomplete=username]")))
            .sendKeys(this.twitterUsername)
        this.seleniumDriver.findElement(By.xpath("\"//button/div/span/span[.='Next']\""))
            .click()

        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[autocomplete=current-password]")))
            .sendKeys(this.twitterPassword)
        this.seleniumDriver.findElement(By.xpath("\"//button/div/span/span[.='Log in']\""))
            .click()
    }
}