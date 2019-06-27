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
package de.hybris.platform.sap.sapinvoiceaddon.aspect;


import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 * This aspect is used to apply the UI changes when switching the View of Account Summary order management AOM.
 *
 */
public class SapInvoiceViewSwitchUIComponentsAspect
{

	public static final Logger LOG = Logger.getLogger(SapInvoiceViewSwitchUIComponentsAspect.class);
	public static final String INVOICE_ADDON_PREFIX = "addon:/sapinvoiceaddon/";
	private static final String sapinvoiceaddon = "/sapinvoiceaddon/";


	/**
	 * Switch accountsummary page
	 *
	 * @param pjp
	 * @return UI Component
	 * @throws Throwable
	 */
	public Object switchInvoicePage(final ProceedingJoinPoint pjp) throws Throwable
	{
		final String uiComponent = applyInvoiceUIChanges(pjp).toString();
		return uiComponent.replace("accountLayoutPage", "accountInvoiceLayoutPage");
	}


	//add addon prefix
	public Object applyInvoiceUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();

			final StringBuilder prefix = new StringBuilder(INVOICE_ADDON_PREFIX);
			prefix.append(uiComponent);
			uiComponent = prefix.toString();
			logInfoMessage(pjp.getSignature().toString(), uiComponent, null, true);
			return uiComponent;

	}


	/**
	 * Log an information message
	 *
	 * @param methodSignature
	 * @param uiComponent
	 * @param sapInvoiceEnabled
	 */
	private void logInfoMessage(final String methodSignature, final String uiComponent, final String newUiComponent,
			final boolean sapInvoiceEnabled)
	{
		if (LOG.isInfoEnabled())
		{
			if (sapInvoiceEnabled)
			{
				LOG.info("For document view, switching from AccountSummaryAddon to SapInvoiceAddon");
			}
			else
			{
				LOG.info("Document view is from AccountSummaryAddon");
			}
		}
	}

	

}
