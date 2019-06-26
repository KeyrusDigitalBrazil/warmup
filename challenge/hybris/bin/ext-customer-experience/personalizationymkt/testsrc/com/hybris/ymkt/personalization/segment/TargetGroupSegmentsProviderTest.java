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
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.personalization.services.TargetGroupService;


/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class TargetGroupSegmentsProviderTest
{

	private static final String PREFIX = "prefix-";
	private static final TargetGroupSegmentsProvider provider = new TargetGroupSegmentsProvider();
	private static List<String> TGIds = new ArrayList<>();
	private static List<UUID> UUIDList = new ArrayList<>();

	@Mock
	private CustomerModel customer;

	final SegmentMappingData data = new SegmentMappingData();
	@Mock
	TargetGroupService targetGroupService;
	final UserModel user = new UserModel();
	@Mock
	private UserContextService userContextService;
	@Mock
	private UserModel userModel;



	@Mock
	private UserService userService;


	@Test
	public void getUserSegment_ThrowExceptionReturnNullTest() throws IOException
	{
		Mockito.when(targetGroupService.getCustomerTargetGroupIds(any())).thenThrow(new IOException());
		Assert.assertEquals(null, provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegment_ThrowExceptionReturnNullTest2() throws IOException
	{
		Mockito.when(targetGroupService.getCustomerTargetGroupsGUIDs(any(), any())).thenThrow(new IOException());
		Assert.assertEquals(null, provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegment_UserIsCustomerTest() throws IOException
	{
		Mockito.when(targetGroupService.getCustomerTargetGroupsGUIDs(any(), any())).thenReturn(UUIDList);
		Mockito.when(targetGroupService.getCustomerTargetGroupIds(UUIDList)).thenReturn(TGIds);

		final List<SegmentMappingData> dataList = new ArrayList<>();
		final SegmentMappingData data = new SegmentMappingData();
		data.setCode(PREFIX + TGIds.get(0));
		data.setAffinity(BigDecimal.ONE);
		dataList.add(data);

		Assert.assertEquals(dataList.get(0).getCode(), provider.getUserSegments(customer).get(0).getCode());
		Assert.assertEquals(dataList.get(0).getAffinity(), provider.getUserSegments(customer).get(0).getAffinity());
	}

	@Test
	public void getUserSegments_incognitoTrue()
	{
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(true);
		Assert.assertNull(provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegments_NotCustomerThenReturnEmptyCollections()
	{
		Assert.assertEquals(Collections.emptyList(), provider.getUserSegments(userModel));
	}

	@Test
	public void getUserSegments_targetGroupDisabledTest()
	{
		provider.setTargetGroupEnabled(false);
		Assert.assertNull(provider.getUserSegments(customer));
	}

	@Test
	public void getUserSegments_userIsAnonymous()
	{
		Mockito.when(userService.isAnonymousUser(customer)).thenReturn(true);
		Assert.assertNull(provider.getUserSegments(customer));
	}

	@Test
	public void segmentationDataConvertTest()
	{
		final SegmentMappingData data = provider.convert("dataCode");
		Assert.assertEquals(PREFIX + "dataCode", data.getCode());
		Assert.assertEquals(BigDecimal.ONE, data.getAffinity());
	}

	@Before
	public void setUp() throws Exception
	{

		provider.setSegmentPrefix(PREFIX);
		provider.setUserService(userService);
		provider.setUserContextService(userContextService);
		provider.setTargetGroupService(targetGroupService);
		provider.setTargetGroupEnabled(true);

		data.setAffinity(BigDecimal.ONE);
		data.setCode("dataCode");

		UUIDList.add(UUID.randomUUID());
		UUIDList.add(UUID.randomUUID());
		UUIDList.add(UUID.randomUUID());

		TGIds.add("id1");
		TGIds.add("id2");
		TGIds.add("id3");

		Mockito.when(userContextService.getUserOrigin()).thenReturn("userOriginId");
		Mockito.when(userService.isAnonymousUser(any())).thenReturn(false);
		Mockito.when(userContextService.isIncognitoUser()).thenReturn(false);
		Mockito.when(customer.getCustomerID()).thenReturn("bobbyTestCustomer@hybris.com");
	}


}
