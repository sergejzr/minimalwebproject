package de.bonn.hrz.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class StringHelper 
{
	/**
	 * If the string is longer than maxLength it is split at the nearest blankspace
	 * 
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static String shortnString(String str, int maxLength)
	{
		if(null == str)
			return "";
		
		if(str.length() > maxLength)
		{
			
		    int endIdx = maxLength;
		    while (endIdx > 0 && str.charAt(endIdx) != ' ' && str.charAt(endIdx) != '\n')
		        endIdx--;
		 
		    str = str.substring(0, endIdx)+ "...";
		}
		return str;
	}
	
	public static String implode(Collection<String> list, String delim) 
	{
	    StringBuilder out = new StringBuilder();
	    for(String item : list) 
	    {
	        if(out.length()!=0) 
	        	out.append(delim);
	        out.append(item);
	    }
	    return out.toString();
	}
	
	public static String implodeInt(Collection<Integer> list, String delim) 
	{
	    StringBuilder out = new StringBuilder();
	    for(Integer item : list) 
	    {
	        if(out.length()!=0) 
	        	out.append(delim);
	        out.append(item.toString());
	    }
	    return out.toString();
	}
	
	/**
	 * Make first character upper case
	 * @param input
	 * @return
	 */
	public static String ucFirst(String input)
	{
		return input.substring(0,1).toUpperCase() + input.substring(1,input.length());
	}
	
	/**
	 * Translates a string into application/x-www-form-urlencoded format using a specific encoding scheme. <br/>
	 * This method uses UTF-8. <br/> 
	 * It's just a confinience method to get rid of the UnsupportedEncodingException. 
	 * @param str
	 * @return
	 */
	public static String urlEncode(String str)
	{
		if(null == str)
			return "";
		try {
			return URLEncoder.encode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}
	
	public static String urlDecode(String str)
	{
		if(null == str)
			return "";
		try {
			return URLDecoder.decode(str, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}

    public static String decodeBase64(String encoded) {
        //decode byte array
        byte[] decoded = Base64.decodeBase64(encoded.getBytes());
        //byte to string and return it
        return new String(decoded);
    }
    
    public static String encodeBase64(byte[] bytes) {
        //decode byte array
        byte[] encoded = Base64.encodeBase64(bytes);
        //byte to string and return it
        return new String(encoded);
    }
    
    public static void main(String[] args)
    {

    }
}
