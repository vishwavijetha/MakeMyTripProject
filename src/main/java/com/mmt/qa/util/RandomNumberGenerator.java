package com.mmt.qa.util;

import java.util.Random;

public class RandomNumberGenerator{

	public static synchronized int getRandomNumber(int maxRange, int minRane) {
		Random rand = new Random();
		int randomNum = rand.nextInt((maxRange - minRane) + minRane) + 1;
		return randomNum;
	}
}
