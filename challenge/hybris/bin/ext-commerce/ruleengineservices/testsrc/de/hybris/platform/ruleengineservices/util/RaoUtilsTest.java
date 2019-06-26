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
package de.hybris.platform.ruleengineservices.util;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rao.AbstractActionedRAO;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.DiscountRAO;
import de.hybris.platform.ruleengineservices.rao.ShipmentRAO;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@UnitTest
public class RaoUtilsTest
{

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private RaoUtils raoUtils;

	@Before
	public void setUp()
	{
		raoUtils = new RaoUtils();
	}

	@Test
	public void testGetDiscountsWithNull()
	{
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("actionedRao object is not expected to be NULL here");

		raoUtils.getDiscounts(null);
	}

	@Test
	public void testGetDiscountsEmpty()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final Set<DiscountRAO> discounts = raoUtils.getDiscounts(actionedRao);
		assertThat(discounts).isEmpty();
	}

	@Test
	public void testGetDiscounts()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final DiscountRAO discount = new DiscountRAO();
		actionedRao.setActions(new LinkedHashSet<>(asList(new AbstractRuleActionRAO(), discount)));
		final Set<DiscountRAO> discounts = raoUtils.getDiscounts(actionedRao);
		assertThat(discounts).isNotEmpty().hasSize(1);
		assertThat(discounts.iterator().next()).isEqualTo(discount);
	}

	@Test
	public void testGetShipmentWithNull()
	{
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("actionedRao object is not expected to be NULL here");

		raoUtils.getShipment(null);
	}

	@Test
	public void testGetShipmentEmpty()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final Optional<ShipmentRAO> shipmentRao = raoUtils.getShipment(actionedRao);
		assertThat(shipmentRao.isPresent()).isFalse();
	}

	@Test
	public void testGetShipment()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final ShipmentRAO shipment1 = new ShipmentRAO();
		final ShipmentRAO shipment2 = new ShipmentRAO();
		actionedRao.setActions(new LinkedHashSet<>(asList(new AbstractRuleActionRAO(), shipment1, shipment2)));
		final Optional<ShipmentRAO> shipmentRao = raoUtils.getShipment(actionedRao);
		assertThat(shipmentRao.isPresent()).isTrue();
		assertThat(shipmentRao.get()).isEqualTo(shipment1);
	}

	@Test
	public void testIsAbsoluteFalse()
	{
		final DiscountRAO discount = new DiscountRAO();
		final boolean absolute = raoUtils.isAbsolute(discount);
		assertThat(absolute).isFalse();
	}

	@Test
	public void testIsAbsoluteTrue()
	{
		final DiscountRAO discount = new DiscountRAO();
		discount.setCurrencyIsoCode("USD");
		final boolean absolute = raoUtils.isAbsolute(discount);
		assertThat(absolute).isTrue();
	}

	@Test
	public void testAddActionWithNullActioned()
	{
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("actionedRao object is not expected to be NULL here");

		raoUtils.addAction(null, new AbstractRuleActionRAO());
	}

	@Test
	public void testAddActionWithNullAction()
	{
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("actionRao object is not expected to be NULL here");

		raoUtils.addAction(new AbstractActionedRAO(), null);
	}

	@Test
	public void testAddActionAddToEmpty()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final AbstractRuleActionRAO actionRao = new AbstractRuleActionRAO();

		raoUtils.addAction(actionedRao, actionRao);

		assertThat(actionedRao.getActions()).isNotEmpty().hasSize(1);
		assertThat(actionedRao.getActions().iterator().next()).isEqualTo(actionRao);
	}

	@Test
	public void testAddActionAddToExisting()
	{
		final AbstractActionedRAO actionedRao = new AbstractActionedRAO();
		final AbstractRuleActionRAO actionRao = new AbstractRuleActionRAO();

		actionedRao.setActions(new LinkedHashSet<>(Collections.singleton(new AbstractRuleActionRAO())));

		raoUtils.addAction(actionedRao, actionRao);

		assertThat(actionedRao.getActions()).isNotEmpty().hasSize(2);
		final Iterator<AbstractRuleActionRAO> iterator = actionedRao.getActions().iterator();
		iterator.next();
		assertThat(iterator.next()).isEqualTo(actionRao);
	}

}
