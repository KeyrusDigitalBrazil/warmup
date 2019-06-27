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
package de.hybris.platform.sap.productconfig.rules.rao.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.UserGroupRAO;
import de.hybris.platform.ruleengineservices.rao.UserRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticRAO;
import de.hybris.platform.sap.productconfig.rules.rao.CsticValueRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigRAO;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigCartRAOProviderTest
{
	private ProductConfigCartRAOProvider classUnderTest;

	@Mock
	private Converter<CartModel, CartRAO> cartRaoConverter;

	private CartModel cartModel;
	private CartRAO cartRAO;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigCartRAOProvider();
		cartRAO = new CartRAO();
		cartModel = new CartModel();
		given(cartRaoConverter.convert(cartModel)).willReturn(cartRAO);
		classUnderTest.setCartRaoConverter(cartRaoConverter);
	}

	@Test
	public void testCreateRAO()
	{
		final CartRAO cartRao = classUnderTest.createRAO(cartModel);
		assertNotNull(cartRao);
	}

	@Test
	public void testExpandFactModel_2differentconfigs()
	{
		fillCartRAODifferent(cartRAO);
		final Set<Object> expandFactModel = classUnderTest.expandFactModel(cartModel);
		assertNotNull(expandFactModel);
		assertEquals(12, expandFactModel.size());
	}


	@Test
	public void testExpandFactModel_2sameconfigs()
	{
		fillCartRAOSame(cartRAO);
		final Set<Object> expandFactModel = classUnderTest.expandFactModel(cartModel);
		assertNotNull(expandFactModel);
		assertEquals(11, expandFactModel.size());
	}

	private void fillCartRAODifferent(final CartRAO cartRAO)
	{
		final CsticValueRAO csticValueRAO111 = new CsticValueRAO();
		csticValueRAO111.setCsticValueName("Value111");
		csticValueRAO111.setConfigId("conf1");
		csticValueRAO111.setCsticName("Cstic11");
		final CsticValueRAO csticValueRAO112 = new CsticValueRAO();
		csticValueRAO112.setCsticValueName("Value112");
		csticValueRAO112.setConfigId("conf1");
		csticValueRAO112.setCsticName("Cstic11");
		final CsticRAO csticRAO11 = new CsticRAO();
		csticRAO11.setConfigId("conf1");
		csticRAO11.setCsticName("Cstic11");
		final List<CsticValueRAO> csticValues11 = new ArrayList<CsticValueRAO>();
		csticValues11.add(csticValueRAO111);
		csticValues11.add(csticValueRAO112);
		csticRAO11.setAssignedValues(csticValues11);

		final CsticValueRAO csticValueRAO121 = new CsticValueRAO();
		csticValueRAO121.setCsticValueName("Value121");
		csticValueRAO121.setConfigId("conf1");
		csticValueRAO121.setCsticName("Cstic12");
		final CsticValueRAO csticValueRAO122 = new CsticValueRAO();
		csticValueRAO122.setCsticValueName("Value122");
		csticValueRAO122.setConfigId("conf1");
		csticValueRAO122.setCsticName("Cstic12");
		final CsticRAO csticRAO12 = new CsticRAO();
		csticRAO12.setCsticName("Cstic12");
		csticRAO12.setConfigId("conf1");
		final List<CsticValueRAO> csticValues12 = new ArrayList<CsticValueRAO>();
		csticValues12.add(csticValueRAO121);
		csticValues12.add(csticValueRAO122);
		csticRAO12.setAssignedValues(csticValues12);

		final ProductConfigRAO productConfigRAO1 = new ProductConfigRAO();
		productConfigRAO1.setConfigId("conf1");
		final List<CsticRAO> cstics1 = new ArrayList<CsticRAO>();
		cstics1.add(csticRAO11);
		cstics1.add(csticRAO12);
		productConfigRAO1.setCstics(cstics1);

		final OrderEntryRAO orderEntryRAO1 = new OrderEntryRAO();
		orderEntryRAO1.setEntryNumber(Integer.valueOf(1));
		orderEntryRAO1.setProductConfiguration(productConfigRAO1);

		final CsticRAO csticRAO21 = new CsticRAO();
		csticRAO21.setCsticName("Cstic21");
		csticRAO21.setConfigId("conf2");
		final ProductConfigRAO productConfigRAO2 = new ProductConfigRAO();
		final List<CsticRAO> cstics2 = new ArrayList<CsticRAO>();
		cstics2.add(csticRAO21);
		productConfigRAO2.setCstics(cstics2);
		productConfigRAO2.setConfigId("conf2");

		final OrderEntryRAO orderEntryRAO2 = new OrderEntryRAO();
		orderEntryRAO2.setEntryNumber(Integer.valueOf(2));
		orderEntryRAO2.setProductConfiguration(productConfigRAO2);

		final Set<OrderEntryRAO> entries = new HashSet<OrderEntryRAO>();
		entries.add(orderEntryRAO1);
		entries.add(orderEntryRAO2);
		cartRAO.setEntries(entries);

		return;
	}

	private void fillCartRAOSame(final CartRAO cartRAO)
	{
		final ProductConfigRAO productConfigRAO1 = createRAOForConfigWithOneCstic("conf1");
		final OrderEntryRAO orderEntryRAO1 = new OrderEntryRAO();
		orderEntryRAO1.setEntryNumber(Integer.valueOf(1));
		orderEntryRAO1.setProductConfiguration(productConfigRAO1);

		final ProductConfigRAO productConfigRAO2 = createRAOForConfigWithOneCstic("conf2");
		final OrderEntryRAO orderEntryRAO2 = new OrderEntryRAO();
		orderEntryRAO2.setEntryNumber(Integer.valueOf(2));
		orderEntryRAO2.setProductConfiguration(productConfigRAO2);

		final Set<OrderEntryRAO> entries = new HashSet<OrderEntryRAO>();
		entries.add(orderEntryRAO1);
		entries.add(orderEntryRAO2);
		cartRAO.setEntries(entries);

		return;
	}

	protected ProductConfigRAO createRAOForConfigWithOneCstic(final String configId)
	{
		final CsticValueRAO csticValueRAO1 = new CsticValueRAO();
		csticValueRAO1.setCsticValueName("value1");
		csticValueRAO1.setConfigId(configId);
		csticValueRAO1.setCsticName("cstic");
		final CsticValueRAO csticValueRAO2 = new CsticValueRAO();
		csticValueRAO2.setCsticValueName("value2");
		csticValueRAO2.setConfigId(configId);
		csticValueRAO2.setCsticName("cstic");
		final CsticRAO csticRAO = new CsticRAO();
		csticRAO.setConfigId(configId);
		csticRAO.setCsticName("cstic");
		final List<CsticValueRAO> csticValues = new ArrayList<CsticValueRAO>();
		csticValues.add(csticValueRAO1);
		csticValues.add(csticValueRAO2);
		csticRAO.setAssignedValues(csticValues);


		final ProductConfigRAO productConfigRAO = new ProductConfigRAO();
		productConfigRAO.setConfigId(configId);
		final List<CsticRAO> cstics = new ArrayList<CsticRAO>();
		cstics.add(csticRAO);
		productConfigRAO.setCstics(cstics);
		return productConfigRAO;
	}

	@Test
	public void testExpandFactModelUserAndGroups()
	{
		final UserRAO userRAO = new UserRAO();
		final UserGroupRAO userGroupRAO1 = new UserGroupRAO();
		userGroupRAO1.setId("Group1");
		final UserGroupRAO userGroupRAO2 = new UserGroupRAO();
		userGroupRAO2.setId("Group2");
		final Set<UserGroupRAO> groups = new LinkedHashSet<UserGroupRAO>();
		groups.add(userGroupRAO1);
		groups.add(userGroupRAO2);
		userRAO.setGroups(groups);
		cartRAO.setUser(userRAO);

		final Set<Object> expandFactModel = classUnderTest.expandFactModel(cartModel);
		assertNotNull(expandFactModel);
		assertEquals(4, expandFactModel.size());
	}

}
