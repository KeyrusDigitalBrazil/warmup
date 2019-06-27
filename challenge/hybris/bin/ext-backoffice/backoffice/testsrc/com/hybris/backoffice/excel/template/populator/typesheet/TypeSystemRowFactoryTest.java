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
package com.hybris.backoffice.excel.template.populator.typesheet;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.template.AttributeNameFormatter;
import com.hybris.backoffice.excel.template.CollectionFormatter;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;
import com.hybris.backoffice.excel.translators.ExcelTranslatorRegistry;
import com.hybris.backoffice.excel.translators.ExcelValueTranslator;


@RunWith(MockitoJUnitRunner.class)
public class TypeSystemRowFactoryTest
{
	@Mock
	ExcelFilter<AttributeDescriptorModel> uniqueFilter;
	@Mock
	CommonI18NService mockedCommonI18NService;
	@Mock
	ExcelTranslatorRegistry mockedExcelTranslatorRegistry;
	@Mock
	AttributeNameFormatter mockedAttributeNameFormatter;
	@Mock
	CollectionFormatter mockedCollectionFormatter;
	@InjectMocks
	TypeSystemRowFactory typeSystemRowFactory;

	@Before
	public void setUp()
	{
		doAnswer(inv -> ((AttributeDescriptorModel) inv.getArguments()[0]).getUnique()).when(uniqueFilter).test(any());
	}

	@Test
	public void shouldCreateTypeSystemRowFormAttributeDescriptor()
	{
		// given
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(composedTypeModel.getCode()).willReturn("enclosingTypeCode");
		given(composedTypeModel.getName()).willReturn("enclosingTypeName");

		final TypeModel typeModel = mock(TypeModel.class);
		given(typeModel.getCode()).willReturn("attributeTypeCode");

		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getEnclosingType()).willReturn(composedTypeModel);
		given(attributeDescriptorModel.getQualifier()).willReturn("qualifier");
		given(attributeDescriptorModel.getName()).willReturn("name");
		given(attributeDescriptorModel.getOptional()).willReturn(true);
		given(attributeDescriptorModel.getAttributeType()).willReturn(typeModel);
		given(attributeDescriptorModel.getDeclaringEnclosingType()).willReturn(composedTypeModel);
		given(attributeDescriptorModel.getLocalized()).willReturn(true);
		given(attributeDescriptorModel.getUnique()).willReturn(true);

		given(mockedCollectionFormatter.formatToString("enclosingTypeCode")).willReturn("formattedEnclosingTypeCode");
		given(mockedCommonI18NService.getAllLanguages()).willReturn(Collections.emptyList());
		given(mockedExcelTranslatorRegistry.getTranslator(attributeDescriptorModel)).willReturn(Optional.empty());

		// when
		final TypeSystemRow typeSystemRow = typeSystemRowFactory.create(attributeDescriptorModel);

