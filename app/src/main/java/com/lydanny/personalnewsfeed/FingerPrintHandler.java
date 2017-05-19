package com.lydanny.personalnewsfeed;

import android.Manifest;
import android.content.Context;
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
     * Description:
     *  ->initializes the fingerprint authentication process
     *  -> checks one more time if access has been granted for fingerprint access
     * PRECONDITIONS:
     * @param fManager fingerprint objectmanager
     * @param cryptoObject the cryptoObject created from AuthenticationActivity
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
     * Description:
     *  -> when authentication has error'd
     * @param helpMsgId
     * @param errorString
     */
    @Override
    public void onAuthenticationError(int helpMsgId, CharSequence errorString){
        Toast.makeText(appContext,
                "Authentication Error\n" + errorString,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Function: OnAuthHelp
     * Description: When user presented  with help
     * @param helpMsgId
     * @param helpString
     */
    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString){
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    /**
     * Function: onAuthFailed
     * Description: present a failer message, and number of attempers before exiting.
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
     * Function:
     * Description:
     *  -> authenticaiton has been succeessful therefore we need open to our main activity
     *  PRECONDITIONS:
     * @param AuthResult
     * POSTCONDITION:
     *  ->go to main activity
     */
    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult AuthResult) {
        Toast.makeText(appContext,
                "Authentication succeeded.",
                Toast.LENGTH_LONG).show();
    }

}
