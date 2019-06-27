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
package de.hybris.platform.addressservices.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseNameWithTitleFormatStrategyTest
{
	private static final String CHINESE_FIRST_NAME = "建中";
	private static final String CHINESE_LAST_NAME = "张";
	private static final String CHINESE_FULLNAME = "张建中";
	private static final String ENGLISH_FIRST_NAME = "John";
	private static final String ENGLISH_LAST_NAME = "Nash";
	private static final String ENGLISH_FULL_NAME = "John Nash";
	private static final String REVERENT_CHINESE_TITLE = "尊敬的";
	private static final String REVERENT_ENGLISH_TITLE = "rev";
	private static final String MR_CHINESE_TITLE = "先生";
	private static final String MR_ENGLISH_TITLE = "mr";
	private static final String CHINESE_ISOCODE = "zh";
	private static final String ENGLISH_ISOCODE = "en";


	@Mock
	private UserService userService;

	private ChineseNameWithTitleFormatStrategy chineseNameWithTitleFormatStrategy;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		chineseNameWithTitleFormatStrategy = new ChineseNameWithTitleFormatStrategy();
		chineseNameWithTitleFormatStrategy.setUserService(userService);
	}

	@Test
	public void testGetFullnameWithTitleForISOCodeWithFirstAndLastNameNull()
	{
		final String firstname = StringUtils.EMPTY;
		final String lastname = StringUtils.EMPTY;
		Assert.assertNull(chineseNameWithTitleFormatStrategy.getFullnameWithTitleForISOCode(firstname, lastname,
				REVERENT_ENGLISH_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testGetFullnameWithCNNameAndCNIsocodeAndReverentTitle()
	{
		final String expectedFullNameWithTitle = REVERENT_CHINESE_TITLE + " " + CHINESE_LAST_NAME + " " + CHINESE_FIRST_NAME;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(CHINESE_ISOCODE);
		titleModel.setName(REVERENT_CHINESE_TITLE, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(CHINESE_FIRST_NAME, CHINESE_LAST_NAME, REVERENT_CHINESE_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testGetFullnameWithTitleForISOCodeWithCNNameAndCNIsocodeAndNonreverentTitle()
	{
		final String expectedFullNameWithTitle = CHINESE_LAST_NAME + " " + CHINESE_FIRST_NAME + " " + MR_CHINESE_TITLE;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(CHINESE_ISOCODE);
		titleModel.setName(REVERENT_CHINESE_TITLE, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(CHINESE_FIRST_NAME, CHINESE_LAST_NAME, MR_CHINESE_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testGetFullnameWithTitleForISOCodeWithEngNameAndCNIsocodeAndReverentTitle()
	{
		final String expectedFullNameWithTitle = REVERENT_CHINESE_TITLE + " " + ENGLISH_FIRST_NAME + " " + ENGLISH_LAST_NAME;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(CHINESE_ISOCODE);
		titleModel.setName(REVERENT_CHINESE_TITLE, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(ENGLISH_FIRST_NAME, ENGLISH_LAST_NAME, REVERENT_CHINESE_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testGetFullnameWithTitleForISOCodeWithEngNameAndEnIsocodeAndNonreverentTitle()
	{
		final String expectedFullNameWithTitle = MR_ENGLISH_TITLE + " " + ENGLISH_FIRST_NAME + " " + ENGLISH_LAST_NAME;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale englishLocale = new Locale(ENGLISH_ISOCODE);
		titleModel.setName(REVERENT_ENGLISH_TITLE, englishLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(ENGLISH_FIRST_NAME, ENGLISH_LAST_NAME, MR_ENGLISH_TITLE, ENGLISH_ISOCODE));
	}

	@Test
	public void testGetFullnameParamWithFullNameAndEnIsoCodeAndReverentTitle()
	{
		final String expectedFullNameWithTitle = MR_ENGLISH_TITLE + " " + ENGLISH_FULL_NAME;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale englishLocale = new Locale(ENGLISH_ISOCODE);
		titleModel.setName(REVERENT_ENGLISH_TITLE, englishLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(ENGLISH_FULL_NAME, MR_ENGLISH_TITLE, ENGLISH_ISOCODE));
	}

	@Test
	public void testGetFullnameParamWithFullNameAndCNIsoCodeAndNonReverentTitle()
	{
		final String expectedFullNameWithTitle = ENGLISH_FULL_NAME + " " + MR_CHINESE_TITLE;
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(CHINESE_ISOCODE);
		titleModel.setName(REVERENT_CHINESE_TITLE, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertEquals(expectedFullNameWithTitle, chineseNameWithTitleFormatStrategy
				.getFullnameWithTitleForISOCode(ENGLISH_FULL_NAME, MR_CHINESE_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testWithChineseName()
	{
		Assert.assertTrue(chineseNameWithTitleFormatStrategy.containsChineseCharacter(CHINESE_FULLNAME));
	}

	@Test
	public void testWithNonChineseName()
	{
		Assert.assertFalse(chineseNameWithTitleFormatStrategy.containsChineseCharacter(ENGLISH_FULL_NAME));
	}

	@Test
	public void testTitleIsReverent()
	{
		final String targetTitleName = REVERENT_CHINESE_TITLE;
		final String isocode = CHINESE_ISOCODE;

		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(isocode);
		titleModel.setName(targetTitleName, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertFalse(chineseNameWithTitleFormatStrategy.isNotReverent(REVERENT_CHINESE_TITLE, CHINESE_ISOCODE));
	}

	@Test
	public void testTitleNotReverent()
	{
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode(REVERENT_ENGLISH_TITLE);

		final Locale chineseLocale = new Locale(CHINESE_ISOCODE);
		titleModel.setName(REVERENT_CHINESE_TITLE, chineseLocale);

		Mockito.when(userService.getTitleForCode(REVERENT_ENGLISH_TITLE)).thenReturn(titleModel);
		Assert.assertTrue(chineseNameWithTitleFormatStrategy.isNotReverent(MR_CHINESE_TITLE, CHINESE_ISOCODE));
	}

}
