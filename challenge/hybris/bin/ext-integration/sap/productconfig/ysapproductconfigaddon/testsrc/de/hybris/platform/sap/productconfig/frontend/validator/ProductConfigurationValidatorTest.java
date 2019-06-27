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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigDataMergeProcessor;
import de.hybris.platform.sap.productconfig.frontend.util.impl.ConfigDataMergeProcessorImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationModelCacheStrategy;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductConfigurationValidatorTest
{
	private final ProductConfigurationValidator classUnderTest = new ProductConfigurationValidator();

	@Mock
	private Errors errorObj;
	@Mock
	private ConfigurationModelCacheStrategy configModelCacheStrategy;
	@Mock
	private CsticValueValidator mockedValidator;

	private ConfigurationData configuration;
	private CsticData cstic;



	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		final ConfigDataMergeProcessor mergeProcessor = new ConfigDataMergeProcessorImpl();
		classUnderTest.setMergeProcessor(mergeProcessor);

		classUnderTest.setConfigModelCacheStrategy(configModelCacheStrategy);
		given(configModelCacheStrategy.getConfigurationModelEngineState(anyString())).willReturn(new ConfigModelImpl());

		classUnderTest.setCsticValidators(Collections.singletonList(mockedValidator));
		given(Boolean.valueOf(mockedValidator.appliesTo(any(CsticData.class)))).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(mockedValidator.appliesToValues())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(mockedValidator.appliesToAdditionalValues())).willReturn(Boolean.TRUE);
		given(Boolean.valueOf(mockedValidator.appliesToFormattedValues())).willReturn(Boolean.TRUE);

		configuration = ValidatorTestData.createConfigurationWithNumeric("numeric", "123");
		configuration.setInputMerged(true);
		cstic = configuration.getGroups().get(0).getCstics().get(0);
	}


	@Test
	public void testValidateSubGrupsNull() throws Exception
	{
		final ConfigurationData configuration = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		// happens if all is collapsed
		configuration.setGroups(null);
		classUnderTest.validateSubGroups(configuration.getGroups(), errorObj, "groups");
		// should run through without NPE

	}

	@Test
	public void notSupportingString() throws Exception
	{
		final boolean supports = classUnderTest.supports(String.class);
		assertFalse("Should not support everything", supports);
	}

	@Test
	public void supportingConfigurationData() throws Exception
	{
		final boolean supports = classUnderTest.supports(ConfigurationData.class);
		assertTrue("Must support ConfigurationData", supports);
	}

	@Test
	public void testEmptyConfiguration() throws Exception
	{
		final ConfigurationData configuration = ValidatorTestData.createEmptyConfigurationWithDefaultGroup();
		configuration.setInputMerged(true);
		classUnderTest.validate(configuration, errorObj);
	}


	@Test
	public void testValidate() throws Exception
	{
		classUnderTest.validate(configuration, errorObj);
		verify(mockedValidator).validate(cstic, errorObj, "123");
	}

	@Test
	public void testValidateNoValidators() throws Exception
	{
		classUnderTest.setCsticValidators(null);
		classUnderTest.validate(configuration, errorObj);
		verifyZeroInteractions(mockedValidator);
	}


	@Test
	public void testValidateInSubGroup() throws Exception
	{
		configuration = ValidatorTestData.createConfigurationWithNumericInSubGroup("numeric", "123");
		configuration.setInputMerged(true);
		final CsticData cstic = configuration.getGroups().get(0).getSubGroups().get(0).getCstics().get(0);

		classUnderTest.validate(configuration, errorObj);
		Mockito.verify(mockedValidator, Mockito.times(1)).validate(cstic, errorObj, "123");
	}

	@Test
	public void testValidateWithModificationAllValuesOK()
	{
		cstic.setValue("aValue");
		cstic.setFormattedValue("aFormattedValue");
		cstic.setAdditionalValue("anAdditionalValue");

		classUnderTest.validateWithModification(errorObj, cstic, mockedValidator);

		verify(mockedValidator).validate(cstic, errorObj, "aValue");
		verify(mockedValidator).validate(cstic, errorObj, "anAdditionalValue");
		verify(mockedValidator).validate(cstic, errorObj, "aFormattedValue");

	}

	@Test
	public void testValidateWithModificationAllValuesNullOrEmpty()
	{
		cstic.setValue("");
		cstic.setFormattedValue("");
		cstic.setAdditionalValue(null);

		classUnderTest.validateWithModification(errorObj, cstic, mockedValidator);
		verifyNoMoreInteractions(mockedValidator);
	}

	@Test
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	public void testValidateWithModificationDoesNotApply()
	{
		cstic.setValue("aValue");
		cstic.setFormattedValue("aFormattedValue");
		cstic.setAdditionalValue("anAdditionalValue");

		given(Boolean.valueOf(mockedValidator.appliesToValues())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(mockedValidator.appliesToAdditionalValues())).willReturn(Boolean.FALSE);
		given(Boolean.valueOf(mockedValidator.appliesToFormattedValues())).willReturn(Boolean.FALSE);

		classUnderTest.validateWithModification(errorObj, cstic, mockedValidator);
		verify(mockedValidator).appliesToValues();
		verify(mockedValidator).appliesToFormattedValues();
		verify(mockedValidator).appliesToAdditionalValues();
		verifyNoMoreInteractions(mockedValidator);
	}

	@Test
	public void testValidateWithModificationAllValuesModified()
	{
		cstic.setValue("aValue");
		cstic.setFormattedValue("aFormattedValue");
		cstic.setAdditionalValue("anAdditionalValue");

		given(mockedValidator.validate(cstic, errorObj, "aValue")).willReturn("aNewValue");
		given(mockedValidator.validate(cstic, errorObj, "aFormattedValue")).willReturn("aNewFormattedValue");
		given(mockedValidator.validate(cstic, errorObj, "anAdditionalValue")).willReturn("aNewAdditionalValue");

		classUnderTest.validateWithModification(errorObj, cstic, mockedValidator);

		assertEquals("aNewValue", cstic.getValue());
		assertEquals("aNewFormattedValue", cstic.getFormattedValue());
		assertEquals("aNewAdditionalValue", cstic.getAdditionalValue());
	}

	@Test
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	public void validateValidatorDoesNotApply()
	{
		cstic.setValue("aValue");
		cstic.setFormattedValue("aFormattedValue");
		cstic.setAdditionalValue("anAdditionalValue");
		given(Boolean.valueOf(mockedValidator.appliesTo(cstic))).willReturn(Boolean.FALSE);

		classUnderTest.validateCstic(errorObj, cstic);

		verify(mockedValidator).appliesTo(cstic);
		verifyNoMoreInteractions(mockedValidator);

		assertEquals("aValue", cstic.getValue());
		assertEquals("aFormattedValue", cstic.getFormattedValue());
		assertEquals("anAdditionalValue", cstic.getAdditionalValue());
	}

}
