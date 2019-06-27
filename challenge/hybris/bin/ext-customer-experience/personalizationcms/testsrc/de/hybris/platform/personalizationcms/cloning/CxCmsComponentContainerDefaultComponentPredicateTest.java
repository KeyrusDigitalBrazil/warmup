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
/**
 *
 */
package de.hybris.platform.personalizationcms.cloning;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;

import org.junit.Assert;
import org.junit.Test;



@UnitTest
public class CxCmsComponentContainerDefaultComponentPredicateTest
{

	private final CxCmsComponentContainerDefaultComponentPredicate predicate = new CxCmsComponentContainerDefaultComponentPredicate();

	@Test
	public void shouldReturnTrue()
	{
		Assert.assertTrue(predicate.test(new CxCmsComponentContainerModel(), CxCmsComponentContainerModel.DEFAULTCMSCOMPONENT));
	}

	@Test
	public void shouldReturnFalseForWrongAttribute()
	{
		Assert.assertFalse(predicate.test(new CxCmsComponentContainerModel(), CxCmsComponentContainerModel.UID));
	}

	@Test
	public void shouldReturnFalseForWrongType()
	{
		Assert.assertFalse(predicate.test(new SimpleCMSComponentModel(), CxCmsComponentContainerModel.DEFAULTCMSCOMPONENT));
	}

}
