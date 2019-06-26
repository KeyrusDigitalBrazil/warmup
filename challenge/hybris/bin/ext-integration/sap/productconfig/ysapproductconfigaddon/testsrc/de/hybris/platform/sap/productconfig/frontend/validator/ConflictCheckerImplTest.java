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
package de.hybris.platform.sap.productconfig.frontend.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;


@UnitTest
public class ConflictCheckerImplTest
{

	@Mock
	private BindingResult bindingResult;

	private ConflictCheckerImpl checker;

	@Before
	public void setup()
	{
		checker = new ConflictCheckerImpl();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNoConflicts()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();

		checker.checkConflicts(config, bindingResult);

		Mockito.verifyZeroInteractions(bindingResult);
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testWithConflictWithText()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict("a conflict");

		checker.checkConflicts(config, bindingResult);


		Mockito.verify(bindingResult, times(1)).addError(Mockito.any(ConflictError.class));
		assertEquals(GroupStatusType.CONFLICT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testDoNotOverwriteErrorStatusOnGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict("a conflict");
		config.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);

		checker.checkConflicts(config, bindingResult);

		assertEquals(GroupStatusType.ERROR, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testWithConflictWithoutText()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict(null);

		checker.checkConflicts(config, bindingResult);


		Mockito.verify(bindingResult, times(1)).addError(Mockito.any(ConflictError.class));
	}

	@Test
	public void testcheckMandatoryFieldsInGroupWithCstic()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);