		// then
		assertThat(typeSystemRow.getTypeCode()).isEqualTo("formattedEnclosingTypeCode");
		assertThat(typeSystemRow.getTypeName()).isEqualTo("enclosingTypeName");
		assertThat(typeSystemRow.getAttrQualifier()).isEqualTo("qualifier");
		assertThat(typeSystemRow.getAttrName()).isEqualTo("name");
		assertThat(typeSystemRow.getAttrOptional()).isEqualTo(true);
		assertThat(typeSystemRow.getAttrTypeCode()).isEqualTo("attributeTypeCode");
		assertThat(typeSystemRow.getAttrTypeItemType()).isEqualTo("enclosingTypeCode");
		assertThat(typeSystemRow.getAttrLocalized()).isEqualTo(true);
		assertThat(typeSystemRow.getAttrUnique()).isEqualTo(true);
	}

	@Test
	public void shouldCreateTypeSystemWithAttributeDisplayName()
	{
		// given
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getAttributeType()).willReturn(mock(TypeModel.class));
		given(attributeDescriptorModel.getName()).willReturn("name");
		given(attributeDescriptorModel.getQualifier()).willReturn("qualifier");
		given(attributeDescriptorModel.getEnclosingType()).willReturn(mock(ComposedTypeModel.class));
		given(attributeDescriptorModel.getDeclaringEnclosingType()).willReturn(mock(ComposedTypeModel.class));

		final String isoCode = "isoCode";
		final String displayName = "name[qualifier]*";
		final String formattedAttributeDisplayName = "formattedAttributeDisplayName";

		final LanguageModel languageModel = mock(LanguageModel.class);
		given(languageModel.getActive()).willReturn(true);
		given(languageModel.getIsocode()).willReturn(isoCode);

		given(mockedAttributeNameFormatter.format(any())).willReturn(displayName);
		given(mockedCommonI18NService.getAllLanguages()).willReturn(Collections.singletonList(languageModel));
		given(mockedExcelTranslatorRegistry.getTranslator(attributeDescriptorModel)).willReturn(Optional.empty());
		given(mockedCollectionFormatter.formatToString(Collections.singletonList(displayName)))
				.willReturn(formattedAttributeDisplayName);

		// when
		final TypeSystemRow typeSystemRow = typeSystemRowFactory.create(attributeDescriptorModel);

		// then
		assertThat(typeSystemRow.getAttrDisplayName()).isEqualTo(formattedAttributeDisplayName);
	}

	@Test
	public void shouldCreateTypeSystemWithReferenceFormat()
	{
		// given
		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getAttributeType()).willReturn(mock(TypeModel.class));
		given(attributeDescriptorModel.getEnclosingType()).willReturn(mock(ComposedTypeModel.class));
		given(attributeDescriptorModel.getDeclaringEnclosingType()).willReturn(mock(ComposedTypeModel.class));

		final ExcelValueTranslator excelValueTranslator = mock(ExcelValueTranslator.class);
		given(excelValueTranslator.referenceFormat(attributeDescriptorModel)).willReturn("referenceFormat");

		given(mockedCommonI18NService.getAllLanguages()).willReturn(Collections.emptyList());
		given(mockedExcelTranslatorRegistry.getTranslator(attributeDescriptorModel)).willReturn(Optional.of(excelValueTranslator));

		// when
		final TypeSystemRow typeSystemRow = typeSystemRowFactory.create(attributeDescriptorModel);

		// then
		assertThat(typeSystemRow.getAttrReferenceFormat()).isEqualTo("referenceFormat");
	}

	@Test
	public void shouldMergeTwoTypeSystemRows()
	{
		// given
		final TypeSystemRow typeSystemRow1 = createTypeSystemRow("{typeCode}", "typeName", "attrQualifier", "attrName",
				"attrTypeCode", "attrTypeItemType", "attrLocLang", "attrDisplayName", "attrReferenceFormat");
		final TypeSystemRow typeSystemRow2 = createTypeSystemRow("{typeCode2}", "typeName2", "attrQualifier2", "attrName2",
				"attrTypeCode2", "attrTypeItemType2", "attrLocLang2", "attrDisplayName2", "attrReferenceFormat2");

		// when
		final TypeSystemRow result = typeSystemRowFactory.merge(typeSystemRow1, typeSystemRow2);

		// then
		assertThat(result.getTypeCode()).isEqualTo("{typeCode},{typeCode2}");
		assertThat(result.getTypeName()).isEqualTo("typeName");
		assertThat(result.getAttrQualifier()).isEqualTo("attrQualifier");
		assertThat(result.getAttrName()).isEqualTo("attrName");
		assertThat(result.getAttrOptional()).isEqualTo(true);
		assertThat(result.getAttrTypeCode()).isEqualTo("attrTypeCode");
		assertThat(result.getAttrTypeItemType()).isEqualTo("attrTypeItemType");
		assertThat(result.getAttrLocalized()).isEqualTo(true);
		assertThat(result.getAttrLocLang()).isEqualTo("attrLocLang");
		assertThat(result.getAttrDisplayName()).isEqualTo("attrDisplayName");
		assertThat(result.getAttrUnique()).isEqualTo(true);
		assertThat(result.getAttrReferenceFormat()).isEqualTo("attrReferenceFormat");
	}

	private static TypeSystemRow createTypeSystemRow(final String typeCode, final String typeName, final String attrQualifier,
			final String attrName, final String attrTypeCode, final String attrTypeItemType, final String attrLocLang,
			final String attrDisplayName, final String attrReferenceFormat)
	{
		final TypeSystemRow typeSystemRow = new TypeSystemRow();
		typeSystemRow.setTypeCode(typeCode);
		typeSystemRow.setTypeName(typeName);
		typeSystemRow.setAttrQualifier(attrQualifier);
		typeSystemRow.setAttrName(attrName);
		typeSystemRow.setAttrOptional(true);
		typeSystemRow.setAttrTypeCode(attrTypeCode);
		typeSystemRow.setAttrTypeItemType(attrTypeItemType);
		typeSystemRow.setAttrLocalized(true);
		typeSystemRow.setAttrLocLang(attrLocLang);
		typeSystemRow.setAttrDisplayName(attrDisplayName);
		typeSystemRow.setAttrUnique(true);
		typeSystemRow.setAttrReferenceFormat(attrReferenceFormat);
		return typeSystemRow;
	}
}
