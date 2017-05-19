package com.lydanny.personalnewsfeed;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * ----------------------------------------------|
 * Project Name: Personal News Feed
 * File Name:
 * AUTHOR: Danny Ly | RedKlouds                  |
 * Created On: 5/19/2017                         |
 * ----------------------------------------------|
 *
 * File Description:
 * -> (Helper Class) This class is in charge of handling the calls to the web server
 *  to make fetches of data, and gather the json we need to parse
 * Assumptions:
 * ->
 **/
public class HttpHandler {
    /**
     * Function: Default Constructor
     * Description: Called to initalize a blank object such that we can continue
     * to reuse this object for more updated requests later.
     */
    public HttpHandler(){}

    /**
     * Function: makeServiceCall
     * Description: creates a GET REquests to the HTTP provided location, using
     * an InputStream buffer
     * PRECONDITIONS:
     *  ->STRING URL
     * @param requestUrl
     * POSTCONDITION:
     *  ->Returns a string that repersents a JSON string of the data
     * @return
     * ASSUMPTIONS:
     *  convertStreamToString has been properly configured and implemented
     */
    public String makeServiceCall(String requestUrl){
        //set our defaulse resones to null
        String response = null;
        try{
            //make a url object from the given string parameter
            URL url = new URL(requestUrl);
            //make a connection object given the url object,
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //configure the connection setting's requests type
            conn.setRequestMethod("GET");
            //create a buffer for where our data will be stored
            InputStream in = new BufferedInputStream(conn.getInputStream());
            //convert our string stream to a string
            response = convertStreamToString(in);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (ProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Function: Helper convertStreamToString
     * Description: Helper method , given an input stream buffer
     * read each line and append it to a string builder
     * PRECONDITION:
     * @param inputStream
     * @return
     * POSTCONDITION:
     * returns the String repersentation of our given buffer input
     */
    private String convertStreamToString(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringbuilder = new StringBuilder();
        //store each line to parse
        String line;
        try{
            //while we still have something to read
            //continue reading and parsing
            while( (line = reader.readLine()) != null){
                //add the line to the string builder
                //append a newline seperator
                stringbuilder.append(line).append('\n');
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            //properly close the inputstream
            try{
                inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        //return the string representation of our string built from the stream
        return stringbuilder.toString();
    }
}
