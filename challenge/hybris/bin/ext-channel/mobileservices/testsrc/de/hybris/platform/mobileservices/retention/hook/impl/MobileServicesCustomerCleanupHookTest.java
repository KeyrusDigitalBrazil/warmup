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
package de.hybris.platform.mobileservices.retention.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.mobileservices.model.text.UserPhoneNumberModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class MobileServicesCustomerCleanupHookTest
{
	@InjectMocks
	private final MobileServicesCustomerCleanupHook customerCleanupHook = new MobileServicesCustomerCleanupHook();

	@Mock
	private ModelService modelService;
	@Mock
	private WriteAuditRecordsDAO writeAuditRecordsDAO;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCleanupRelatedObjects()
	{
		final CustomerModel orderModel = mock(CustomerModel.class);
		final UserPhoneNumberModel userPhoneNumberModel = mock(UserPhoneNumberModel.class);
		final List<UserPhoneNumberModel> userPhoneNumbers = Collections.singletonList(userPhoneNumberModel);
		given(orderModel.getPhoneNumbers()).willReturn(userPhoneNumbers);
		final PK userPhoneNumberPK = PK.parse("1111");
		given(userPhoneNumberModel.getPk()).willReturn(userPhoneNumberPK);

		customerCleanupHook.cleanupRelatedObjects(orderModel);
		verify(modelService).remove(userPhoneNumberModel);
		verify(writeAuditRecordsDAO).removeAuditRecordsForType(UserPhoneNumberModel._TYPECODE, userPhoneNumberPK);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCleanupRelatedObjectsIfInputIsNull()
	{
		customerCleanupHook.cleanupRelatedObjects(null);
	}
}
