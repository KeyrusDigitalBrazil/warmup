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
package de.hybris.platform.sap.productconfig.services.tracking.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ValueChangeType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingWriter;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


@UnitTest
public class TrackingRecorderImplTest
{

	private static final String CART_GUID = "CartGuid";
	private static final String CONFIG_ID_TO_BE_DELETED = "ConfigIdToBeDeleted";
	private static final String PRODUCT_CODE = "ProductCode";
	private static final String CONFIG_ID = "ConfigId";
	private static final String SESSION_ID = "SessionId";

	@Spy
	private TrackingRecorderImpl classUnderTest;

	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;
	@Mock
	private TrackingWriter writer;
	@Mock
	private TrackingWriter anotherWriter;
	@Mock
	AbstractOrderEntryModel cartEntry;
	@Mock
	CartModel cart;

	private ConfigModel configModel;
	private KBKey kbKey;
	private List<CsticValueDelta> deltas;

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new TrackingRecorderImpl();
		final List<TrackingWriter> writers = new ArrayList<>();
		writers.add(writer);
		classUnderTest.setWriters(writers);
		classUnderTest.setSessionService(sessionService);

		configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);

		deltas = createCsticValueDeltaList();
		configModel.setCsticValueDeltas(deltas);

		kbKey = new KBKeyImpl("TestProductCode", "TestKBName", "TestLogSys", "1.0", new Date());

		Mockito.when(sessionService.getCurrentSession()).thenReturn(session);
		Mockito.when(session.getSessionId()).thenReturn(SESSION_ID);

		Mockito.when(cartEntry.getPk()).thenReturn(PK.fromLong(2));
		final ProductModel product = new ProductModel();
		product.setCode(PRODUCT_CODE);
		Mockito.when(cartEntry.getProduct()).thenReturn(product);
		Mockito.when(cartEntry.getQuantity()).thenReturn(Long.valueOf("1"));

		Mockito.when(cart.getGuid()).thenReturn(CART_GUID);
	}


	@Test
	public void recordCreateConfigurationTest()
	{
		classUnderTest.recordCreateConfiguration(configModel, kbKey);
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void recordCreateConfigurationTestDisabled()
	{
		classUnderTest.setTrackingEnabled(false);
		classUnderTest.recordCreateConfiguration(configModel, kbKey);
		Mockito.verify(writer, times(0)).trackingItemCreated(Mockito.any());
		assertEquals(deltas, configModel.getCsticValueDeltas());
	}

	@Test
	public void testRecordUpdateConfiguration()
	{
		classUnderTest.recordUpdateConfiguration(configModel);
		Mockito.verify(writer, times(4)).trackingItemCreated(Mockito.any());
		assertTrue(configModel.getCsticValueDeltas().isEmpty());
	}

	@Test
	public void testRecordUpdateConfigurationDisabled()
	{
		classUnderTest.setTrackingEnabled(false);
		classUnderTest.recordUpdateConfiguration(configModel);
		Mockito.verify(writer, times(0)).trackingItemCreated(Mockito.any());
		assertEquals(deltas, configModel.getCsticValueDeltas());
	}

	@Test
	public void testNotifyWriters()
	{
		final List<TrackingWriter> writers = classUnderTest.getWriters();
		writers.add(anotherWriter);
		classUnderTest.setWriters(writers);
		classUnderTest.notifyWriter(new TrackingItem());
		Mockito.verify(writer, times(1)).trackingItemCreated(Mockito.any());
		Mockito.verify(anotherWriter, times(1)).trackingItemCreated(Mockito.any());
	}

	@Test
	public void testRecordCartEventDelete()
	{
		final CommerceCartParameter parameters = createCommerceCartParameters();
		final TrackingItem item = classUnderTest.recordCartEvent(cartEntry, parameters, EventType.DELETE_CART_ENTRY);
		assertNotNull(item);
		assertNotNull(item.getTrackingItemKey());
		assertEquals(DigestUtils.sha256Hex(CONFIG_ID_TO_BE_DELETED), item.getTrackingItemKey().getConfigId());
	}

	@Test
	public void testRecordCartEventAddToCart()
	{
		final CommerceCartParameter parameters = createCommerceCartParameters();
		final TrackingItem item = classUnderTest.recordCartEvent(cartEntry, parameters, EventType.ADD_TO_CART);
		assertNotNull(item);
		assertNotNull(item.getTrackingItemKey());
		assertEquals(DigestUtils.sha256Hex(CONFIG_ID), item.getTrackingItemKey().getConfigId());
		assertEquals(DigestUtils.sha256Hex(CART_GUID), item.getParameters().get("CART"));
		assertEquals(DigestUtils.sha256Hex(SESSION_ID), item.getTrackingItemKey().getSessionId());
		assertEquals(DigestUtils.sha256Hex(PK.fromLong(2).toString()), item.getParameters().get("CART_ITEM_PK"));
	}

	@Test
	public void testRecordUpdateCartEntry()
	{
		final CommerceCartParameter parameters = createCommerceCartParameters();
		parameters.setCreateNewEntry(true);
		classUnderTest.recordUpdateCartEntry(cartEntry, parameters);
		Mockito.verify(writer, times(0)).trackingItemCreated(Mockito.any());
	}

	private CommerceCartParameter createCommerceCartParameters()
	{
		final CommerceCartParameter params = new CommerceCartParameter();
		params.setConfigId(CONFIG_ID);
		params.setConfigToBeDeleted(CONFIG_ID_TO_BE_DELETED);
		params.setCart(cart);
		return params;
	}

	private List<CsticValueDelta> createCsticValueDeltaList()
	{
		final List<CsticValueDelta> deltas = new ArrayList<>();
		final CsticValueDelta delta1 = createCsticValueDelta("INSTANCENAME1", "ID1", ValueChangeType.SET, "CSTICNAME1", "VAL1",
				"VAL2");
		deltas.add(delta1);

		final CsticValueDelta delta2 = createCsticValueDelta("INSTANCENAME1", "ID1", ValueChangeType.SET, "CSTICNAME2", "VAL3",
				"VAL4");
		deltas.add(delta2);

		return deltas;
	}


	private CsticValueDelta createCsticValueDelta(final String instanceName, final String instanceId,
			final ValueChangeType changeType, final String csticName, final String... valueNames)
	{
		final CsticValueDelta delta = new CsticValueDelta();
		delta.setInstanceName(instanceName);
		delta.setInstanceId(instanceId);
		delta.setChangeType(changeType);
		delta.setCsticName(csticName);
		final List<String> values = new ArrayList<>();
		for (int i = 0; i < valueNames.length; i++)
		{
			values.add(valueNames[i]);
		}
		delta.setValueNames(values);

		return delta;
	}
}
