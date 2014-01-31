package com.tracer.util;

public class Constants {

	/**
	 * Webservice Base URL
	 */
	public static final String WEBSERVICE_BASE_URL = "http://118.102.131.157:8080/TraceR_WS/";

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

	/**
	 * VISIT INFO
	 */
	public static final String VISITCODE = "visitCode";
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
	public static final String RUNNER_VISIT_ID = "runner_visit_id";
	public final static int PICTURE_RESULT = 1;
	public final static int BARCODE_SCAN_RESULT = 2;
	public final static int DIGITAL_SIGNATURE_RESULT = 3;

	/**
	 * RUNNERS DATA
	 */
	public static final String RUNNERS = "runners";
	public static final String CONTACT_NUMBER = "contactNumber";
	public static final String CAFCOUNT = "CAFCount";
	public static final String IS_PRESENT = "isPresent";
	public static final String RUNNERCODE = "runnerCode";
	public static final String RUNNERNAME = "runnerName";
}
