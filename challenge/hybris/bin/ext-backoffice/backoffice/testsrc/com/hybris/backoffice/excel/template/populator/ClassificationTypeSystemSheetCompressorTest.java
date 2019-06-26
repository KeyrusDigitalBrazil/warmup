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
package com.hybris.backoffice.excel.template.populator;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.ATTRIBUTE_LOCALIZED;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.ATTRIBUTE_LOC_LANG;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.CLASSIFICATION_ATTRIBUTE;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.CLASSIFICATION_CLASS;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.CLASSIFICATION_SYSTEM;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.CLASSIFICATION_VERSION;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.FULL_NAME;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.MANDATORY;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.template.CollectionFormatter;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationTypeSystemSheetCompressorTest
{
	@Mock
	CollectionFormatter mockedCollectionFormatter;
	@InjectMocks
	ClassificationTypeSystemSheetCompressor compressor;

	@Test
	public void shouldCompressClassificationRow()
	{
		// given
		final Collection<Map<ClassificationTypeSystemColumns, String>> rows = Arrays
				.asList(new HashMap<ClassificationTypeSystemColumns, String>()
				{
					{
						put(FULL_NAME, "fullName1");
						put(CLASSIFICATION_SYSTEM, "classificationSystem");
						put(CLASSIFICATION_VERSION, "classificationVersion");
						put(CLASSIFICATION_CLASS, "classificationClass");
						put(CLASSIFICATION_ATTRIBUTE, "classificationAttribute");
						put(ATTRIBUTE_LOCALIZED, "true");
						put(ATTRIBUTE_LOC_LANG, "en");
						put(MANDATORY, "true");
					}
				}, new HashMap<ClassificationTypeSystemColumns, String>()
				{
					{
						put(FULL_NAME, "fullName2");
						put(CLASSIFICATION_SYSTEM, "classificationSystem");
						put(CLASSIFICATION_VERSION, "classificationVersion");
						put(CLASSIFICATION_CLASS, "classificationClass");
						put(CLASSIFICATION_ATTRIBUTE, "classificationAttribute");
						put(ATTRIBUTE_LOCALIZED, "true");
						put(ATTRIBUTE_LOC_LANG, "de");
						put(MANDATORY, "true");
					}
				});
		given(mockedCollectionFormatter.formatToString("en", "de")).willReturn("{en},{de}");
		given(mockedCollectionFormatter.formatToString("fullName1", "fullName2")).willReturn("{fullName1},{fullName2}");

		// when
		final Collection<Map<ClassificationTypeSystemColumns, String>> compressionResult = compressor.compress(rows);

		// then
		assertThat(compressionResult).hasSize(1);
		final Map<ClassificationTypeSystemColumns, String> firstRow = compressionResult.iterator().next();
		assertThat(firstRow).includes(entry(FULL_NAME, "{fullName1},{fullName2}"));
		assertThat(firstRow).includes(entry(CLASSIFICATION_SYSTEM, "classificationSystem"));
		assertThat(firstRow).includes(entry(CLASSIFICATION_VERSION, "classificationVersion"));
		assertThat(firstRow).includes(entry(CLASSIFICATION_CLASS, "classificationClass"));
		assertThat(firstRow).includes(entry(CLASSIFICATION_ATTRIBUTE, "classificationAttribute"));
		assertThat(firstRow).includes(entry(ATTRIBUTE_LOCALIZED, "true"));
		assertThat(firstRow).includes(entry(ATTRIBUTE_LOC_LANG, "{en},{de}"));
		assertThat(firstRow).includes(entry(MANDATORY, "true"));
	}
}
