/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cissapdigitalpayment.constants;

/**
 * Global class for all Cissapdigitalpayment constants.You can add global constants for your extension into this class.
 *
 *
 */
public class CisSapDigitalPaymentConstant
{
	public static final String SAP_DIGITAL_PAYMENT_REG_CARD_SESSION_ID = "sap-digital-payment-card-registration-session-id";
	public static final String SAP_DIGITAL_PAYMENT_POLL_REG_CARD_PROCESS_DEF_NAME = "sapdigitalpayment-poll-card-process";


	public static final String BASE_STORE = "base_store";
	public static final String USER = "user";
	public static final String CART = "cart";

	public static final String PAYMENT_PROVIDER = "sap digital payment";


	public static final String POLL_REG_CARD_PENDING_STAT = "PENDING";
	public static final String POLL_REG_CARD_SUCCESS_STAT = "SUCCESS";
	public static final String POLL_REG_CARD_TIMEOUT_STAT = "TIMEOUT";
	public static final String POLL_REG_CARD_CANCELLED_STAT = "CANCELLED";

	public static final String TRANS_RES_SUCCESS_STAT = "SUCCESS";
	public static final String TRANS_RES_FAILURE_STAT = "FAILURE";

	public static final String SAP_DIGITAL_PAYMENT_URL_KEY = "url";
	public static final String SAP_DIGITAL_PAYMENT_OAUTH_URL_KEY = "oauth.url";
	public static final String SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_ID_KEY = "oauth.clientId";
	public static final String SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_SECRET_KEY = "oauth.clientSecret";
	public static final String SAP_DIGITAL_PAYMENT_OAUTH_SCOPE = "oauth.scope";
	public static final String SAP_DIGITAL_PAYMENT_OAUTH_GRANT_TYPE_KEY = "grant_type";

	public static final String SAP_DIGITAL_PAYMENT_COMPANY_CODE_KEY = "CompanyCode";
	public static final String SAP_DIGITAL_PAYMENT_CUSTOMER_COUNTRY_KEY = "CustomerCountry";
	public static final String SAP_DIGITAL_PAYMENT_PAYMENT_METHOD_KEY = "PaymentMethod";
	public static final String SAP_DIGITAL_PAYMENT_CUSTOM_PARAM_KEY = "RoutingCustomParameterValue";

	public static final String SAP_DIGITAL_PAYMENT_RETRIES_KEY = "retries";
	public static final String SAP_DIGITAL_PAYMENT_RETRIES_INTERVAL_KEY = "retriesInterval";
	public static final String SAP_DIGITAL_PAYMENT_TIMEOUT_KEY = "timeout";

	public static final String SAP_DIGITAL_PAYMENT_AUTH_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static final String SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DUMMY_STRING = "dummy";
	public static final String SAP_DIGITAL_PAYMENT_DUMMY_AUTH_TRANS_RES = "01";
	public static final String SAP_DIGITAL_PAYMENT_DUMMY_AUTH_TRANS_RES_DESC = "Successful";
	public static final String SAP_DIGITAL_PAYMENT_DUMMY_AUTH_DESC = "Authorized";




	/**
	 * Private Constructor
	 */
	private CisSapDigitalPaymentConstant()
	{

	}



}
