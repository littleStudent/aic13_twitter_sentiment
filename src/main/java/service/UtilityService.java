package service;

/**
 * This class provides utilities in form of static methods
 * @author Stefan Zischka 0828584
 *
 */
public class UtilityService {

	/**
	 * This method checks if a string is parseable into Integer 
	 * @param string to check
	 * @return true if string is parseable into integer, false if not
	 */
	public static boolean isInteger(String string){
		try{
			Integer.valueOf(string);
			return true;
		}
		catch(NumberFormatException e){
			return false;			
		}
	}
	
	
	/**
	 * This method checks if a Dates read from two strings are valid.
	 * A date is valid if its in the format YYYY-MM-DD 
	 * First date has to be before second date in time
	 * @param date to validate
	 * @return true if date has the form YYYY-MM-DD and first date lays
	 */
	public static boolean checkIfDatesValid(String dateFrom, String dateTo){
		
		//Both dates have to be valid
		if(checkIfDateValid(dateFrom) && checkIfDateValid(dateTo)){
			
			String[] splitFromDate = dateFrom.split("-");
			String[] splitToDate = dateTo.split("-");		
			
			if((Integer.parseInt(splitFromDate[0]) < Integer.parseInt(splitToDate[0])) ||
			  ((Integer.parseInt(splitFromDate[0]) <= Integer.parseInt(splitToDate[0])) && (Integer.parseInt(splitFromDate[1]) < Integer.parseInt(splitToDate[1]))) ||
			  ((Integer.parseInt(splitFromDate[0]) <= Integer.parseInt(splitToDate[0])) && (Integer.parseInt(splitFromDate[1]) <= Integer.parseInt(splitToDate[1])) 
			  && (Integer.parseInt(splitFromDate[2]) < Integer.parseInt(splitToDate[2])))){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method checks if a Date read from a string is valid.
	 * A date is valid if its in the format YYYY-MM-DD 
	 * @param date to validate
	 * @return true if date has the form YYYY-MM-DD
	 */
	private static boolean checkIfDateValid(String date){
		
		 String[] splitDate = date.split("-");
		 
		 //TODO: Further checking of dates? Take month into account to determine number of valid days, leap years
		 if(splitDate.length == 3){	 
			 if(isInteger(splitDate[0]) && isInteger(splitDate[1]) && (Integer.parseInt(splitDate[1]) <= 12) && isInteger(splitDate[2]) && (Integer.parseInt(splitDate[2]) <= 31)){
				 return true;
			 }
		 }
		 return false;
	}
	
}
