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
package de.hybris.platform.cms2.cloning.service.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import org.junit.Test;


@IntegrationTest
public class CMSCopyContextTest extends ServicelayerBaseTest
{
	private final CMSCopyContext copyContext = new CMSCopyContext();

	@Test
	public void shouldNotSkipAttributeNonItemModel()
	{
		final boolean value = copyContext.skipAttribute(new Object(), "test-qualifier");

		assertThat(value, is(false));
	}

	@Test
	public void shouldNotSkipAttributeNonRelationDescriptor()
	{
		final boolean value = copyContext.skipAttribute(new CMSLinkComponentModel(), CMSLinkComponentModel.URL);

		assertThat(value, is(false));
	}

	@Test
	public void shouldNotSkipAttributeRelationDescriptor()
	{
		final boolean value = copyContext.skipAttribute(new CMSLinkComponentModel(), CMSLinkComponentModel.NAVIGATIONNODES);

		assertThat(value, is(false));
	}

}
