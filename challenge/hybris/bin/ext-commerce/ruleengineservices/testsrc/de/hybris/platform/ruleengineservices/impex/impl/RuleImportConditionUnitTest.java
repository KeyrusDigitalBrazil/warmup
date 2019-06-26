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
package de.hybris.platform.ruleengineservices.impex.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.ArrayUtils.removeElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleImportConditionUnitTest
{
	private RuleImportCondition condition = spy(new RuleImportCondition());

	@Mock
	private ValueLine valueLine;

	@Mock
	private ValueLine.ValueEntry valueEntry;

	@Mock
	private HeaderDescriptor header;

	@Mock
	private RuleDao ruleDao;

	@Mock
	private AbstractColumnDescriptor columnDescriptor;

	@Before
	public void setUp()
	{
		when(condition.getRuleDao()).thenReturn(ruleDao);
		when(valueLine.getHeader()).thenReturn(header);
		when(valueEntry.getCellValue()).thenReturn("promotion_code");
		when(columnDescriptor.getValuePosition()).thenReturn(0);
		when(valueLine.getValueEntry(0)).thenReturn(valueEntry);
	}

	@Test(expected = UnknownIdentifierException.class)
	public void shouldThrowUnknownIdentifierExceptionOnMissingCodeColumn()
	{
		condition.test(valueLine);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void shouldThrowAmbiguousIdentifierExceptionOnMultipleCodeColumns()
	{
		when(header.getColumnsByQualifier("code")).thenReturn(
				asList(mock(AbstractColumnDescriptor.class), mock(AbstractColumnDescriptor.class)));

		condition.test(valueLine);
	}

	@Test
	public void shouldReturnFalse()
	{
		when(ruleDao.findAllRuleVersionsByCodeAndStatuses("promotion_code",
				removeElement(RuleStatus.values(), RuleStatus.UNPUBLISHED))).thenReturn(
				asList(mock(AbstractRuleModel.class), mock(AbstractRuleModel.class)));

		when(header.getColumnsByQualifier("code")).thenReturn(asList(columnDescriptor));

		assertThat(condition.test(valueLine)).isFalse();
	}

	@Test
	public void shouldReturnTrue()
	{
		when(ruleDao.findAllRuleVersionsByCodeAndStatuses("promotion_code",
				removeElement(RuleStatus.values(), RuleStatus.UNPUBLISHED))).thenReturn(Collections.emptyList());

		when(header.getColumnsByQualifier("code")).thenReturn(asList(columnDescriptor));

		assertThat(condition.test(valueLine)).isTrue();
	}
}
