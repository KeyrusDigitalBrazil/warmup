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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.util.StandardDateRange;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BBudgetPopulatorTest
{
	private static final String CUR_ISOCODE = "currIsoCode";

	@InjectMocks
	private final B2BBudgetPopulator b2BBudgetPopulator = new B2BBudgetPopulator();

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;

	private B2BBudgetModel b2BBudgetModel;
	private B2BBudgetData b2BBudgetData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldPopulate() throws ParseException
	{
		b2BBudgetModel = mock(B2BBudgetModel.class);
		b2BBudgetPopulator.setCurrencyConverter(currencyConverter);

		final String startString = "January 2, 2010";
		final String endString = "January 2, 2050";
		final DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		final Date start = format.parse(startString);
		final Date end = format.parse(endString);
		final StandardDateRange dateRange = new StandardDateRange(start, end);

		given(b2BBudgetModel.getCode()).willReturn("budgetCode");
		given(b2BBudgetModel.getName()).willReturn("budgetName");
		given(b2BBudgetModel.getActive()).willReturn(Boolean.TRUE);
		given(b2BBudgetModel.getDateRange()).willReturn(dateRange);

		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		final CurrencyData curData = new CurrencyData();
		curData.setIsocode(CUR_ISOCODE);
		given(currencyConverter.convert(currencyModel)).willReturn(curData);
		given(b2BBudgetModel.getCurrency()).willReturn(currencyModel);

		final B2BUnitModel unitModel = mock(B2BUnitModel.class);
		given(unitModel.getUid()).willReturn("unitUit");
		given(unitModel.getLocName()).willReturn("unitName");
		given(unitModel.getActive()).willReturn(Boolean.TRUE);
		given(b2BBudgetModel.getUnit()).willReturn(unitModel);


		final Set<B2BCostCenterModel> costCenterModelSet = new HashSet<>();
		final B2BCostCenterModel costCenter0 = mock(B2BCostCenterModel.class);
		given(costCenter0.getName()).willReturn("costCenter0");
		costCenterModelSet.add(costCenter0);
		final B2BCostCenterModel costCenter1 = mock(B2BCostCenterModel.class);
		given(costCenter1.getName()).willReturn("costCenter1");
		costCenterModelSet.add(costCenter1);
		given(b2BBudgetModel.getCostCenters()).willReturn(costCenterModelSet);

		b2BBudgetData = new B2BBudgetData();
		b2BBudgetPopulator.populate(b2BBudgetModel, b2BBudgetData);

		Assert.assertEquals("Unexpected value for uid", b2BBudgetModel.getCode(), b2BBudgetData.getCode());
		Assert.assertEquals("Unexpected value for name", b2BBudgetModel.getName(), b2BBudgetData.getName());
		Assert.assertEquals("Unexpected value for active", b2BBudgetModel.getActive(), Boolean.valueOf(b2BBudgetData.isActive()));
		Assert.assertEquals("Unexpected value for StartDate", b2BBudgetModel.getDateRange().getStart(),
				b2BBudgetData.getStartDate());
		Assert.assertEquals("Unexpected value for EndDate", b2BBudgetModel.getDateRange().getEnd(), b2BBudgetData.getEndDate());
		Assert.assertEquals("Unexpected value for Currency", b2BBudgetModel.getCurrency().getIsocode(),
				b2BBudgetData.getCurrency().getIsocode());

		Assert.assertNotNull("Unit of b2BBudgetModel is null", b2BBudgetModel.getUnit());
		Assert.assertNotNull("Unit of b2BBudgetData is null", b2BBudgetData.getUnit());
		Assert.assertEquals("Unexpected value for unit uid", b2BBudgetModel.getUnit().getUid(), b2BBudgetData.getUnit().getUid());
		Assert.assertEquals("Unexpected value for unit name", b2BBudgetModel.getUnit().getLocName(),
				b2BBudgetData.getUnit().getName());
		Assert.assertEquals("Unexpected value for unit active", b2BBudgetModel.getUnit().getActive(),
				Boolean.valueOf(b2BBudgetData.getUnit().isActive()));

		final ArrayList<B2BCostCenterModel> costCenterModels = new ArrayList(b2BBudgetModel.getCostCenters());
		final List<String> costCenterNames = b2BBudgetData.getCostCenterNames();
		Collections.sort(costCenterModels, new CostCenterComparator());
		Collections.sort(costCenterNames);
		Assert.assertEquals("Unexpected value for cost center size", costCenterModels.size(), costCenterNames.size());
		Assert.assertEquals("Unexpected value for CostCenters().get(0)", costCenterModels.get(0).getName(), costCenterNames.get(0));
		Assert.assertEquals("Unexpected value for CostCenters().get(1)", costCenterModels.get(1).getName(), costCenterNames.get(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUserGroupModel()
	{
		b2BBudgetPopulator.populate(null, b2BBudgetData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUserGroupData()
	{
		b2BBudgetPopulator.populate(b2BBudgetModel, null);
	}

	public class CostCenterComparator implements Comparator<B2BCostCenterModel>
	{
		@Override
		public int compare(final B2BCostCenterModel c0, final B2BCostCenterModel c1)
		{
			return c0.getName().compareTo(c1.getName());
		}
	}
}
