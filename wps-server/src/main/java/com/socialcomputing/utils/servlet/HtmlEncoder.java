package com.socialcomputing.utils.servlet;

public class HtmlEncoder
{
    private static char[] token = { '&', '<', '>', '"' };

    private static String[] html = { "amp", "lt", "gt", "quot" };

    public static String encode(String src)
    {
     	StringBuffer ret = new StringBuffer();
        for (int i = 0; i < src.length(); i++)
        {
            char c = src.charAt(i);
            boolean isConverted = false;

            for (int j = 0; j < token.length; j++)
                if (c==token[j])
                {
	                ret.append( '&');
	                ret.append( html[j]);
	                ret.append( ';');
                    isConverted = true;
                    break;
                }
        	if (!isConverted) ret.append( c);
        }

        return ret.toString();
    } 
}


