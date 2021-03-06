package com.lydanny.personalnewsfeed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 *----------------------------------------------|
 *  Project Name: Personal News Feed            |
 *  File Name: FingerPrintHandler.java          |
 *  AUTHOR: Danny Ly | RedKlouds                |
 *  Created On: 5/19/2017                       |
 *----------------------------------------------|
 *
 * Class Description:
 *  -> This class is called to initialize the presentation of the processes for fingerprint
        authentication, during the time of fingerprint Authentication, there will be many
        call backs from the fingerprint manager class, therefore we must handle them here.
 *
 * Assumptions:
 *  -> Prior to calling or making this object, CryptoKey has been created by
 *  AuthenticationActivity.
 **/

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback{
    //cancellationSignal Object used to get callbacks from user action
    private CancellationSignal cancallationSignal;
    private Context appContext;

    /**
     * Function Default Constructor
     * Description: called to setup the current app context
     * @param context
     */
    public FingerPrintHandler(Context context){
        appContext = context;
    }

    /**
     * Function: FingerPrintHandler.startAuth
     * Description:
     *  ->initializes the fingerprint authentication process
     *  -> checks one more time if access has been granted for fingerprint access
     * PRECONDITIONS:
     *  ->Valid cryptoObject freated from AuthenticationActivity class
     *  -> AuthenticatiopnAcitivty class was called prior to calling this function
     *  and was successful on initalizing the following parameters
     *    =fManager (fingerprint manager)
     *    =cryptoOject (CryptoObject.CryptoObject)
     * @param fManager
     * @param cryptoObject
     * POSTCONDITION:
     *  -> calls authenticate from the fingerprint manager object given the cryptoObject
     *  and checks authentication, then calls the below implemented callbacks
     */
    public void startAuth(FingerprintManager fManager,
                          FingerprintManager.CryptoObject cryptoObject){
        cancallationSignal = new CancellationSignal();

        if(ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED){
            //no permissions grated for finger print
            return;
        }
        //otherwise continue, fingerprint has been granted
        fManager.authenticate(cryptoObject, cancallationSignal, 0, this, null);
    }

    /**
     * Function: FingerPrintHandler.onAuthenticationError
     * Description:
     *  -> when authentication has error'd
     *  PRECONDITION:
     * @param helpMsgId
     * @param errorString
     * POSTCONDITION:
     *  ->None
     */
    @Override
    public void onAuthenticationError(int helpMsgId, CharSequence errorString){
        Toast.makeText(appContext,
                "Authentication Error\n" + errorString,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Function: FingerPrintHandler.OnAuthHelp
     * Description: Instance where device supports user assistant on fingerprint scanning
     * -> example 'make sure fingerp is covering entire  scanner' messages
     * PRECONDTION:
     * @param helpMsgId
     * @param helpString
     * POSTCONDIITON:
     *  ->Presents the system OS help scanner message to user
     */
    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        //make a toast to the systems original message
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Function: FingerPrintHandler.onAuthFailed
     * Description:
     *  -> present a message on failed recognition attempt
     * PRECONDITONS:
     * -> None
     * POSTCONDITION:
     * ->None
     */
    @Override
    public void onAuthenticationFailed(){
        Toast.makeText(appContext,
                "Authentication Failed. " ,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Function: FingerPrintHandler.onAuthenticationSucceed
     * Description:
     *  -> authenticaiton has been succeessful therefore we need open to our main activity
     *  PRECONDITIONS:
     * @param AuthResult
     * POSTCONDITION:
     *  ->Handles successful fingerprint recognition, go to main view
     * ASSUMPTIONS:
     *  ->Correct fingerprint recognized
     */
    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult AuthResult) {
        //present success message
        Toast.makeText(appContext,
                "Authentication succeeded.",
                Toast.LENGTH_LONG).show();
        //prepare to make an intent to main view
        Intent toMainActivity = new Intent(appContext, MainActivity.class);
        appContext.startActivity(toMainActivity);
    }

}
