package com.lydanny.personalnewsfeed;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *----------------------------------------------|
 *  Project Name: Personal News Feed            |
 *  File Name: AuthenticationActivity.java      |
 *  AUTHOR: Danny Ly | RedKlouds                |
 *  Created On: 5/19/2017                       |
 *----------------------------------------------|
 *
 * Class Description:
 *  -> This class is called to initialize the presentation of the processes for
 *  fingerprint authentication, during the time of fingerprint Authentication,
 *  there will be many call backs from the fingerprint manager class, therefore
 *  be must handle them here.
 *
 * Assumptions:
 *  -> Prior to calling or making this object, CryptoKey has been created by
 *  AuthenticationActivity.
 **/

//requirments need device to register at least a pin,pattern or password
public class AuthenticationActivity extends AppCompatActivity {
    //key name to be stored within the android keystore container
    private static final String KEY_NAME = "Redklouds_Key";
    //Fingerprint Authentication uses two system services
    private FingerprintManager fingerprintManager;
    // KeyguardManager and FingerprintManager
    private KeyguardManager keyguardManager;
    // Keystore access
    private KeyStore _keyStore;
    // Keygenerator object
    private KeyGenerator _keyGenerator;
    // Cipher instance used in fingerprint authentication process
    private Cipher _cipher;
    // The Crypto Object to use as authentication encrypt/decrypt fingerprint
    private FingerprintManager.CryptoObject _cryptoObject;


    /*  Function:
        Description:
        PRECONDITION:
        POSTCONDITION:
        ASSUMPTIONS:
     */
    /*
        Function:
        Description:
        PRECONDITION:
        POSTCONDITION:
        ASSUMPTIONS:
     */
    /**
        Function: onCreate
        Description: called when this activity is called upon(like main for individual files)
            -> initializes the view for this class and instantiates objects.
            -> error checking if device qualifies for fingerprint authentication
        PRECONDITION:
            -> None
        POSTCONDITION:
            -> initialization of the following:
                fingerprintManager
                KeyguardManger
                Cipher
                keystore
                keygenerator
                cryptoCipher
        ASSUMPTIONS:
            -> User has enrolled in at least 1 fingerprint within the system manually
            -> user has a keyguard, password/pin/pattern(backup)
     **/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //present the layout when this class is called.
        setContentView(R.layout.activity_authentication);
        //instantiate the keyguard manager and ask for keyguard service
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        //instantiate the fingerprint manager ask for the fingerprint service
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        /*
        //checks wether the device is secured with a PIN,Pattern, and/or Password
        //ensure backup screen unlocking method has been configured
        //this mainly checks for backup security on device has been setup
        */
        if(!keyguardManager.isKeyguardSecure()){
            //show user an error,
            Toast.makeText(this,
                    "Lock screen security is not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //Check to ensure permissions to use fingerprint is granted
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            //display the error in form of toast
            Toast.makeText(this,
                    "Fingerprint Authentication permission not enabled",
                    Toast.LENGTH_LONG).show();
            return;
        }

        //finally need to ensure at least one fingerprint has been enabled/registered
        if(!fingerprintManager.hasEnrolledFingerprints() ){
            //Present an error since no fingerprints have been enrolled within device
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }
        //call to generate the key
        generateKey();
        //if the cipher is successful then make a CryptoObject
        //if the cipher has been initialized then we also make a FingerPrintHandler
        //object and begin the fingerprint authentication process
        if( cipherInit()){
            //given our generated _cipher make a crypto object from it
            _cryptoObject = new FingerprintManager.CryptoObject(_cipher);
            //create the instance of FingerprintHandler here
            // only parameter is a context, in which THIS context is used.
            FingerPrintHandler helper = new FingerPrintHandler(this);
            //call the method startAuth to begin the authentication process
            helper.startAuth(fingerprintManager,_cryptoObject);
        }

    }


    /**
    Function getKeyStore
    Description:
        -> Inorder to get a generation of the encrypted key we need to gain access to
        the Android keystore system, this method gets access to that store
    PRECONDITION:
        -> None
    POSTCONDITION:
        -> Assigns the keystore variable with an object which gains access to a keystore
        container
     ASSUMPTIONS:
        -> None
     */
    private void getKeyStore(){
        try{
            //a reference to the keystore is obtained by calling the singleton of KeyStore
            //Object.
            _keyStore = KeyStore.getInstance("AndroidKeyStore");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
    Function GenerateKey
    Description: This method initializes and get's the keygenerator
    given the name of keystore container(AndroidKeyStore) and type of key to be generated
    (AES)
    POSTCONDITION:
        -> _keystore must be initialized from generateKey
     POSTCONDITION:
         ->_keygenerator is initialized with encrypted key
     ASSUMPTIONS:
        ->None
     */
    private void getKeyGenerator(){
        try{
            _keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        }catch (NoSuchAlgorithmException |NoSuchProviderException e){
            throw new RuntimeException("Failed to get keygenerator instance", e);
        }
    }
    /**
    Function GenerateKey
    Description:
        -> Generate the key from the keystore given the specific configurations
     PRECONDITION:
        -> geyKetStore successfully called
        -> getKeyGenerator successfully called
     POSTCONDITION:
        -> key is generated and stored with in the keystore container
     ASSUMPTIONS:
        -> None
     */
    protected void generateKey(){
        //get references to the keystore and the keygenerator
        this.getKeyStore();
        this.getKeyGenerator();
        //since we successfully have these references we can generate the key

        try {
            //empty out the keystore
            _keyStore.load(null);
            _keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                                    KeyProperties.PURPOSE_ENCRYPT|
                                    KeyProperties.PURPOSE_DECRYPT)
                                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                    .setUserAuthenticationRequired(true)
                                    .setEncryptionPaddings(
                                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                            .build());
            //generate an PKCS7 Key given the above specifications.
            //the builder specific the key can be encrypted and decrypted
            //authentication required, to ensure user auth. every use of the key
            _keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException |
                CertificateException |
                IOException e){
            //if any of these exceptions are catch gracefully exit;
            throw new RuntimeException(e);
        }
    }

    /**
    Function ciperInit
    Description:
        -> initializes a cipher that will create a encrypted FingerprintManger.CryptoObject
        instance.This object is used for fingerprint authentication process
        -> Cipher Config. involves obtaining a Cipher instance and initializing it with
        the the keystored in the keystore container
     PRECONDITION:
        -> _Keystore has been initialized by getKeystore()
     POSTCONDITION:
        -> Key is generated
        -> Cipher is initialized
     ASSUMPTIONS:
        -> generateKey was called to initialize _KeyStore

 */
    public boolean cipherInit(){
        try {
            //get the singleton of the Cipher class
            //generate a Cipher using the specific configurations
            _cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                    NoSuchPaddingException e){
                throw new RuntimeException("Failed to get Cipher", e);
            }

        try{
            //empty out the keystore container
            _keyStore.load(null);
            SecretKey key = (SecretKey) _keyStore.getKey(KEY_NAME,
                                                        null);
            _cipher.init(Cipher.ENCRYPT_MODE, key);
            //return true, successfully made key
            return true;
        }catch (KeyPermanentlyInvalidatedException e){
            return false;
        }catch (KeyStoreException |
                CertificateException |
                UnrecoverableEntryException |
                IOException |
                NoSuchAlgorithmException |
                InvalidKeyException e){
            throw new RuntimeException("Failed to initalize Cipher ", e);

        }
    }

}
