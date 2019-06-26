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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictTextParser;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSChoice;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConflict;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSNogood;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ConflictPopulatorTest
{
	private static final String CONFLICT_ID = "CONFLICT_1";
	private static final String CONFLICT_NAME = "TestConflict";
	private static final String CONFLICT_EXPLANATION_MESSAGE = "This is a conflict";
	private static final int CONFLICT_TYPE = 1;

	private ConflictPopulator classUnderTest;

	private CPSConflict source;
	private SolvableConflictModel target;

	@Mock
	private Converter<CPSChoice, ConflictingAssumptionModel> conflictAssumptionConverter;
	@Mock
	private CPSConflictTextParser conflictTextParser;
	private CPSNogood nogood;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new ConflictPopulator();

		classUnderTest.setConflictAssumptionConverter(conflictAssumptionConverter);
		classUnderTest.setConflictTextParser(conflictTextParser);

		when(conflictTextParser.parseConflictText(Mockito.eq(CONFLICT_EXPLANATION_MESSAGE)))
				.thenReturn(CONFLICT_EXPLANATION_MESSAGE);
		when(conflictAssumptionConverter.convert(any(CPSChoice.class))).thenReturn(new ConflictingAssumptionModelImpl());

		source = new CPSConflict();
		source.setId(CONFLICT_ID);
		source.setExplanation(CONFLICT_EXPLANATION_MESSAGE);
		source.setName(CONFLICT_NAME);
		source.setType(CONFLICT_TYPE);

		final List<String> itemIds = new ArrayList<>();
		itemIds.add("1");
		source.setItemIds(itemIds);

		final List<CPSNogood> nogoods = new ArrayList<>();
		nogood = new CPSNogood();
		nogood.setId("0");
		final List<CPSChoice> choices = new ArrayList<>();
		choices.add(createChoice("1", "CSTIC_1", "Value1"));
		choices.add(createChoice("1", "CSTIC_2", "Value2"));
		nogood.setChoices(choices);
		nogoods.add(nogood);
		source.setNogoods(nogoods);

		target = new SolvableConflictModelImpl();
	}

	private CPSChoice createChoice(final String itemId, final String csticId, final String csticValue)
	{
		final CPSChoice choice = new CPSChoice();
		choice.setItemId(itemId);
		choice.setCharacteristicId(csticId);
		final CPSValue value = new CPSValue();
		value.setValue(csticValue);
		choice.setValue(value);
		return choice;
	}

	@Test
	public void testPopulateConflict()
	{
		classUnderTest.populate(source, target);

		assertEquals(CONFLICT_ID, target.getId());
		assertEquals(CONFLICT_EXPLANATION_MESSAGE, target.getDescription());

		final List<ConflictingAssumptionModel> assumptions = target.getConflictingAssumptions();
		assertNotNull(assumptions);
		assertEquals(2, assumptions.size());
	}

	@Test
	public void testConvertChoiceToAssumption()
	{
		final List<ConflictingAssumptionModel> result = classUnderTest.convertChoicesToAssumptions(nogood);

		assertNotNull(result);
		assertEquals(2, result.size());
	}
}
