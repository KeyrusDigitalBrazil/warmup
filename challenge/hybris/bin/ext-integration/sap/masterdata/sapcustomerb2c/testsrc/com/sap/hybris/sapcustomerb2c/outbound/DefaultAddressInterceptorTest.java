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
package com.sap.hybris.sapcustomerb2c.outbound;

import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.CONTACT_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit Test for class defaultAddressInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 *
 */
@UnitTest
public class DefaultAddressInterceptorTest
{
	@InjectMocks
	private DefaultAddressInterceptor defaultAddressInterceptor;
	@Mock
	private AddressModel addressModel;
	@Mock
	private CustomerModel customerModel;
	@Mock
	private AddressModel defaultShipmentAddress;
	@Mock
	private InterceptorContext ctx;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private DefaultStoreSessionFacade storeSessionFacade;
	@Mock
	private CustomerExportService customerExportService;
	@Mock
	private LanguageData languageData;
	@Mock
	private BaseStoreModel baseStore;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSuccessfulReplication() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, times(1)).getCurrentBaseStore();
		verify(storeSessionFacade, times(1)).getCurrentLanguage();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);
	}

	@Test
	public void testCustomerReplicationDisabled() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.FALSE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, never()).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, never()).getSapContactID();
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeSessionFacade, never()).getCurrentLanguage();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);
	}

	@Test
	public void testAddressOwnerNotCustomerModel() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.FALSE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, never()).getSapContactID();
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeSessionFacade, never()).getCurrentLanguage();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);
	}

	@Test
	public void testSapContactIDNull() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(null);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeSessionFacade, never()).getCurrentLanguage();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);
	}

	@Test
	public void testUnsupportedFieldModified() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.BUILDING)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeSessionFacade, never()).getCurrentLanguage();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);
	}

	@Test
	public void testExportSessionLanguage() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn("DE");

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, times(1)).getCurrentBaseStore();
		verify(languageData, times(1)).getIsocode();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, null, "DE", addressModel);
	}

	@Test
	public void testExportBaseStoreUid() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
		given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
		given(baseStore.getUid()).willReturn("ELECTRONICS");

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, times(2)).getCurrentBaseStore();
		verify(baseStore, times(1)).getUid();
		verify(customerExportService, times(1)).sendCustomerData(customerModel, "ELECTRONICS", null, addressModel);
	}

	@Test
	public void testNotShipmentAddress() throws InterceptorException
	{
		// given
		given(addressModel.getOwner()).willReturn(customerModel);
		given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
		given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
		given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
		given(ctx.isModified(addressModel, AddressModel.COUNTRY)).willReturn(true);
		given(customerModel.getDefaultShipmentAddress()).willReturn(defaultShipmentAddress);

		// when
		defaultAddressInterceptor.onValidate(addressModel, ctx);

		// then
		verify(customerExportService, times(1)).isCustomerReplicationEnabled();
		verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
		verify(customerModel, times(1)).getSapContactID();
		verify(baseStoreService, never()).getCurrentBaseStore();
		verify(storeSessionFacade, never()).getCurrentLanguage();
		verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);
	}
}