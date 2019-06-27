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
package de.hybris.platform.cmsfacades.navigations.service.functions;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryCmsItemModelIdConversionFunctionTest
{

	private static final java.lang.String UID = "uid";

	private final DefaultNavigationEntryCmsItemModelIdConversionFunction conversionFunction = new DefaultNavigationEntryCmsItemModelIdConversionFunction();

	@Test
	public void testConversionWithCMSItemModelClass()
	{
		final CMSItemModel itemModel = new CMSItemModel();
		itemModel.setUid(UID);
		final String uid = conversionFunction.apply(itemModel);
		Assert.assertThat(uid, is(UID));
	}

	@Test(expected = ConversionException.class)
	public void testConversionWithInvalidItemModelClass()
	{
		final ItemModel itemModel = new ItemModel();
		conversionFunction.apply(itemModel);
		fail();
	}
}
