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
package de.hybris.platform.notificationfacades.populators;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.notificationfacades.data.SiteMessageData;
import de.hybris.platform.notificationfacades.url.SiteMessageUrlResolver;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class SiteMessagePopulatorTest
{
	private SiteMessagePopulator populator;

	@Mock
	private SiteMessageForCustomerModel source;

	@Mock
	private SiteMessageModel siteMessageModel;

	@Mock
	private Map<NotificationType, SiteMessageUrlResolver> siteMessageUrlResolvers;

	@Mock
	private SiteMessageUrlResolver urlResolver;

	private static final String TITLE = "message title";
	private static final String CONTENT = "message content";
	private static final String LINK = "/test/link";
	private static final String NOTIFICATION_TYPE = "NOTIFICATION";
	private static final String SENT_DATE = "2016/11/11 11:11:11";
	private static final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Before
	public void setUp() throws ParseException
	{
		MockitoAnnotations.initMocks(this);
		populator = new SiteMessagePopulator();
		populator.setSiteMessageUrlResolvers(siteMessageUrlResolvers);
	}

	@Test
	public void testPopulate() throws UnsupportedEncodingException, ParseException
	{
		final SiteMessageData target = new SiteMessageData();
		when(siteMessageModel.getTitle()).thenReturn(TITLE);
		when(siteMessageModel.getContent()).thenReturn(CONTENT);
		when(siteMessageModel.getNotificationType()).thenReturn(NotificationType.valueOf(NOTIFICATION_TYPE));
		when(siteMessageModel.getExternalItem()).thenReturn(new ItemModel());
		when(source.getSentDate()).thenReturn(format.parse(SENT_DATE));
		when(source.getMessage()).thenReturn(siteMessageModel);
		when(siteMessageUrlResolvers.get(siteMessageModel.getNotificationType())).thenReturn(urlResolver);
		when(urlResolver.resolve(siteMessageModel.getExternalItem())).thenReturn(LINK);
		populator.populate(source, target);

		Assert.assertEquals(TITLE, target.getTitle());
		Assert.assertEquals(CONTENT, target.getContent());
		Assert.assertEquals(format.parse(SENT_DATE), target.getSentDate());
		Assert.assertEquals(LINK, target.getLink());
		Assert.assertEquals(NotificationType.valueOf(NOTIFICATION_TYPE), target.getNotificationType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull() {
		final SiteMessageData target = new SiteMessageData();
		populator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		populator.populate(source, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateMessageNull()
	{
		final SiteMessageData target = new SiteMessageData();
		source.setMessage(null);
		populator.populate(source, target);
	}

	@Test
	public void testPopulateMessage_urlResolver_Null()
	{
		when(source.getMessage()).thenReturn(siteMessageModel);
		when(siteMessageUrlResolvers.get(siteMessageModel.getNotificationType())).thenReturn(null);
		final SiteMessageData target = new SiteMessageData();
		populator.populate(source, target);
		Assert.assertTrue(StringUtils.isBlank(target.getLink()));
	}

}
