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
package com.hybris.backoffice.excel.template.populator.extractor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import org.junit.Test;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;


public class ClassificationFullNameExtractorTest
{
	ClassificationFullNameExtractor extractor = new ClassificationFullNameExtractor();

	@Test
	public void shouldFormatNonLocalizedName()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo("class.attribute - systemId/systemVersion");
	}

	@Test
	public void shouldFormatLocalizedName()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();
		given(excelAttribute.isLocalized()).willReturn(true);
		given(excelAttribute.getIsoCode()).willReturn("lang");

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo("class.attribute[lang] - systemId/systemVersion");
	}

	@Test
	public void shouldFormatWithoutClassificationClassName()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();
		given(excelAttribute.getAttributeAssignment().getClassificationClass().getCode()).willReturn(null);
		given(excelAttribute.isLocalized()).willReturn(true);
		given(excelAttribute.getIsoCode()).willReturn("lang");

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo(".attribute[lang] - systemId/systemVersion");
	}

	@Test
	public void shouldFormatWithoutAttributeName()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();
		given(excelAttribute.getAttributeAssignment().getClassificationAttribute().getCode()).willReturn(null);

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo("class. - systemId/systemVersion");
	}

	@Test
	public void shouldFormatWithoutSystemVersion()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();
		given(excelAttribute.getAttributeAssignment().getSystemVersion().getVersion()).willReturn(null);

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo("class.attribute - systemId/");
	}

	@Test
	public void shouldFormatWithoutSystemId()
	{
		// given
		final ExcelClassificationAttribute excelAttribute = prepareExcelClassificationAttribute();
		given(excelAttribute.getAttributeAssignment().getSystemVersion().getCatalog().getId()).willReturn(null);

		// when
		final String result = extractor.extract(excelAttribute);

		// then
		assertThat(result).isEqualTo("class.attribute - /systemVersion");
	}

	private ExcelClassificationAttribute prepareExcelClassificationAttribute()
	{
		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);
		given(classificationSystemModel.getId()).willReturn("systemId");

		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		given(classificationSystemVersionModel.getVersion()).willReturn("systemVersion");
		given(classificationSystemVersionModel.getCatalog()).willReturn(classificationSystemModel);

		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		given(classificationAttributeModel.getSystemVersion()).willReturn(classificationSystemVersionModel);
		given(classificationAttributeModel.getCode()).willReturn("attribute");

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		given(classificationClassModel.getCode()).willReturn("class");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignmentModel.getClassificationAttribute()).willReturn(classificationAttributeModel);
		given(classAttributeAssignmentModel.getClassificationClass()).willReturn(classificationClassModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(classificationSystemVersionModel);

		final ExcelClassificationAttribute excelAttribute = mock(ExcelClassificationAttribute.class);
		given(excelAttribute.getAttributeAssignment()).willReturn(classAttributeAssignmentModel);

		return excelAttribute;
	}
}
