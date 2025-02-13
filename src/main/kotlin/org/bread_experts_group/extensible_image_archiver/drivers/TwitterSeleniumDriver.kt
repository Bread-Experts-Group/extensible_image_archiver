package org.bread_experts_group.extensible_image_archiver.drivers

import org.bread_experts_group.extensible_image_archiver.SeleniumDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.lang.Thread.sleep
import java.nio.file.Path
import java.time.Duration
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.io.path.pathString

class TwitterSeleniumDriver(
    usernameToSearch: String,
    private val twitterUsername: String,
    private val twitterPassword: String,
    override val seleniumDriver: WebDriver
) : SeleniumDriver {
    override val url: String = "https://x.com/$usernameToSearch/media"

    override fun run(outputDirectory: Path) {
        this.seleniumDriver.get("https://x.com/i/flow/login")

        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[autocomplete=username]")))
            .sendKeys(this.twitterUsername)
        sleep((100..750).random().toLong())
        this.seleniumDriver.findElement(By.xpath("//button/div/span/span[text()='Next']"))
            .click()

        sleep((75..1250).random().toLong())
        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[autocomplete=current-password]")))
            .sendKeys(this.twitterPassword)
        sleep((250..800).random().toLong())
        this.seleniumDriver.findElement(By.xpath("//button/div/span/span[text()='Log in']"))
            .click()

        // used as general "this page has loaded" indicator
        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[aria-label=\"Account menu\"]")))
        this.seleniumDriver.get(url)

        val images = this.seleniumDriver.findElements(By.xpath("//img"))
        val ktorClient = HttpClient(CIO)
        runBlocking {
            for (image in images) {
                val imageUrl = image.getDomAttribute("src") ?: continue
                val imageName = imageUrl.split("?").first().split("/").last()

                val outputFile = File(outputDirectory.toFile(), imageName)
                if (outputFile.exists()) {
                    println("$imageName already exists in ${outputDirectory.pathString} - skipping...")
                    continue
                }

                val res = ktorClient.get(imageUrl)
                if (res.status != HttpStatusCode.OK) continue
                val body = res.bodyAsBytes()
                outputFile.writeBytes(body)

                println("Downloaded $imageName")
                delay(1000)
            }
        }
    }
}