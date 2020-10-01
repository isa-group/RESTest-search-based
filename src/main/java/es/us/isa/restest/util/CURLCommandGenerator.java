package es.us.isa.restest.util;

import es.us.isa.restest.testcases.TestCase;

/**
 * Class that genereates a valid CURL command from TestCase a
 * a test case that can be used to reproduce the invocation to
 * the API specified by the TestCase.
 * 
 * @see https://en.wikipedia.org/wiki/CURL
 * @see https://curl.haxx.se/docs/manpage.html
 * 
 * @author japarejo
 *
 */
public class CURLCommandGenerator {

	private static final String command="curl";
	
	public static String generate(String basePath,TestCase testCase) {
		return command +generateMethod(testCase)+ generateURL(basePath,testCase) + generateHeaders(testCase);
	}
	
	private static String generateMethod(TestCase testCase) {
		String result="";
		if(testCase.getMethod()!=null)
			result= " -X "+testCase.getMethod().toString();
		return result;
	}

	public static String generateURL(String basePath,TestCase testCase) {
		return " "+basePath+generatePath(testCase) + generateQueryParameters(testCase);
	}
	
	private static String generateQueryParameters(TestCase testCase) {
		StringBuilder builder=new StringBuilder("?");
		int param=0;
		for(String queryParameter:testCase.getQueryParameters().keySet()) {
			if(param!=0)
				builder.append("&");
			builder.append(queryParameter);
			builder.append("=");
			builder.append(testCase.getQueryParameters().get(queryParameter));
			param++;
		}		
		return builder.toString();
	}

	public static String generatePath(TestCase testCase)
	{
		String result=testCase.getPath();
		for(String pathParameter:testCase.getPathParameters().keySet())
			result=result.replace("{"+pathParameter+"}",testCase.getPathParameters().get(pathParameter));
		return result;
	}
	
	public static String generateHeaders(TestCase testCase) {
		StringBuilder builder=new StringBuilder();
		for(String header:testCase.getHeaderParameters().keySet()) {
			builder.append(" -H \"");
			builder.append(header);
			builder.append(": ");
			builder.append(testCase.getHeaderParameters().get(header));
			builder.append("\"");
		}
		return builder.toString();
	}
	
}
