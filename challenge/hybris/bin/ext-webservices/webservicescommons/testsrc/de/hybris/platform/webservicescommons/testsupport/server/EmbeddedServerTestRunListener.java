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
package de.hybris.platform.webservicescommons.testsupport.server;

import de.hybris.platform.testframework.runlistener.CustomRunListener;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.springframework.beans.factory.annotation.Required;


public class EmbeddedServerTestRunListener extends CustomRunListener
{
	private EmbeddedServerController embeddedServerController;

	@Override
	public void testRunStarted(final Description description) throws Exception
	{
		final NeedsEmbeddedServer nes = description.getAnnotation(NeedsEmbeddedServer.class);
		if (nes == null)
		{
			return;
		}

		embeddedServerController.start(nes.webExtensions());
	}

	@Override
	public void testRunFinished(final Result result) throws Exception
	{
		embeddedServerController.stop();
	}

	public EmbeddedServerController getEmbeddedServerController()
	{
		return embeddedServerController;
	}

	@Required
	public void setEmbeddedServerController(final EmbeddedServerController embeddedServerController)
	{
		this.embeddedServerController = embeddedServerController;
	}
}
