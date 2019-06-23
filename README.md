# HyperpayReactNativeDemo
Hyperpay SDK and reactNative bridge demo


This is Just an Android demo on How to build a ReactNative Birdge for Hyperpay Native SDK

To help you better I've provided all the steps requrid below:

1- First of all if you are using EXPO you need to eject it to get the native code
2- Add our SDK to native code using (xcode, android studio) as the below guide :
https://hyperpay.docs.oppwa.com/tutorials/mobile-sdk/first-integration 

3- in native side you need to use our custom UI guide, since the ui will be in the react-native side.
https://hyperpay.docs.oppwa.com/tutorials/mobile-sdk/custom-ui/integration 

To create native module 
iOS
https://facebook.github.io/react-native/docs/native-modules-ios 

Android:
https://facebook.github.io/react-native/docs/native-modules-android 

P.S: with every update you make on the native code you need to rebuild the project in xcode and android studio

4- in the react-native side, you need to fetch the checkoutID, and ask he user to enter his card details, then send those data to the native-side

In the deom inside the HyperpayModule I've implemented transactionPayment to initiate the transaction 
this should not return anything to react-native once this function finished our SDK will call transactionCompleted function with a transaction object which is contains 
the transactionType if it's SYNC or ASYN in your case all transactions should be ASYNC, also it will contains the transaction status in the case of ASYNC it will be pending
and finally and most important it will contain the redirectionUrl to 3dSecure which you need to redirect the user to it.
those are the most important functions you need to implement, you can handle the payment status and from the reactnative side.

also please make sure to validate the card details the user enters before sending it to our SDK

you can always refer to our SDK API's below for native functions which you can implement if you want.
https://gate2play.docs.oppwa.com/msdk/android-docs/ 

https://gate2play.docs.oppwa.com/msdk/ios-docs/
