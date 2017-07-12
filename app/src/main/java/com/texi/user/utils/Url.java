package com.texi.user.utils;

/**
 * Created by techintegrity on 04/07/16.
 */
public class Url {

    public static String baseUrl = "http://your_ip_address/web_service/";
    public static String signupUrl =baseUrl+"sign_up";
    public static String mobileCheckUrl =baseUrl+"chk_before_signup";
    public static String mobileExitCheckUrl =baseUrl+"chk_exist_mobile";
    public static String mobileUpdateUrl =baseUrl+"update_mobile";
    public static String loginUrl =baseUrl+"login";
    public static String forgotPasswordUrl =baseUrl+"forgot_password";
    public static String facebookLoginUrl =baseUrl+"facebook_login";
    public static String twitterLoginUrl =baseUrl+"twitter_login";
    public static String bookCabUrl = baseUrl+"book_cab";
    public static String loadTripsUrl = baseUrl+"load_trips";
    public static String loadTripsFiltersUrl = baseUrl+"filter_book";
    public static String profileUrl = baseUrl+"profile_edit";
    public static String changePasswordUrl = baseUrl+"change_password";
    public static String deleteCabUrl = baseUrl+"user_reject_trip";
    public static String FixAreaUrl = baseUrl+"fix_area_list";
    public static String PaymentComUrl = baseUrl+"payment_completed";

    public static String userImageUrl = "http://your_ip_address/user_image/";
    public static String carImageUrl = "http://your_ip_address/car_image/";
    public static String DriverImageUrl = "http://your_ip_address/driverimages/";

    public static String socketUrl = "http://your_ip_address:4040";

    public static String subscribeUrl = "http://your_ip_address:8001/subscribe";
    public static String unsubscribeUrl = "http://your_ip_address:8001/unsubscribe";

    public static String FacebookImgUrl = "https://graph.facebook.com/";

    public static String SocialShareUrl = "http://your_ip_address/web_service/share-page";
    public static String AppLogUrl = "http://your_ip_address/application/views/img/app-logo-new.png";

    public static String VarificatinStart = "https://api.authy.com/protected/json/phones/verification/start";
    public static String CheckTwilioSms = "https://api.authy.com/protected/json/phones/verification/check";

    public static String BrainTree_ClientToken="http://your_ip_address:3002/client_token";
    public static String BrainTree_Checkout="http://your_ip_address:3002/checkout";
}
