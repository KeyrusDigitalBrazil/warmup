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
package com.hybris.ymkt.personalization.segment;

import static org.mockito.Matchers.any;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.SegmentMappingData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.common.constants.SapymktcommonConstants;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.segmentation.dto.SAPInitiative;
import com.hybris.ymkt.segmentation.services.InitiativeService;


/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class InitiativeUserSegmentsProviderTest
{

	private static final SegmentMappingData data = new SegmentMappingData();
	private static final SAPInitiative initiative1 = new SAPInitiative();
	private static List<SAPInitiative> initiativeList = new ArrayList<>();
	private static final String PREFIX = "prefix-";
	private static final InitiativeUserSegmentsProvider provider = new InitiativeUserSegmentsProvider();

	@Mock
	private CustomerModel customer;

	@Mock
	private InitiativeService initiativeService;

	@Mock
	private SessionService sessionService;

	@Mock
	private UserContextService userContextService;

	@Mock
	private UserModel userModel;

	@Mock
	private UserService userService;

	@Test
	public void getUserSegment_AnonymousUserTest() throws IOException
	{
		Mockito.when(userService.isAnonymousUser(any())).thenReturn(true);
		Mockito.when(sessionService.getAttribute(SapymktcommonConstants.PERSONALIZATION_PIWIK_ID_SESSION_KEY))
				.thenReturn("YMKT_PIWIK_ID");
		Mockito.when(initiativeService.getInitiatives(any())).thenReturn(initiativeList);

		userModel = userService.getCurrentUser();

		final List<SegmentMappingData> dataList = new ArrayList<>();
		final SegmentMappingData data = new SegmentMappingData();
		data.setCode(PREFIX + initiativeList.get(0).getId());
		data.setAffinity(BigDecimal.ONE);
		dataList.add(data);

		Assert.assertEquals(dataList.get(0).getCode(), provider.getUserSegments(customer).get(0).getCode());
		Assert.assertEquals(dataList.get(0).getAffinity(), provider.getUserSegments(customer).get(0).getAffinity());
	}

	@Test
	public void getUserSegment_ThrowExceptionReturnNullTest() throws IOException
	{
		Mockito.when(initiativeService.getInitiatives(any())).thenThrow(new IOException());
		Assert.assertEquals(null, provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegment_userIsCustomerTest() throws IOException
	{
		Mockito.when(initiativeService.getInitiatives(any())).thenReturn(initiativeList);

		userModel = userService.getCurrentUser();

		final List<SegmentMappingData> dataList = new ArrayList<>();
		final SegmentMappingData data = new SegmentMappingData();
		data.setCode(PREFIX + initiativeList.get(0).getId());
		data.setAffinity(BigDecimal.ONE);
		dataList.add(data);


		Assert.assertEquals(dataList.get(0).getCode(), provider.getUserSegments(customer).get(0).getCode());
		Assert.assertEquals(dataList.get(0).getAffinity(), provider.getUserSegments(customer).get(0).getAffinity());
	}

	@Test
	public void getUserSegment_UserIsNotCustomerTest() throws IOException
	{
		Mockito.when(initiativeService.getInitiatives(any())).thenReturn(initiativeList);
		Assert.assertEquals(Collections.emptyList(), provider.getUserSegments(userModel));

	}

	@Test
	public void getUserSegments_campaignDisabledTest()
	{
		provider.setCampaignEnabled(false);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(false);
		Assert.assertNull(provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegments_incognitoUserTrue()
	{
		provider.setCampaignEnabled(true);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(true);
		Assert.assertNull(provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegments_returnEmptyCollections()
	{
		provider.setCampaignEnabled(true);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(false);
		Assert.assertEquals(Collections.emptyList(), provider.getUserSegments(customer));
	}

	@Test
	public void segmentationDataConvertTest()
	{
		final SegmentMappingData data = provider.convert(initiative1);
		Assert.assertEquals(PREFIX + initiative1.getId(), data.getCode());
		Assert.assertEquals(BigDecimal.ONE, data.getAffinity());
	}

	@Before
	public void setUp() throws Exception
	{
		data.setAffinity(BigDecimal.ONE);
		data.setCode("dataCode");
		provider.setSegmentPrefix(PREFIX);

		provider.setInitiativeService(initiativeService);
		provider.setSessionService(sessionService);
		provider.setUserContextService(userContextService);
		provider.setUserService(userService);
		provider.setCampaignEnabled(true);


		initiative1.setId("initiativeId");
		initiative1.setMemberCount("1");
		initiative1.setName("initiativeName");

		initiativeList.add(initiative1);


		Mockito.when(userContextService.isIncognitoUser()).thenReturn(false);
		Mockito.when(userContextService.getUserOrigin()).thenReturn("userOriginId");
		Mockito.when(userService.isAnonymousUser(any())).thenReturn(false);
		Mockito.when(customer.getCustomerID()).thenReturn("bobbyTestCustomer@hybris.com");
	}

	@After
	public void tearDown() throws Exception
	{

	}


}
