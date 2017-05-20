package com.lydanny.personalnewsfeed;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ----------------------------------------------|
 * Project Name: Personal News Feed
 * File Name:   MainActivity.java
 * AUTHOR: Danny Ly | RedKlouds                  |
 * Created On: 5/19/2017                         |
 * ----------------------------------------------|
 *
 * File Description:
 * -> This class is incharge of populating the main view
 *  after login authentication has been made. Once presented
 *  This class also makes the HTTP GET requests to the webservice for
 *  JSON data, once the data has been gather, present them in a listview
 *  -> This is the main content view, after authentication,
 *
 * Assumptions:
 * -> URL Provided is valid URL, returning proper formatted JSON string
 **/
public class MainActivity extends AppCompatActivity {
    //url from which we are going to make the HTTP GET requets
    private String url = "http://www.lydanny.com/PersonalNewsFeed";
    //variables to make references to the various view elements
    private ProgressDialog progressDialog;
    private ListView listView;
    //for our listview adapter a array of hashmaps, which hashmaps hold <k,v> of also
    //type string.

    ArrayList<HashMap<String,String>> dataFeedList;
    /**
     *Function: MainActivity.onCreate
     * Description: called upon creation of this class
     * ->initiates the contactList Array
     * ->Gets a reference to the contact list layout from layout manager
     * -> executes the asychrouns HTTP requests to populate the main view
     * PRECONDITIONS:
     * @param savedInstanceState
     * ->None
     * POSTCONDITION:
     *  -> main list view has been populated
     *  ASSUMPTIONS:
     *  -> URL provided returns valid data in form of JSON
     *  -> listview and list view items have been properly configured
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize the arraylist
        dataFeedList = new ArrayList<>();

        //make the nessecary references to view objects here
        listView = (ListView)findViewById(R.id.main_content_list);
        new GetContacts().execute();
    }

    /**
     *
     * Private Helper class GetContacts
     * Description: An Async class to make the actual HTTP calls on a background
     * thread, onBackground()
     * -> This types of classes are typically for asynchronous jobs that require some
     * processing in the background
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        /**
         * Function GetContacts.onPreExecute()
         * Description: Function called to 'setup' our asychronous doInBackground task
         * PRECONDITIONS:
         *  ->None
         * POSTCONDITIONS:
         *  ->Presents the progress dialog box
         */
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //show the progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data...");
            //prevent an cancel from user
            progressDialog.setCancelable(false);
            //present the dialog
            progressDialog.show();
        }

        /**
         * Function GetContacts.doInBackground
         * Description: Asynchronous task to make the HTTP GET
         * request performed in the background.
         * PRECONDITIONS:
         *  ->None
         * @param arg0
         *
         * POSTCONDITIONS:
         *      -> Populates a JSON string in the form of data to be processed,
         *      -> ContactList will be populated as a array of type HashMaps
         *      -> Each HashMap will contain information pertaining to each list
         *      item.
         * ASSUMPTIONS:
         *      -> the provided parametric url returns valid JSON string
         */
        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler httpHandler = new HttpHandler();
            //make the requests to the url and get response
            String jsonString = httpHandler.makeServiceCall(url);
            //have a successful request call
            if(jsonString != null){
                try{
                    JSONObject jsonObj = new JSONObject(jsonString);

                    //get the json dictionary from Reddit post
                    JSONArray reddit_dict = jsonObj.getJSONArray("reddit");
                    //iterate through the array, that the key 'reddit' is holding
                    //reddit is a key to the value of an array of post
                    //{'reddit':X}
                    for(int i =0; i < reddit_dict.length(); i++){
                        //X = [],[],[],[]
                        //iterate through thte array that reddit value is holding
                        //get the specific dictary at given index of i
                        JSONObject reddit_Post = reddit_dict.getJSONObject(i);
                        //[{X},{X},{X},{X}]
                        //access the data within each dictionary

                        String title =  reddit_Post.getString("title");
                        String url = reddit_Post.getString("url");
                        String date = reddit_Post.getString("date");
                        String score = reddit_Post.getString("score");
                        String num_comments = reddit_Post.getString("num_comments");

                        //add the following data into a hashMap and populate our post data array
                        //the dataFeedList is an array of hashMap
                        HashMap<String,String> temp_data_map = new HashMap<>();
                        //propogate the temp hash

                        temp_data_map.put("title",title);
                        temp_data_map.put("url", url);
                        temp_data_map.put("date","\t" + date + " ");
                        temp_data_map.put("score","Votes: " + score + " |");
                        temp_data_map.put("num_comments"," " + num_comments + " comments |");
                        //addm each child hashmap to datafeedList
                        dataFeedList.add(temp_data_map);
                        Log.d("ARRAY", temp_data_map.toString());
                    }
                    //loop through all the contacts/elements in the array
                }catch( JSONException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        /**
         * Description: Called after our Asynchronous Task(GET REQUESTS)
         * has finished executing.
         * PRECONDITIONS:
         *  ->Result is the string that has been populated to fit the
         *  list view adapter
         *  POSTCONDITIONS:
         *      ->Attaches a simple listvioew adapter to the list view in main_activity
         *      and populates the list items with json from the get requests
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing()) {
                //dismiss the progress dialoag since we have successfully
                //gotten the data
                progressDialog.dismiss();
            }
            //update the list view
            //configure the lsit adapter to MainActivity
            //given the Contacts list of hashmaps objects
            //using the list item layout
            //attach each field with its corresponding key value
            //to its corresponding layout id's
            //inside we access the from, title,url,score hashmap
            //each element repersents a single row in listview
            //notice its a 1 to 1 mapping from the two
            //arrays String -> int id layout
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    dataFeedList,
                    R.layout.list_item,
                    new String[]{
                            "title",
                            "score",
                            "num_comments",
                            "date"}
                    ,
                    new int[]{
                            R.id.listing_title,
                            R.id.listing_num_votes,
                            R.id.listing_num_comments,
                            R.id.listing_date
                    }
            );
            //attach the adapter to the list view object
            listView.setAdapter(adapter);
        }
    }
}
