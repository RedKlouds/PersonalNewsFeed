package com.lydanny.personalnewsfeed;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
 *  JSON data, once the data has been gather, present them in a ListView
 *  -> This is the main content view, after authentication,
 *
 * Assumptions:
 * -> URL Provided is valid URL, returning proper formatted JSON string
 * todo
 * ->add function to change the subreddit section and repopulate
 * the list view
 **/
public class MainActivity extends AppCompatActivity {
    //url from which we are going to make the HTTP GET requets
    private String url = "http://www.lydanny.com/PersonalNewsFeed";
    //variables to make references to the various view elements
    private ProgressDialog progressDialog;
    private ListView listViewReddit;
    //for our ListView adapter a array of hashmaps, which hashmaps hold <k,v> of also
    //type string.

    private ListView ListViewFinviz;
    ArrayList<HashMap<String,String>> dataFeedList;
    ArrayList<HashMap<String,String>> dataFeedList_finviz;
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
     *  -> ListView and list view items have been properly configured
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize the arraylist
        dataFeedList = new ArrayList<>();
        dataFeedList_finviz = new ArrayList<>();

        //make the nessecary references to view objects here
        listViewReddit = (ListView) findViewById(R.id.main_content_list);
        ListViewFinviz = (ListView)findViewById(R.id.main_content_list_2);
        new GetContacts().execute();

        /**
         * This is called when a list item in ListView 2 is clicked
         * todo
         * Implmement a detailed item list , to display the list data
         * furthur
         */
        ListViewFinviz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView t = (TextView) view.findViewById(R.id.listing_title);
                String text = t.getText().toString();
                Log.d("CLICK @@@@@@", "DATA : " +  text);
                Log.d("CLICKED @@@@@@@", "Clicked at positon : " + position);

            }
        });
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
                    //call the helper to parse the jsonString to a dictionary,
                    //then append that parsed data to the Databuffer array
                    parseFinvizJSON(jsonObj);
                    //parse the JSON string text into the buffers
                    parseRedditJSON(jsonObj);
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
            //each element repersents a single row in ListView
            //notice its a 1 to 1 mapping from the two
            //arrays String -> int id layout
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    dataFeedList,
                    R.layout.list_item_reddit,
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
            //list adapter for finviz data
            ListAdapter adapter_finviz = new SimpleAdapter(
                    MainActivity.this,
                    dataFeedList_finviz,
                    R.layout.list_item_finviz,
                    new String[]{
                            "signal",
                            "symbol",
                            "volume",
                            "change",
                            "price"
                    }
                    ,
                    new int[]{
                            R.id.finviz_signal,
                            R.id.finviz_symbol,
                            R.id.finviz_volume,
                            R.id.finviz_change,
                            R.id.finviz_price
                    }
            );
            //attach the adapter to the list view object
            //ListView->listItem->mainview
            listViewReddit.setAdapter(adapter);

            ListViewFinviz.setAdapter(adapter_finviz);


        }

        /**
         * Function (helper)parseFinvizJSON
         * Description: Function that takes a JSON STRING, and
         * popualtes a hashmap for each element, then adds the respective
         * populated data hashmap into the arrayList for data buffering
         * PRECONDITION: No JSONEXECPETIONS
         * @param redditJSON
         * @throws JSONException
         * POSTCONDITIONS:
         * ->Array list is buffered
         */
        private void parseRedditJSON(JSONObject redditJSON) throws JSONException {
            //JSONObject jsonObj = new JSONObject(redditJSON);
            //get the json dictionary from Reddit post
            JSONArray reddit_dict = redditJSON.getJSONArray("reddit");
            //iterate through the array, that the key 'reddit' is holding
            //reddit is a key to the value of an array of post
            //{'reddit':X}
            for(int i =0; i < reddit_dict.length(); i++) {
                //X = [],[],[],[]
                //iterate through thte array that reddit value is holding
                //get the specific dictary at given index of i
                JSONObject reddit_Post = reddit_dict.getJSONObject(i);
                //[{X},{X},{X},{X}]
                //access the data within each dictionary

                String title = reddit_Post.getString("title");
                String url = reddit_Post.getString("url");
                String date = reddit_Post.getString("date");
                String score = reddit_Post.getString("score");
                String num_comments = reddit_Post.getString("num_comments");

                //add the following data into a hashMap and populate our post data array
                //the dataFeedList is an array of hashMap
                HashMap<String, String> temp_data_map = new HashMap<>();
                //propogate the temp hash

                temp_data_map.put("title", title);
                temp_data_map.put("url", url);
                temp_data_map.put("date", "  " + date + " ");
                temp_data_map.put("score", "Votes: " + score + "   | ");
                temp_data_map.put("num_comments", " " + num_comments + " comments   | ");
                //addm each child hashmap to datafeedList
                dataFeedList.add(temp_data_map);
            }
        }

        /**
         * Function (helper)parseFinvizJSON
         * Description: Function that takes a JSON STRING, and
         * popualtes a hashmap for each element, then adds the respective
         * populated data hashmap into the arrayList for data buffering
         * PRECONDITION: No JSONEXECPETIONS
         * @param finvizJSON
         * @throws JSONException
         * POSTCONDITIONS:
         * ->Array list is buffered
         */
        public void parseFinvizJSON(JSONObject finvizJSON) throws JSONException {
            //get the finvi array, since finviz KEY , has an value
            //of TYPE ARRAY
            //JSONObject jsonObj = new JSONObject(finvizJSON);
            JSONArray finviz_dict = finvizJSON.getJSONArray("finviz");

            for (int i = 0; i < finviz_dict.length(); i++) {
                //parse the array for each dictionary element and store its
                //contents

                JSONObject finviz_stock_item = finviz_dict.getJSONObject(i);

                String symbol = finviz_stock_item.getString("index");
                String signal = finviz_stock_item.getString("signal");
                String price = finviz_stock_item.getString("price");
                String change = finviz_stock_item.getString("change");
                String volume = finviz_stock_item.getString("volume");
                //temp hash to add to the arraylist
                HashMap<String, String> temp_finviz_map = new HashMap<>();
                temp_finviz_map.put("symbol", symbol);
                temp_finviz_map.put("signal", signal);
                temp_finviz_map.put("price", "$" + price);
                temp_finviz_map.put("change", "$" + change);
                temp_finviz_map.put("volume", "Volume: " + volume);
                //add the temp hashmap to the main arraylist
                dataFeedList_finviz.add(temp_finviz_map);
            }
        }
    }

}
