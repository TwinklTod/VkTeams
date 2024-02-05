package org.example;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VkTeams {
    private static final Logger log = Logger.getLogger(VkTeams.class.getName());

    public static void main(String[] args) {
        String threadname = "machineName"; // имя машины
        int duration = 18000000; // Длительность вебинара
        int frequency = 30000; // Как часто снимаются метрики
        String audio_status = "true"; // Изначальная установка статуса микрофона включен
        boolean headless = false;

        // получаем путь к webdriver
        System.setProperty("webdriver.chrome.driver",
                "F:\\Tools\\chromedriver-win64\\chromedriver.exe");

        // Включаем микрофон и камеру
        ChromeOptions options = new ChromeOptions();
        options.addArguments("use-fake-device-for-media-stream");
        options.addArguments("use-fake-ui-for-media-stream");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-setuid-sandbox");

        if (headless) {
            options.addArguments("--headless");
        }

        // создаем webdriver
        WebDriver driver = new ChromeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // устанавливаем явное ожидание
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // вызываем обработчик исключений
        try {
            // Открываем вкладку со статистикой
            driver.get("chrome://webrtc-internals/");
            String originalWindow = driver.getWindowHandle();

            // Открываем новое окно
            js.executeScript("window.open()");

            // Loop through until we find a new window handle
            for (String windowHandle : driver.getWindowHandles()) {
                if (!originalWindow.equals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Открываем вкладку vkteams
            driver.get("https://webim.vkteams-test.ext.lukoil.com/");
            String meetWindow = driver.getWindowHandle();
            driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

            // нажимаем кнопку для подключения через браузер
            driver.findElement(org.openqa.selenium.By.xpath("//button[@data-action='acceptAgreement']")).click();

            // получаем имя
            String login_email = "nikita.bolotin@contractor.lukoil.com";

            // вводим имя в поле "Имя пользователя"
            driver.findElement(org.openqa.selenium.By.name("otpEmail")).sendKeys(login_email);

            // нажимаем Войти
            driver.findElement(org.openqa.selenium.By.xpath("//button[@data-action='submit']")).click();
        } catch (Exception e) {
            log.log(Level.WARNING, threadname + e.getMessage(), e);
            System.out.println(threadname + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
