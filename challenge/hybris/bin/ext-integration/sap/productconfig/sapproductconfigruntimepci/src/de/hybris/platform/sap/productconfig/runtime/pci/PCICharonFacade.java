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
package de.hybris.platform.sap.productconfig.runtime.pci;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;


/**
 * Facilitates charon calls to Product Configuration Intelligence (PCI) analytics REST service
 */
public interface PCICharonFacade
{
	/**
	 * Creates analytics document containing e.g. popularity information for possible values
	 *
	 * @param analyticsDocumentInput
	 *           input document
	 * @return analytics document
	 */
	AnalyticsDocument createAnalyticsDocument(AnalyticsDocument analyticsDocumentInput);

}
