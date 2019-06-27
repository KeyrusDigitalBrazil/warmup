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
package com.sap.hybris.sapimpeximportadapter.facades;

import java.io.InputStream;


/**
 * Interface for impex import facade.
 * 
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 */
@Deprecated
public interface ImpexImportFacade
{
	/**
	 * Creates and imports Impex from the input stream
	 *
	 * @param inputStream
	 *           - impex payload in input stream format
	 */
	void createAndImportImpexMedia(final InputStream inputStream);
}
