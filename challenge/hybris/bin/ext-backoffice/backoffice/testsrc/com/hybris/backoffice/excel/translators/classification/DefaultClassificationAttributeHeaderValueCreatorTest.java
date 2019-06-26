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
package com.hybris.backoffice.excel.translators.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.importing.ExcelImportContext;
import com.hybris.backoffice.excel.translators.generic.RequiredAttribute;
import com.hybris.backoffice.excel.translators.generic.factory.RequiredAttributesFactory;


@RunWith(MockitoJUnitRunner.class)
public class DefaultClassificationAttributeHeaderValueCreatorTest
{
	@Mock
	RequiredAttributesFactory mockedRequiredAttributesFactory;
	@InjectMocks
	ClassificationAttributeHeaderValueCreator classificationAttributeHeaderValueCreator = new DefaultClassificationAttributeHeaderValueCreator();

	@Test
	public void shouldCreateClassificationAttributeHeaderValue()
	{
		// given
		final ClassAttributeAssignmentModel classAttributeAssignmentModel = prepareAssignment();

		final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
		attribute.setName("name");
		attribute.setIsoCode("isoCode");
		attribute.setAttributeAssignment(classAttributeAssignmentModel);

		// when
		final String result = classificationAttributeHeaderValueCreator.create(attribute, new ExcelImportContext());

		// then
		assertThat(result).isEqualTo(
				"@column[system='ClassificationSystem',version='ClassificationVersion',translator=de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator]");
	}

	@Test
	public void shouldCreateClassificationAttributeHeaderValueWithReferences()
	{
		// given
		final ComposedTypeModel referenceType = mock(ComposedTypeModel.class);
		final RequiredAttribute requiredAttribute = prepareRequiredAttribute(StringUtils.EMPTY,
				prepareRequiredAttribute("catalog", prepareRequiredAttribute("id"), prepareRequiredAttribute("version")));

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = prepareAssignment();
		given(classAttributeAssignmentModel.getReferenceType()).willReturn(referenceType);

		given(mockedRequiredAttributesFactory.create(referenceType)).willReturn(requiredAttribute);

		final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
		attribute.setName("name");
		attribute.setIsoCode("isoCode");
		attribute.setAttributeAssignment(classAttributeAssignmentModel);

		// when
		final String result = classificationAttributeHeaderValueCreator.create(attribute, new ExcelImportContext());

		// then
		assertThat(result).isEqualTo(
				"@column(catalog(id,version))[system='ClassificationSystem',version='ClassificationVersion',translator=de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator]");
	}

	private RequiredAttribute prepareRequiredAttribute(final String qualifier, final RequiredAttribute... children)
	{
		final RequiredAttribute requiredAttribute = mock(RequiredAttribute.class);
		given(requiredAttribute.getQualifier()).willReturn(qualifier);
		given(requiredAttribute.getChildren()).willReturn(Arrays.asList(children));
		return requiredAttribute;
	}

	private ClassAttributeAssignmentModel prepareAssignment()
	{
		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		given(classificationAttributeModel.getCode()).willReturn("column");

		final ClassificationSystemModel classificationSystemModel = mock(ClassificationSystemModel.class);
		given(classificationSystemModel.getId()).willReturn("ClassificationSystem");

		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		given(classificationSystemVersionModel.getCatalog()).willReturn(classificationSystemModel);
		given(classificationSystemVersionModel.getVersion()).willReturn("ClassificationVersion");

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignmentModel.getClassificationAttribute()).willReturn(classificationAttributeModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(classificationSystemVersionModel);
		return classAttributeAssignmentModel;
	}
}
