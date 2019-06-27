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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryMediaModelIdConversionFunctionTest
{

	private static final String MEDIA_CODE = "media-code";
	private final DefaultNavigationEntryMediaModelIdConversionFunction conversionFunction = new DefaultNavigationEntryMediaModelIdConversionFunction();


	@Test
	public void testConverValidMediaModel()
	{
		final MediaModel itemModel = new MediaModel();
		itemModel.setCode(MEDIA_CODE);
		final String mediaCode = conversionFunction.apply(itemModel);
		assertThat(mediaCode, is(MEDIA_CODE));
	}

	@Test(expected = ConversionException.class)
	public void testConvertInvalidMedia()
	{
		final ItemModel itemModel = new ItemModel();
		conversionFunction.apply(itemModel);
		fail();
	}

}
