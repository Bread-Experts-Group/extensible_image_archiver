package org.bread_experts_group.extensible_image_archiver.drivers

import org.bread_experts_group.extensible_image_archiver.SeleniumDriver
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WindowType
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Path
import java.time.Duration

class PixivSeleniumDriver(
    userIdToSearch: Int,
    override val seleniumDriver: WebDriver,
) : SeleniumDriver {
    override val url = "https://pixiv.net/en/users/$userIdToSearch/illustrations"

    override fun run(outputDirectory: Path) {
        this.seleniumDriver.get(this.url)
        WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5)).until(
                ExpectedConditions.presenceOfElementLocated(By.className("thumbnail-link"))
            )

        val images = this.seleniumDriver.findElements(By.className("thumbnail-link"))
        this.seleniumDriver.windowHandle

        for (image in images) {
            val anchorHref = image.getDomAttribute("href")
            this.seleniumDriver.switchTo().newWindow(WindowType.TAB)

            this.seleniumDriver.get("https://pixiv.net$anchorHref")

            val src = WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("work-thumb"))
                ).getDomAttribute("src")

            if (src == null) {
                println("Skipping null src for ${this.seleniumDriver.currentUrl}")
                break
            }

            val mangaButton = this.seleniumDriver.findElements(By.xpath("//button[text()='Show all']"))
            if (mangaButton.size == 1) {
                mangaButton[0].click()
                WebDriverWait(this.seleniumDriver, Duration.ofSeconds(5)).until(
                        ExpectedConditions.presenceOfElementLocated(
                            By.className("manga-pages")
                        )
                    )

                val pages = this.seleniumDriver.findElements(By.className("manga-page"))
                for (page in pages) {
                    val imageSrc = page.findElement(By.xpath("./div/img")).getDomAttribute("src")
                    println(imageSrc)
                }
            } else {
                (this.seleniumDriver as JavascriptExecutor).executeScript("window.open('${src}', '_blank');")
                val newWindow = this.seleniumDriver.windowHandles.filter { it != this.seleniumDriver.windowHandle }[0]
                this.seleniumDriver.switchTo().window(newWindow)
            }
        }
    }
}