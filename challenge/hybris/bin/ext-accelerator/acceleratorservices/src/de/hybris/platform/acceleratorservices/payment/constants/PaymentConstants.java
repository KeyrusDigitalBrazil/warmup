/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.acceleratorservices.payment.constants;

/**
 * 
 */
public interface PaymentConstants
{
	interface PaymentProperties // NOSONAR
	{
		String HOP_POST_URL = "hop.post.url";
		String SOP_POST_URL = "sop.post.url";
		String HOP_PCI_STRATEGY_ENABLED = "hop.pci.strategy.enabled";
		String HOP_DEBUG_MODE = "hop.debug.mode";
		String SITE_PCI_STRATEGY="site.pci.strategy";
	}
}
