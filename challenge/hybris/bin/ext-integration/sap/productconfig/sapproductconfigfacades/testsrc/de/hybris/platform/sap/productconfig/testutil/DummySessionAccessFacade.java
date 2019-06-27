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
package de.hybris.platform.sap.productconfig.testutil;

import de.hybris.platform.sap.productconfig.facades.impl.SessionAccessFacadeImpl;
import de.hybris.platform.sap.productconfig.services.testutil.DummySessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl.PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl;



public class DummySessionAccessFacade extends SessionAccessFacadeImpl
{

	public DummySessionAccessFacade()
	{
		final DummySessionAccessService sessionAccessService = new DummySessionAccessService();
		super.setSessionAccessService(sessionAccessService);

		final PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl abstractOrderEntryLinkStrategy = new PersistenceConfigurationAbstractOrderEntryLinkStrategyImpl();
		abstractOrderEntryLinkStrategy.setSessionAccessService(sessionAccessService);
		super.setAbstractOrderEntryLinkStrategy(abstractOrderEntryLinkStrategy);
	}
}
