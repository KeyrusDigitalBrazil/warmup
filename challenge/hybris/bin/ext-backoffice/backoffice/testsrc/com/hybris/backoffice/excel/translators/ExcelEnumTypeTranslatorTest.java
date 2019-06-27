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
package com.hybris.backoffice.excel.translators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelEnumTypeTranslatorTest
{

	@Mock
	private ExcelFilter<AttributeDescriptorModel> mandatoryFilter;

	@Mock
	private ExcelFilter<AttributeDescriptorModel> uniqueFilter;
	private final ExcelEnumTypeTranslator translator = new ExcelEnumTypeTranslator();

	@Before
	public void setUp()
	{
		doAnswer(inv -> ((AttributeDescriptorModel) inv.getArguments()[0]).getUnique()).when(uniqueFilter).test(any());
		translator.setExcelUniqueFilter(uniqueFilter);
		translator.setMandatoryFilter(mandatoryFilter);
	}

	@Test
	public void shouldExportDataBeNullSafe()
	{
		// expect
		assertThat(translator.exportData(null).isPresent()).isFalse();
	}

	@Test
	public void shouldExportedDataBeInProperFormat()
	{
		// given
		final String code = "some";
		final HybrisEnumValue hybrisEnumValue = mock(HybrisEnumValue.class);
		given(hybrisEnumValue.getCode()).willReturn(code);

		// when
		final String output = translator.exportData(hybrisEnumValue).map(String.class::cast).get();

		// then
		assertThat(output).isEqualTo(code);
	}

	@Test
	public void shouldGivenTypeBeHandled()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final EnumerationMetaTypeModel enumerationMetaType = mock(EnumerationMetaTypeModel.class);
		given(attributeDescriptor.getAttributeType()).willReturn(enumerationMetaType);

		// when
		final boolean canHandle = translator.canHandle(attributeDescriptor);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldImportEnumValue()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(ProductModel.APPROVALSTATUS);
		final String cellValue = "approved";
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, cellValue,
				UUID.randomUUID().toString(), Collections.emptyList());

		// when
		final ImpexValue impexValue = translator.importValue(attributeDescriptor, importParameters);

		// then
		assertThat(impexValue.getValue()).isEqualTo(cellValue);
		assertThat(impexValue.getHeaderValue().getName()).isEqualTo(String.format("%s(code)", ProductModel.APPROVALSTATUS));
	}
}
