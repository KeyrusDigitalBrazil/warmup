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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

/**
 * Defines the partner functions.
 * 
 * The partner functions could used in the sales document
 * to integrate the business partner in their different functions.
 * 
 */
@SuppressWarnings("squid:S1214")
public interface PartnerFunctionData {

	/** 
	 * constant for the partner function soldto
	 */
    String SOLDTO    = "SOLDTO";

    /** 
     * constant for the partner function payer
     */
    String PAYER    = "PAYER";

    /** 
     * constant for the partner function billto
     */
    String BILLTO    = "BILLTO";

    /** 
     * constant for the partner function shipto.<br>
     * <b>Note that the ship to isn't support as partner function in Java Layer </b>
     */
    String SHIPTO    = "SHIPTO";


	/** 
	 * constant for the partner function soldfrom
	 */
    String SOLDFROM  = "SOLDFROM";

	/** 
	 * constant for the partner function contact
	 */
    String CONTACT   = "CONTACT";

	/** 
	 * constant for the partner function reseller
	 */
    String RESELLER  = "RESELLER";

	/** 
	 * constant for the partner function sales prospect
	 */
    String SALES_PROSPECT  = "SALES_PROSPECT";
    
	/** 
	 * Constant for the partner function agent. An agent is an employee
	 * that is not related to a business partner. Used for BOB scenarios.
	 */
    String AGENT  = "AGENT";

	/** 
	 * Constant for the partner function responsible at partner. 
	 */
    String RESP_AT_PARTNER  = "RESP_AT_PARTNER";

	/** 
	 * Constant for the partner function end customer. 
	 */
    String END_CUSTOMER  = "END_CUSTOMER";

    
    /** 
     * constant for the partner function ship from. <br>
     */
    String SHIPFROM    = "SHIPFROM";


	/** 
	 * constant for the partner function vendor
	 */
	String VENDOR    = "VENDOR";

	/**
	 * 
	 * constant for the partner function responsible employee
	 */
	String RESP_EMPLOYEE = "RESP_EMPLOYEE";
	
    /**
     * Returns the name of the partner function
     * @return name of partner function
     */
    String getName();

}