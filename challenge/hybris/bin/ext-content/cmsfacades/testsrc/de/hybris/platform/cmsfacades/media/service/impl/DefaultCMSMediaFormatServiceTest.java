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
package de.hybris.platform.cmsfacades.media.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSImageComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSMediaFormatDao;
import de.hybris.platform.core.model.media.MediaFormatModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCMSMediaFormatServiceTest
{
	@InjectMocks
	private DefaultCMSMediaFormatService cmsMediaFormatService;
	@Mock
	private Map<Class<? extends AbstractCMSComponentModel>, Collection<String>> cmsComponentMediaFormats;
	@Mock
	private CMSMediaFormatDao cmsMediaFormatDao;
	@Mock
	private MediaFormatModel desktopFormat;
	@Mock
	private MediaFormatModel mobileFormat;

	@Before
	public void setUp()
	{
		when(cmsComponentMediaFormats.containsKey(CMSImageComponentModel.class)).thenReturn(Boolean.TRUE);
		when(cmsComponentMediaFormats.get(CMSImageComponentModel.class)).thenReturn(Arrays.asList("desktop", "mobile"));
		when(cmsComponentMediaFormats.get(CMSLinkComponentModel.class)).thenReturn(Collections.emptyList());
		when(cmsMediaFormatDao.getMediaFormatsByQualifiers(any())).thenReturn(Arrays.asList(desktopFormat, mobileFormat));
	}

	@Test
	public void shouldFindMediaFormats() throws CMSItemNotFoundException
	{
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatService
				.getMediaFormatsByComponentType(CMSImageComponentModel.class);
		Assert.assertEquals(2, mediaFormats.size());
	}

	@Test
	public void shouldFailComponentTypeUndefined()
	{
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatService
				.getMediaFormatsByComponentType(CMSLinkComponentModel.class);
		Assert.assertTrue(mediaFormats.isEmpty());
	}

}
