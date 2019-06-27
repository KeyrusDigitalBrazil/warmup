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
package de.hybris.platform.assistedservicestorefront.customer360.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSAPromoData;
import de.hybris.platform.assistedservicepromotionfacades.populator.CSAPromoDataPopulator;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.StubLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CSAPromoDataPopulatorTest
{
	@InjectMocks
	private final CSAPromoDataPopulator populator = new CSAPromoDataPopulator();



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void populateTest()
	{
		final String code = "Test Promo Code";
		final String name = "Test Promo Name";
		final String description = "Test Promo Description";
		final AbstractRuleModel csaPromoModel = new AbstractRuleModel();
		final LocaleProvider localeProvider = new StubLocaleProvider(Locale.ENGLISH);
		final ItemModelContextImpl itemModelContext = (ItemModelContextImpl) csaPromoModel.getItemModelContext();

		itemModelContext.setLocaleProvider(localeProvider);
		csaPromoModel.setCode(code);
		csaPromoModel.setName(name);
		csaPromoModel.setDescription(description);


		final CSAPromoData csaPromoData = new CSAPromoData();

		populator.populate(csaPromoModel, csaPromoData);

		Assert.assertEquals(csaPromoModel.getCode(), csaPromoData.getCode());
		Assert.assertEquals(csaPromoModel.getName(), csaPromoData.getName());
		Assert.assertEquals(csaPromoModel.getDescription(), csaPromoData.getDescription());

	}
}

