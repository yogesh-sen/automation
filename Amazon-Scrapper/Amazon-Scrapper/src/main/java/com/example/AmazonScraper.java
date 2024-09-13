package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AmazonScraper {

	public static void main(String[] args) {
		// Set up WebDriverManager to manage the ChromeDriver binary
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			// Open Amazon.in
			driver.get("https://www.amazon.in");

			// Search for 'lg soundbar'
			WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
			searchBox.sendKeys("lg soundbar");
			searchBox.submit();

			// Wait for the results to load
			Thread.sleep(3000);

			// Map to store product names and prices
			Map<String, Integer> products = new HashMap<>();

//          Get product elements
			List<WebElement> productElements = driver.findElements(By.xpath("//*[starts-with(@data-cel-widget, 'search_result_')]"));
			
			ArrayList<WebElement> list = new ArrayList<>(productElements);
			
			
			for (int i=0;i<list.size()-1;i++) {
				try {
					String productName = list.get(i).findElement(By.xpath("//*[starts-with(@data-cel-widget, 'search_result_"+(i+1)+"')]//span[@class='a-size-medium a-color-base a-text-normal']")).getText();
					
					String priceText = list.get(i).findElement(By.xpath("//*[starts-with(@data-cel-widget, 'search_result_"+(i+1)+"')]//span[@class='a-price-whole']")).getText().replace(",", "");
					int price = Integer.parseInt(priceText);
					products.put(productName, price);
					
					
				}
				catch(NoSuchElementException ex)
				{
					continue;
				}
				catch (Exception e) {
					// If price is not found, consider it as zero
					String productName = list.get(i).findElement(By.xpath("//*[starts-with(@data-cel-widget, 'search_result_"+(i+1)+"')]//span[@class='a-size-medium a-color-base a-text-normal']")).getText();
					products.put(productName, 0);
				}
			}
			
			// Sort the products by price
			List<Map.Entry<String, Integer>> sortedProducts = products.entrySet().stream()
					.sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

			
			// Print the sorted product names and prices
			for (Map.Entry<String, Integer> entry : sortedProducts) {
				
				System.out.println(entry.getValue() + " " + entry.getKey()+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Close the browser
			driver.quit();
		}
	}
}
