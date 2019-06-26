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
package com.sap.hybris.sapcustomerb2b.outbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.services.impl.DefaultBaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 *
 */
@UnitTest
public class DefaultB2BCustomerInterceptorTest
{
	@InjectMocks
	private DefaultB2BCustomerInterceptor defaultCustomerInterceptor;
	@Mock
	private B2BCustomerModel b2bCustomerModel;
	@Mock
	private InterceptorContext ctx;
	@Mock
	private DefaultBaseStoreService defaultBaseStoreService;
	@Mock
	private DefaultStoreSessionFacade storeSessionFacade;
	@Mock
	private B2BCustomerExportService b2bCustomerExportService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testB2BCustomerReplicationDisabled() throws InterceptorException
	{
		// given
		given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.FALSE);
		given(ctx.isNew(b2bCustomerModel)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SAPISREPLICATED)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
	}

	@Test
	public void testCustomerIsNew() throws InterceptorException
	{
		// given
		given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(ctx.isNew(b2bCustomerModel)).willReturn(true);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SAPISREPLICATED)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
	}

	@Test
	public void testValidatorIsBeingCalledFromDataHub() throws InterceptorException
	{
		// given
		given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(ctx.isNew(b2bCustomerModel)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SAPISREPLICATED)).willReturn(true);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
	}

	// we only replicate to Data Hub for changes to NAME, TITLE, UID, DEFAULTSHIPMENTADDRESS
	@Test
	public void testUnsupportedField() throws InterceptorException
	{
		// given
		given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(ctx.isNew(b2bCustomerModel)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SAPISREPLICATED)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.DESCRIPTION)).willReturn(true);
		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
		// then
		verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
	}

	@Test
	public void testExportNameModified() throws InterceptorException
	{
		// given
		given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(ctx.isNew(b2bCustomerModel)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.SAPISREPLICATED)).willReturn(false);
		given(ctx.isModified(b2bCustomerModel, CustomerModel.NAME)).willReturn(true);
		// when
		defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
		// then
		verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel, "en");
	}
}