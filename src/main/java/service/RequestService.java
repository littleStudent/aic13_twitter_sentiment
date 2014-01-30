package service;


import java.io.BufferedReader;
import java.io.IOException;

import twitter.scheduler.ThreadPoolScheduler;

import service.UtilityService;

/**
 * Processes the requests of the user
 * @author Stefan Zischka 0828584
 *
 */
public class RequestService {
	
	private BufferedReader stdIn;
	private ThreadPoolScheduler scheduler;
	private int threadCount;
	
	public RequestService(BufferedReader stdIn, int threadCount){
		this.stdIn = stdIn;
		this.threadCount = threadCount;
		scheduler = new ThreadPoolScheduler(threadCount);
		
		scheduler.startEmptyTask();
	}
	
	
	/**
	 * This method processes the request of the user
	 * @param input - the command line input of the user
	 */
	public void processCommand(String input){
		
		input = input.trim();
		
		//Perform sentiment analysis
		if(input.length() >= 8 && input.substring(0,8).equals("!analyze")){
			analyze(input);
		}
		//Shutdown server
		else if(input.equals("!exit")){
			shutdown();
		}
		else{
			System.out.println("Illegal command");
		}
	}
	
	/**
	 * This method performs the sentiment analysis
	 * Method expects format of input "!analyze <ANALYSIS-STRING> <DATE-FROM> <DATE-TO> where dates are in format YYYY-MM-DD
	 * @param input - the command line input of the user
	 */
	private void analyze(String input){
		
		String analysisString;
		String dateFrom;
		String dateTo;
		
		String[] splitInput = input.split(" ");

		
		//Input validation
		if (splitInput.length != 4){
			System.out.println("Illegal argument size!");
			System.out.println("Expected input: !analyze <ANALYSIS-STRING> <DATE-FROM> <DATE-TO> where dates are in format YYYY-MM-DD");
			return;
		}
		
		analysisString = splitInput[1];
		dateFrom = splitInput[2];
		dateTo = splitInput[3];
		
		if(!UtilityService.checkIfDatesValid(dateFrom, dateTo)){
			System.out.println("Illegal date format");
			System.out.println("Expected input: !analyze <ANALYSIS-STRING> <DATE-FROM> <DATE-TO> where dates are in format YYYY-MM-DD");
			System.out.println("Furthermore, <DATE-FROM> has to be before <DATE-TO>");
			return;
		}

		//TODO: Remove Debug Code
		
		System.out.println("Starting analysis.");
		long start = System.nanoTime();
		
		double result = scheduler.sentimentAnalysis(analysisString, dateFrom, dateTo, threadCount);
	
		System.out.println("Results for "+analysisString+" period "+dateFrom+" to "+dateTo+" : " + result);
		
		
		
		//TODO: Remove Debug Code
		long elapsed = (System.nanoTime() - start) / 1000000;
		long min = elapsed / (60 * 1000);
	    long sec = (elapsed % (60 * 1000)) / 1000;
	    long msec = elapsed % 1000;
	    System.out.println(String.format("Elapsed: %02d:%02d.%03d", min, sec, msec));
	}
	
	
	/**
	 * This method ensures a clean shut down of the server
	 **/
	public void shutdown(){
		System.out.println("System is shutting down...");
		try{
			stdIn.close();
		}catch (IOException e) {
			System.err.println("Error: Clean shutdown not possible!");
		}
	}

}
