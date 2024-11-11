package com.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

public class CloudflareHelper {
    public static void retryWithCloudflare(Page page, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                page.navigate("https://accessibility.moda.gov.tw/");
                handleCloudflareChallenge(page);

                // Check if we're past Cloudflare
                if (page.title().contains("無障礙網路空間服務網")) {
                    return;
                }
            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    throw new RuntimeException("Failed after " + maxAttempts + " attempts", e);
                }
                System.out.printf("Attempt %d failed, retrying...%n", attempt);
                try {
                    Thread.sleep(5000); // Wait before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    private static void handleCloudflareChallenge(Page page) {
        // 假設這是一個處理 Cloudflare 挑戰的方法
        // 你需要根據實際情況實現這個方法
    }

    public static void saveAuthenticatedState(Browser browser, String statePath) {
        try (BrowserContext context = browser.newContext()) {
            Page page = context.newPage();
            page.navigate("https://accessibility.moda.gov.tw/");
            // 你的其他代碼
            page.context().storageState(new BrowserContext.StorageStateOptions().setPath(java.nio.file.Paths.get(statePath)));
        }
    }
}