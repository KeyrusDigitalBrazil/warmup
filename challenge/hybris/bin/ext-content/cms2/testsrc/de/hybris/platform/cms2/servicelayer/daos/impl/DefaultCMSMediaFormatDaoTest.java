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
package de.hybris.platform.cms2.servicelayer.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSMediaFormatDaoTest extends ServicelayerTransactionalTest
{
	private static final String DESKTOP = "desktop";
	private static final String MOBILE = "mobile";
	private static final String TABLET = "tablet";
	private static final String INVALID = "invalid";

	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private ModelService modelService;
	@Resource
	private DefaultCMSMediaFormatDao cmsMediaFormatDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsMediaFormatTestData.csv", "utf-8");
	}

	@Test
	public void shouldFindAllMediaFormats()
	{
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatDao.getAllMediaFormats();
		Assert.assertEquals(3, mediaFormats.size());
	}

	@Test
	public void shouldFindMediaFormatsWithOneInvalidQualifier()
	{
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatDao.getMediaFormatsByQualifiers( //
				Arrays.asList(INVALID, DESKTOP, TABLET));
		Assert.assertEquals(2, mediaFormats.size());
	}

	@Test
	public void shouldFindAllMediaFormatsWithMultipleQualifiers()
	{
		final Collection<MediaFormatModel> mediaFormats = cmsMediaFormatDao.getMediaFormatsByQualifiers( //
				Arrays.asList(DESKTOP, MOBILE, TABLET));
		Assert.assertEquals(3, mediaFormats.size());
	}

	@Test
	public void shouldFindMediaFormat()
	{
		final MediaFormatModel mediaFormat = cmsMediaFormatDao.getMediaFormatByQualifier(TABLET);
		Assert.assertEquals(TABLET, mediaFormat.getQualifier());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldFailFindMediaFormatWithInvalidQualifier()
	{
		cmsMediaFormatDao.getMediaFormatByQualifier(INVALID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailFindMediaFormatWithNullQualifier()
	{
		cmsMediaFormatDao.getMediaFormatByQualifier(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailFindMediaFormatsWithNullQualifiers()
	{
		cmsMediaFormatDao.getMediaFormatsByQualifiers(null);
	}
}
