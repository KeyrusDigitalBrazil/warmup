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
package de.hybris.platform.personalizationcmsweb.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.personalizationcms.model.CxCmsActionModel;
import de.hybris.platform.personalizationcmsweb.data.CxCmsActionData;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class CxCmsActionReversePopulatorTest
{
	private static final String COMPONENT = "component1";
	private static final String CONTAINER = "container1";

	private final CxCmsActionReversePopulator cxCmsActionReversePopulator = new CxCmsActionReversePopulator();


	@Test
	public void shouldPopulate()
	{
		final CxCmsActionData source = new CxCmsActionData();
		source.setContainerId(CONTAINER);
		source.setComponentId(COMPONENT);
		final CxCmsActionModel target = new CxCmsActionModel();
		cxCmsActionReversePopulator.populate(source, target);

		Assert.assertEquals(CONTAINER, target.getContainerId());
		Assert.assertEquals(COMPONENT, target.getComponentId());
	}



}
