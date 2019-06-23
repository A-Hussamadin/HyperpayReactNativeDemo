package com.hyperpayreactnativedemo;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.payment.BrandsValidation;
import com.oppwa.mobile.connect.payment.CheckoutInfo;
import com.oppwa.mobile.connect.payment.ImagesRequest;
import com.oppwa.mobile.connect.payment.card.CardPaymentParams;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.ITransactionListener;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.provider.TransactionType;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;


import javax.annotation.Nonnull;

public class HyperpayModule extends ReactContextBaseJavaModule  implements ITransactionListener {

    private IProviderBinder binder;
    private Context mApplicationContext;
    private Intent bindIntent;


    public HyperpayModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        //this.reactContext = reactContext;
        mApplicationContext = reactContext.getApplicationContext();
        Intent intent = new Intent(mApplicationContext, ConnectService.class);

        mApplicationContext.startService(intent);
        mApplicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        if (serviceConnection != null) {
            // Unbind from the In-app Billing service when we are done
            // Otherwise, the open service connection could cause the device’s performance
            // to degrade
            mApplicationContext.unbindService(serviceConnection);
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return "Hyperpay";
    }


    //SDK Functions

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IProviderBinder) service;
            /* we have a connection to the service */
            try {
                binder.initializeProvider(Connect.ProviderMode.TEST);
            } catch (PaymentException ee) {
                /* error occurred */
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }

    };
        // REact Methods
@ReactMethod
public void sayHI(){
    Toast.makeText(getReactApplicationContext(), "HI", Toast.LENGTH_LONG).show();

}
        @ReactMethod
        public void transactionPayment(ReadableMap options, Promise promise) {
            // promiseModule = promise;

            try {

                boolean isTokenizationEnabled = true;
                CardPaymentParams cardPaymentParams = new CardPaymentParams(options.getString("checkoutID"),
                        options.getString("paymentBrand"), options.getString("cardNumber"), options.getString("holderName"),
                        options.getString("expiryMonth"), options.getString("expiryYear"), options.getString("cvv"));

                cardPaymentParams.setTokenizationEnabled(true);
               // cardPaymentParams.setShopperResultUrl();
                Transaction transaction = null;

                try {

                    transaction = new Transaction(cardPaymentParams);

                    binder.submitTransaction(transaction);
                    binder.addTransactionListener( HyperpayModule.this);
                    promise.resolve(null);
                } catch (PaymentException ee) {
                    promise.reject(null, ee.getMessage());
                }
            } catch (PaymentException e) {
                promise.reject(null, e.getMessage());
            }

        }

    @Override
    public void brandsValidationRequestSucceeded(BrandsValidation brandsValidation) {

    }

    @Override
    public void brandsValidationRequestFailed(PaymentError paymentError) {

    }

    @Override
    public void imagesRequestSucceeded(ImagesRequest imagesRequest) {

    }

    @Override
    public void imagesRequestFailed() {

    }

    @Override
    public void paymentConfigRequestSucceeded(CheckoutInfo checkoutInfo) {

    }

    @Override
    public void paymentConfigRequestFailed(PaymentError paymentError) {

    }

    @Override
    public void transactionCompleted(Transaction transaction) {
        WritableMap data = Arguments.createMap();
        if (transaction.getTransactionType() == TransactionType.ASYNC){
//            Intent viewIntent =
//                    new Intent("android.intent.action.VIEW",
//                            Uri.parse("http://www.stackoverflow.com/"));

            data.putString("redirectUrl", transaction.getRedirectUrl());
            data.putString("status", "pending");
        }else{
            data.putString("status", "complated");
        }
        // shoud add listner



        data.putString("checkoutID", transaction.getPaymentParams().getCheckoutId());

        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("transactionStatus", data);

        // promiseModule.resolve(transaction);

    }

    @Override
    public void transactionFailed(Transaction transaction, PaymentError paymentError) {
        /* add your implementation here */
    }



}
