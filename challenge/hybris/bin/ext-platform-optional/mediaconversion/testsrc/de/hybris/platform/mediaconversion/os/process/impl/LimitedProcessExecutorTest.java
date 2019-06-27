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
package de.hybris.platform.mediaconversion.os.process.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaconversion.os.ProcessExecutor;
import de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase;


/**
 * @author pohl
 */
@UnitTest
public class LimitedProcessExecutorTest extends AbstractProcessExecutorTestCase
{

	/**
	 * @see de.hybris.platform.mediaconversion.os.process.AbstractProcessExecutorTestCase#createExecutor()
	 */
	@Override
	protected ProcessExecutor createExecutor() throws Exception
	{
		return new LimitedProcessExecutor(5, new EmbeddedProcessExecutor());
	}

	@Override
	protected int amountOfThreads()
	{
		return 150;
	}
}
