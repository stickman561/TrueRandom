package com.snakec0de.truerandom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class TrueRandomCore {

	/**
	 * Main method for TrueRandom project.
	 */
	public static void main(String[] args) {
		
		// Read input parameters
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			System.out.print("Input min value: ");
			int min = Integer.parseInt(reader.readLine().trim());
			
			System.out.print("Input max value: ");
			int max = Integer.parseInt(reader.readLine().trim());
			
			System.out.print("Input number of values to generate: ");
			int its = Integer.parseInt(reader.readLine().trim());
			
			for(int x = 0; x < its; x++) {
				System.out.println(getRandom(min, max));
			}
		}
		
		catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Generates a truly random number through HotBits
	 * 
	 * @param  min   min value
	 * @param  max   max value
	 * 
	 * @return     the string-converted number (from BigDecimal)
	 */
	private static String getRandom(int min, int max) {
		// Call native pull method
		BigDecimal value = parseData();
		
		// Maximum possible value in bit range of HotBits
		BigDecimal maxValue = new BigDecimal(new BigInteger(Constants.MAX_RANDOM, 16));
		
		// Get percentage of maximum value selected by random seed with
		// 32,768 decimal places of precision. More than precise enough
		// for this application, but prevents infinite decimals.
		BigDecimal position = value.divide(maxValue, 32768, RoundingMode.HALF_UP);
		
		// Constrain value to within min-max range. This is the limiting
		// factor of the program for any number occupying less than 4,096
		// bytes of memory. (Anything under (2 ^ 4,095) - 1) This is huge.
		BigDecimal result = position.multiply(BigDecimal.valueOf(max - min)).setScale(0, RoundingMode.HALF_UP).add(BigDecimal.valueOf(min));
		
		return result.toString();
	}
	
	/**
	 * Pulls random number data from HotBits secure webserver
	 * 
	 * @return     the hex data converted to a BigDecimal
	 */
	private static BigDecimal parseData() {
		try {
			// Connect to HotBits secured server
			String hotBits = "https://www.fourmilab.ch/cgi-bin/Hotbits.api?nbytes=2048&fmt=hex&npass=1&lpass=8&pwtype=3&apikey=&pseudo=pseudo";
			Document doc = Jsoup.connect(hotBits).get();
			
			// Isolate block containing random hex
			Elements hexBlock = doc.getElementsByTag("pre");
			
			// Filter hex to remove HTML tags and whitespace
			String hex = hexBlock.text().replaceAll("\\s+","");
			
			// Convert hex to BigDecimal number
			return new BigDecimal(new BigInteger(hex, 16));
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
