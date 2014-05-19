package com.tracer.util;

public class Constants {

  /**
   * Webservice Base URL
   */
  public static final String WEBSERVICE_BASE_URL = "http://192.168.80.20:8080/TraceR_RWS/api/";
  public static final String LOGIN_STATUS = "login_status";
  
  public static final String INVALID_ACCESS_TIME = "login_status";
  public static final String INVALID_CREDENTIALS = "Invalid Credentials";
  public static final String USER_ALREADY_LOGGED_IN = "User already logged in";

  /**
   * LOGIN RESPONSE DATA
   */
  public static final String USERTYPE = "userType";
  public static final String USERNAME = "name";
  public static final String TEAMLEADERCONTACTNUMBER = "tlContactNumber";
  public static final String AUTHCODE = "authCode";

  /**
   * LOGIN INPUT DATA
   */
  public static final String LOGIN_PASSWORD = "password";
  public static final String LOGIN_USERNAME = "userName";

  /**
   * BEAT PLAN DATA
   */
  public static final String DISTRIBUTORS = "distributors";
  public static final String DISTRIBUTORNAME = "distributorName";
  public static final String DISTRIBUTORCODE = "distributorCode";
  public static final String SCHEDULETIME = "scheduleTime";
  public static final String VISITFREQUENCY = "visitFrequency";
  public static final String VISITNUMBER = "visitNo";
  public static final String DISTRIBUTORCONTACTNUMBER = "distributorContactNumber";
  public static final String DISTRIBUTORLATITIUDE = "distributorLattitude";
  public static final String DISTRIBUTORLONGITUDE = "distributorLongitude";

  /**
   * VISIT INFO
   */
  public static final String RUNNER_VISIT_ID = "runnerVisitId";
  public static final String VISITCOUNT = "visitCount";

  /**
   * NEW CAF COLLECTION
   */
  public static final String TOTAL_CAF_COUNT = "total_caf_count";
  public static final String ACCEPTED_CAF_COUNT = "accepted_caf_count";
  public static final String REJECTED_CAF_COUNT = "rejected_caf_count";
  public static final String RETURNED_CAF_COUNT = "returned_caf_count";
  public static final String DISTRIBUTOR_PHOTO_PATH = "distributor_photo_path";
  public static final String DISTRIBUTOR_SIGNATURE_PATH = "dstributor_sign_path";
  public final static int PICTURE_RESULT = 1;
  public final static int BARCODE_SCAN_RESULT = 2;
  public final static int DIGITAL_SIGNATURE_RESULT = 3;
  public final static int RUNNER_PICTURE_RESULT = 4;
  public final static String ISCAFSUBMITTED = "cafSubmitted";

  /**
   * RUNNERS DATA
   */
  public static final String RUNNERS = "runners";
  public static final String CONTACT_NUMBER = "contactNumber";
  public static final String CAFCOUNT = "cafcount";
  public static final String IS_PRESENT = "isPresent";
  public static final String RUNNERCODE = "runnerCode";
  public static final String RUNNERNAME = "runnerName";
}
