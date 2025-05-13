package org.bread_experts_group.extensible_image_archiver.drivers

import org.bread_experts_group.extensible_image_archiver.SeleniumDriver
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.lang.Thread.sleep
import java.nio.file.Path
import java.time.Duration

class PixivSeleniumDriver(
    userIdToSearch: Int,
    override val seleniumDriver: WebDriver,
) : SeleniumDriver {
    override val url = "https://pixiv.net/en/users/$userIdToSearch/illustrations"

    override fun run(outputDirectory: Path) {
        println("URL: $url")
        this.seleniumDriver.get(this.url)
        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
            .until(ExpectedConditions
                .presenceOfElementLocated(By.className("thumbnail-link")))

        val images = this.seleniumDriver
            .findElements(By.className("thumbnail-link"))

        println("Found ${images.size} images")

        for (image in images) {
            val anchorHref = image.getDomAttribute("href")
            this.seleniumDriver.get("https://pixiv.net$anchorHref")

            val src = WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5))
                .until(ExpectedConditions
                    .presenceOfElementLocated(By.className("work-thumb")))
                .getDomAttribute("src")

            if (src == null) {
                println("Skipping null src for ${this.seleniumDriver.currentUrl}")
                break
            }

            val mangaButton = this.seleniumDriver.findElements(By.xpath("//button[text()='Show all']"))
            if (mangaButton.size == 1) {
                println("Manga url")
            } // else {}  // handle single image

            (this.seleniumDriver as JavascriptExecutor).executeScript("window.open('${src}', '_blank');")
            val newWindow = this.seleniumDriver.windowHandles
                .filter { it != this.seleniumDriver.windowHandle }[0]
            this.seleniumDriver.switchTo().window(newWindow)

            sleep(2000)
            println("Image src: ${this.seleniumDriver.currentUrl}")
            break
        }
    }
}