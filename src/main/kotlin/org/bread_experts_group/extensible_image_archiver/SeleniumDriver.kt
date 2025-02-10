package org.bread_experts_group.extensible_image_archiver

import org.openqa.selenium.WebDriver

interface SeleniumDriver : Driver {
    val seleniumDriver: WebDriver
}