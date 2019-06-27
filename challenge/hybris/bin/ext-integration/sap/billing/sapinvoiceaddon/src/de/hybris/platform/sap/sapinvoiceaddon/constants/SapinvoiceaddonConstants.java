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
package de.hybris.platform.sap.sapinvoiceaddon.constants;


/**
 * Global class for all Sapinvoiceaddon constants. You can add global constants for your extension into this class.
 */
public final class SapinvoiceaddonConstants
{
	
	public static class TC
	{
	        private TC()
	        {
	            //private constructor to hide public constructor
	        }
		public static final String MATERIAL = "Material".intern();
		public static final String PARTNERADDRESS = "PartnerAddress".intern();
		public static final String SAPB2BDOCUMENT = "SapB2BDocument".intern();
	}
	
	public static final String EXTENSIONNAME = "sapinvoiceaddon";
	public static final String SAP_INVOICE_BO = "sapInvoiceBO";
	public static final String MYCOMPANY_INVOICE_DETAILS_PAGE = "invoice";
	
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String INVOICE_CODE_PATH_VARIABLE_PATTERN = "{invoiceCode:.*}";
	public static final String REDIRECT_TO_DOCUMENT_LIST_PAGE = REDIRECT_PREFIX
			+ "addon:/accountsummaryaddon/pages/documents";
	public static final String ACCOUNT_STATUS_DOCUMENTS_PATH = "/my-company/organization-management/accountstatus/details?unit=%s";
	public static final String ACCOUNT_STATUS_DOCUMENTS_UNIT_PATH = "/my-company/organization-management/accountsummary-unit/details?unit=%s";
	public static final String TEXT_COMPANY_ACCOUNTSUMMARY = "text.company.accountsummary";
	public static final String TEXT_COMPANY_ACCOUNTSUMMARY_DETAILS = "text.company.accountsummary.details";
	public static final String ACCOUNT_STATUS_PATH = "/my-company/organization-management/accountstatus/";
	public static final String ACCOUNT_STATUS_PATH_UNIT = "/my-company/organization-management/accountsummary-unit/";
	public static final String ACCOUNT_STATUS_UI_VERSION = "commerceservices.default.desktop.ui.experience";
	public static final String ACCOUNT_STATUS_DESKTOP_STRING = "desktop";
	public static final String ACCOUNT_STATUS_RESPONSIVE_STRING = "responsive";
	
	public static final String MY_COMPANY_URL = "/my-company";
	public static final String MY_COMPANY_MESSAGE_KEY = "header.link.company";
	
	public static final String BILLING_ADDRESS_PARTNER_FUCNTION = "sapinvoiceaddon.address.billingaddress.partnerfucntion";
	public static final String SHIPPING_ADDRESS_PARTNER_FUCNTION = "sapinvoiceaddon.address.shippingaddress.partnerfucntion";
	

	
	

	
	public static final String ACCOUNT_INVOICE_DETAILS="text.company.accountsummary.invoice.details";



	private SapinvoiceaddonConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
