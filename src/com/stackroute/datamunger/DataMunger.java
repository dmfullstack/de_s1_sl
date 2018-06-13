package com.stackroute.datamunger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class DataMunger {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String queryString;
		/*read the query from the user*/
		System.out.println("Please enter your query:");
		System.out.println("");
		queryString = scanner.nextLine();
		DataMunger dataMunger = new DataMunger();
		dataMunger.parseQuery(queryString);

	}
	
	/* we are creating multiple methods, each of them are responsible for 
	 * extracting a specific part of the query. However, the problem statement requires
	 * us to print all elements of the parsed queries. Hence, to reduce the complexity,
	 * we are using the parseQuery() method. From inside this method, we are calling 
	 * all the methods together, so that we can call this method only from main() method
	 * to print the entire output in console*/
	public void parseQuery(String queryString) {

		getSplitStrings(queryString);
		getFile(queryString);
		getBaseQuery(queryString);
		getConditionsPartQuery(queryString);
		getConditions(queryString);
		getLogicalOperators(queryString);
		getFields(queryString);
		getOrderByFields(queryString);
		getGroupByFields(queryString);
		getAggregateFunctions(queryString);
		
		
	}
	
	
	/*this method will split the query string based on space into an array of words 
	 * and display it on console*/
	public String[] getSplitStrings(String queryString) {
		
		String[] queryStringTokens=null;
		
		queryString=queryString.toLowerCase();
		
		queryStringTokens = queryString.split("\\s");
		// parse the queryString into words and display
		for(String queryStringToken:queryStringTokens) {
			System.out.println(queryStringToken);
		}
		
		return queryStringTokens;
	}


	/* 
	 * extract the name of the file from the query. File name can be found after a space
	 * after "from" clause. 
	 * Note: CSV file can contain a field that contains from as a part of the 
	 * column name. 
	 * For eg: from_date,from_hrs etc.
	 * 
	 * Please consider this while extracting the file name in this method.
	 */
	public String getFile(String queryString) {
		
		queryString=queryString.toLowerCase();
		// get and display the filename
		String file = queryString.split("from")[1].trim().split("\\s+")[0];
		System.out.println("File:" + file);
		return file;
	}
	
	/* This method is used to extract the baseQuery from the query string. BaseQuery 
	 * contains from the beginning of the query till the where clause
	 * 
	 * Note: 
	 * 1. the query might not contain where clause but contain order by or group by 
	 * clause
	 * 2. the query might not contain where, order by or group by clause
	 * 3. the query might not contain where, but can contain both group by and order by
	 * clause
	 * */
	public String getBaseQuery(String queryString) {
		queryString=queryString.toLowerCase();
		// getting the baseQuery and display
		String baseQuery = queryString.split("where|order by|group by")[0];
		System.out.println("Base Query:" + baseQuery);
		return baseQuery;

	}
	
	/* This method is used to extract the conditions part from the query string.
	 * The conditions part contains starting from where keyword till the next keyword,
	 * which is either group by or order by clause. In case of absence of both
	 * group by and order by clause, it will contain till the end of the 
	 * query string. 
	 *	Note:
	 *  ----- 
	 *  1. The field name or value in the condition can contain keywords as a substring.
	 *  For eg: from_city,job_order_no,group_no etc.
	 *  2. The query might not contain where clause at all.
	 * */
	public String getConditionsPartQuery(String queryString) {
		
		queryString=queryString.toLowerCase();
		// get and display the where conditions part(if where condition exists)
		if (queryString.contains("where")) {
			String whereClauseQuery = queryString.split("where")[1].split("order by")[0].split("group by")[0];
			System.out.println("Where Clause:" + whereClauseQuery);
			return whereClauseQuery;
		}
		return null;

	}
	
	/*This method will extract condition(s) from the query string. The query can contain
	 * one or multiple conditions. In case of multiple conditions, the conditions will
	 * be separated by AND/OR keywords. 
	 *  for eg: 
	 *  Input: select city,winner,player_match from ipl.csv where season > 2014 and city ='Bangalore'
	 *  
	 *  This method will return a string array ["season > 2014","city ='Bangalore'"] and 
	 *  print the array
	 *  
	 *  Note:
	 *  ----- 
	 *  1. The field name or value in the condition can contain keywords as a substring.
	 *  For eg: from_city,job_order_no,group_no etc.
	 *  2. The query might not contain where clause at all.
	 * */
	public String[] getConditions(String queryString) {
		
		String[] expressions=null;
		
		queryString=queryString.toLowerCase();
		if (queryString.contains("where")) {
			String whereClauseQuery = queryString.split("where")[1].split("order by")[0].split("group by")[0];
			
			expressions = whereClauseQuery.trim().split("\\s+and\\s+|\\s+or\\s+");
			System.out.println("Expressions:");
			for(String expression:expressions) {
				System.out.println(expression);
			}
		
		}
		return expressions;
	}
	
	
	/*This method will extract logical operators(AND/OR) from the query string.
	 * The extracted logical operators will be stored in a String array which will be
	 * returned by the method and the same will be printed
	 * Note:
	 * -------
	 * 1. AND/OR keyword will exist in the query only if where conditions exists and
	 * 	  and it contains multiple conditions.
	 * 2. AND/OR can exist as a substring in the conditions as well.
	 * 	  For eg: name='Alexander',color='Red' etc. Please consider these as well when
	 * 	  extracting the logical operators.
	 * 
	 * */
	public String[] getLogicalOperators(String queryString) {

		String[] expressions=null;
		String[] logicalOperators=null;
		
		queryString=queryString.toLowerCase();
		
		if (queryString.contains("where")) {
			String whereClauseQuery = queryString.split("where")[1].split("order by")[0].split("group by")[0];
						
			expressions = whereClauseQuery.split("\\s+and\\s+|\\s+or\\s+");
		
			// get the logical operators(applicable only if multiple conditions exist)
			int size = expressions.length;
			logicalOperators=new String[size-1];
			int i = 0;
			for (String expression : expressions) {
			if (i++ < size - 1)
				logicalOperators[i-1]=whereClauseQuery.split(expression.trim())[1].split("\\s+")[1].trim();
			}
			
			for(String logicalOperator:logicalOperators) {
				System.out.println(logicalOperator);
			}
		}
		
		return logicalOperators;
		
	}
	
	/*This method will extract the fields to be selected from the query string.
	 * The query string can have multiple fields separated by comma. The extracted
	 * fields will be stored in a String array which is to be printed in console as 
	 * well as to be returned by the method
	 * 
	 * Note:
	 * ------
	 *  1. The field name or value in the condition can contain keywords as a substring.
	 *     For eg: from_city,job_order_no,group_no etc. 
	 *  2. The field name can contain '*'
	 * 
	 * */
	public String[] getFields(String queryString) {
		
		queryString=queryString.toLowerCase();
		
		String baseQuery = queryString.split("where|ordery by|group by")[0];
		String[] fields = baseQuery.trim().split("select")[1].split("from")[0].trim().split(",");
		System.out.println("Selected fields:");
		for(String field:fields) {
			System.out.println(field);
		}
		return fields;
		
	}
	
	/* 
	 * This method extracts the order by fields from the query string. 
	 * Note:
	 * ------
	 * 1. The query string can contain more than one order by fields.
	 * 2. The query string might not contain order by clause at all.
	 * 3. The field names, condition values might contain "order" as a substring.
	 *    For eg: order_number,job_order
	 *    Consider this while extracting the order by fields 
	 * */
	public String[] getOrderByFields(String queryString) {
		
		String orderByFields[]=null;
		
		queryString=queryString.toLowerCase();
		// get order by fields if order by clause exists
		if (queryString.contains("order by")) {
			orderByFields = queryString.split("\\s+order by\\s+")[1].split("\\s+group by\\s+")[0].split(",");
			System.out.println("Order by fields:" + Arrays.toString(orderByFields));
		}
		return orderByFields;
	}
	
	/* 
	 * This method extracts the group by fields from the query string. 
	 * Note:
	 * ------
	 * 1. The query string can contain more than one group by fields.
	 * 2. The query string might not contain group by clause at all.
	 * 3. The field names, condition values might contain "group" as a substring.
	 *    For eg: newsgroup_name
	 *    Consider this while extracting the group by fields 
	 * */
	public String[] getGroupByFields(String queryString) {
		String groupByFields[]=null;
		
		queryString=queryString.toLowerCase();
		// get group by fields if group by clause exists
		if (queryString.contains("group by")) {
			groupByFields = queryString.split("\\s+group by\\s+")[1].split("\\s+order by\\s+")[0].split(",");
			System.out.println("Group by field:"+groupByFields[0]);

		}
		return groupByFields;
	}
	
	
	/* 
	 * This method extracts the aggregate functions from the query string. 
	 * Note:
	 * ------
	 * 1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg" followed
	 * by "("
	 * 2. The field names might contain "sum"/"count"/"min"/"max"/"avg" as a substring.
	 *    For eg: account_number,consumed_qty,nominee_name
	 *    
	 *    Consider this while extracting the aggregate functions 
	 * */
	public String[] getAggregateFunctions(String queryString) {
		
		String aggregateFunctions[]=null;
		
		queryString=queryString.toLowerCase();
		// parse and display aggregate functions(if applicable)
		if (queryString.contains("sum(") || queryString.contains("count(") || queryString.contains("min(")
				|| queryString.contains("max(") || queryString.contains("avg(")) {
			aggregateFunctions = queryString.split("from")[0].split("select")[1].trim().split(",");			
		}
		return aggregateFunctions;
	}
	
	
	


}
