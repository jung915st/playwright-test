package com.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class AccessibilityTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0";

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.firefox()
            .launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));
    }

    @AfterAll
    static void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720)
                .setUserAgent(userAgent)
                .setJavaScriptEnabled(true));

        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void testAccessibilityWebsite() {
        try {
            // Navigate to the website
            page.navigate("https://accessibility.moda.gov.tw/");

            // Handle Cloudflare challenge
            handleCloudflareChallenge(page);

            // Verify we're on the actual website
            Assertions.assertTrue(page.title().contains("無障礙網路空間服務網"));

            // Add your test logic here
            // ...

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void handleCloudflareChallenge(Page page) {
        try {
            // Wait for the Cloudflare challenge iframe
            ElementHandle challengeFrame = page.waitForSelector("iframe[title=\"包含 Cloudflare 安全性查問的小工具\"]",
                    new Page.WaitForSelectorOptions().setTimeout(10000));

            if (challengeFrame != null) {
                System.out.println("Cloudflare challenge detected");

                // Switch to the challenge iframe
                Frame frame = challengeFrame.contentFrame();
                if (frame == null) {
                    throw new RuntimeException("Could not get challenge iframe");
                }

                // Here you can interact with elements inside the iframe if needed
                // Example: Click a checkbox or button to pass the challenge
                // ElementHandle checkbox = frame.waitForSelector("selector-for-checkbox",
                //     new Frame.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
                // if (checkbox != null) {
                //     checkbox.click();
                // }

                // Wait for verification to complete
                page.waitForLoadState();
                page.waitForTimeout(30000);

                System.out.println("Cloudflare challenge completed");
            }
        } catch (Exception e) {
            System.out.println("No Cloudflare challenge or different challenge type");
            // If there's no challenge or it's already solved, continue
        }
    }
}