		GroupStatusType state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);

		assertEquals(GroupStatusType.DEFAULT, state);

		final List<CsticData> cstics = new ArrayList<>();
		uiGroup.setCstics(cstics);

		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		CsticData csticData = new CsticData();
		cstics.add(csticData);

		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		csticData = new CsticData();
		csticData.setRequired(true);
		csticData.setType(UiType.STRING);
		csticData.setValue("Value");
		cstics.add(csticData);

		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		csticData = new CsticData();
		csticData.setRequired(true);
		csticData.setType(UiType.STRING);
		cstics.add(csticData);

		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.WARNING, state);

	}

	@Test
	public void testcheckMandatoryFieldsInGroupWithSubGroups()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);

		final List<UiGroupData> subGroups = new ArrayList<>();
		uiGroup.setSubGroups(subGroups);

		GroupStatusType state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);


		final UiGroupData subUiGroup = new UiGroupData();
		subUiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subGroups.add(subUiGroup);

		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.ERROR);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.WARNING);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.WARNING, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		final UiGroupData subUiGroup2 = new UiGroupData();
		subUiGroup2.setGroupStatus(GroupStatusType.DEFAULT);
		subGroups.add(subUiGroup2);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		subUiGroup2.setGroupStatus(GroupStatusType.ERROR);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.ERROR);
		subUiGroup2.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkMandatoryFieldsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);
	}

	@Test
	public void testcheckConflictsInGroupWithCstic()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);

		GroupStatusType state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);

		assertEquals(GroupStatusType.DEFAULT, state);

		final List<CsticData> cstics = new ArrayList<>();
		uiGroup.setCstics(cstics);

		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		CsticData csticData = new CsticData();
		cstics.add(csticData);

		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		csticData = new CsticData();
		csticData.setRequired(true);
		csticData.setType(UiType.STRING);
		csticData.setValue("Value");
		cstics.add(csticData);

		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		csticData = new CsticData();
		csticData.setRequired(true);
		csticData.setType(UiType.STRING);
		final List<ConflictData> conflicts = new ArrayList<>();
		final ConflictData conflict = new ConflictData();
		conflict.setText("Conflict");
		conflicts.add(conflict);

		csticData.setConflicts(conflicts);
		cstics.add(csticData);

		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

	}


	@Test
	public void testcheckConflictsInGroupWithSubGroups()
	{
		final UiGroupData uiGroup = new UiGroupData();
		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);

		final List<UiGroupData> subGroups = new ArrayList<>();
		uiGroup.setSubGroups(subGroups);

		GroupStatusType state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		final UiGroupData subUiGroup = new UiGroupData();
		subUiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subGroups.add(subUiGroup);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.DEFAULT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.ERROR);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.WARNING);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.WARNING, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		final UiGroupData subUiGroup2 = new UiGroupData();
		subUiGroup2.setGroupStatus(GroupStatusType.DEFAULT);
		subGroups.add(subUiGroup2);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.CONFLICT, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.CONFLICT);
		subUiGroup2.setGroupStatus(GroupStatusType.ERROR);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);

		uiGroup.setGroupStatus(GroupStatusType.DEFAULT);
		subUiGroup.setGroupStatus(GroupStatusType.ERROR);
		subUiGroup2.setGroupStatus(GroupStatusType.CONFLICT);
		state = checker.checkConflitcsInGroups(uiGroup, "", bindingResult);
		assertEquals(GroupStatusType.ERROR, state);

	}


	@Test
	public void testEquals()
	{
		final ConflictError error1 = new ConflictError(null, "Field", null, null, null);
		assertFalse(error1.equals(null));
		assertFalse(error1.equals("TEST"));
		assertTrue(error1.equals(error1));

		ConflictError error2 = new ConflictError(null, "Field", null, null, null);

		assertTrue(error1.equals(error2));
		error2 = new ConflictError(new CsticData(), "Field", null, null, null);
		assertFalse(error1.equals(error2));
		assertFalse(error2.equals(error1));
	}

	@Test
	public void testMandatoryFields()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.WARNING, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testMandatoryFieldsMultipleGroups()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();
		config.getGroups().add(ValidatorTestData.createGroupWithNumeric("2", "abc", "123"));

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.WARNING, config.getGroups().get(0).getGroupStatus());
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(1).getGroupStatus());
	}

	@Test
	public void testMandatoryFieldsMultipleGroupsDoNotOverwriteError()
	{
		final ConfigurationData config = createMandatoryFieldConfiguration();
		config.getGroups().add(ValidatorTestData.createGroupWithNumeric("2", "abc", "123"));
		config.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(3)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.ERROR, config.getGroups().get(0).getGroupStatus());
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(1).getGroupStatus());
	}

	@Test
	public void testNoMandatoryFields()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict(null);

		checker.checkMandatoryFields(config, bindingResult);

		Mockito.verify(bindingResult, times(0)).addError(Mockito.any(MandatoryFieldError.class));
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	private ConfigurationData createMandatoryFieldConfiguration()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		final UiGroupData group = config.getGroups().get(0);

		final CsticData checkBoxListCstic = new CsticData();
		checkBoxListCstic.setRequired(true);
		checkBoxListCstic.setType(UiType.CHECK_BOX_LIST);
		final List<CsticValueData> domainvalues = new ArrayList<>();
		checkBoxListCstic.setDomainvalues(domainvalues);
		group.getCstics().add(checkBoxListCstic);

		final CsticData stringCstic = new CsticData();
		stringCstic.setType(UiType.STRING);
		stringCstic.setRequired(true);
		group.getCstics().add(stringCstic);

		final CsticData dropDownCstic = new CsticData();
		dropDownCstic.setRequired(true);
		dropDownCstic.setType(UiType.DROPDOWN);
		dropDownCstic.setDomainvalues(domainvalues);
		group.getCstics().add(dropDownCstic);
		return config;
	}

	@Test
	public void testCheckCompletnessErrorInGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumeric("123", "aaaa");
		config.getGroups().get(0).setGroupStatus(GroupStatusType.ERROR);
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.ERROR, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessConflictInGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithConflict("conflict");
		config.getGroups().get(0).setGroupStatus(GroupStatusType.CONFLICT);
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.CONFLICT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessEmptyGroup()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		config.getGroups().get(0).setGroupStatus(GroupStatusType.DEFAULT);
		checker.checkCompletness(config);
		assertEquals("Empty group should never be considered as finished", GroupStatusType.DEFAULT,
				config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumeric("filed123", "1");
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.FINISHED, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteGroup_notVisited()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumeric("filed123", "1");
		config.getGroups().get(0).setVisited(false);
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteGroupSubGroupError()
	{
		final ConfigurationData config = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		final UiGroupData subGroup = ValidatorTestData.createGroupWithNumeric("12", "111");
		final UiGroupData subSubGroup = ValidatorTestData.createGroupWithNumeric("1234", "aaa");
		subSubGroup.setGroupStatus(GroupStatusType.ERROR);
		subGroup.setSubGroups(Collections.singletonList(subSubGroup));
		config.getGroups().get(0).setSubGroups(Collections.singletonList(subGroup));
		checker.checkCompletness(config);
		assertEquals("Erropr in subgroup should prevent parent from becomming completed", GroupStatusType.DEFAULT,
				config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteGroupNoMandatoryNoValue()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumeric("filed123", null);
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.FINISHED, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteGroupMandatoryNoValue()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumeric("filed123", null);
		config.getGroups().get(0).getCstics().get(0).setRequired(true);
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.DEFAULT, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessCompleteSubGroup()
	{
		final ConfigurationData config = ValidatorTestData.createConfigurationWithNumericInSubGroup("1223", "2");
		checker.checkCompletness(config);
		assertEquals(GroupStatusType.FINISHED, config.getGroups().get(0).getSubGroups().get(0).getGroupStatus());
		assertEquals(GroupStatusType.FINISHED, config.getGroups().get(0).getGroupStatus());
	}

	@Test
	public void testCheckCompletnessNullGroup()
	{
		final ConfigurationData config = new ConfigurationData();
		checker.checkCompletness(config);
		// no expception
	}
}
