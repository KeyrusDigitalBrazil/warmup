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
package com.hybris.backoffice.excel.importing.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.FeatureValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.importing.ExcelImportContext;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.translators.classification.ClassificationAttributeHeaderValueCreator;
import com.hybris.backoffice.excel.translators.classification.ExcelClassificationJavaTypeTranslator;
import com.hybris.backoffice.excel.util.ExcelDateUtils;


@RunWith(MockitoJUnitRunner.class)
public class AbstractClassificationRangeTranslatorTest
{

	@Mock
	private ClassificationAttributeHeaderValueCreator creator;

	@Mock
	private ExcelDateUtils excelDateUtils;

	@InjectMocks
	@Spy
	private ExcelClassificationJavaTypeTranslator translator = new ExcelClassificationJavaTypeTranslator();

	@Test
	public void shouldExportRangeValues()
	{
		// given
		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getRange()).willReturn(true);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);

		final String fromValue = "from";
		final String toValue = "to";

		final FeatureValue fromFeatureValue = mockFeatureValue(fromValue);
		final FeatureValue toFeatureValue = mockFeatureValue(toValue);

		// when
		final Optional<String> output = translator.exportRange(attribute,
				Lists.newArrayList(ImmutablePair.of(fromFeatureValue, toFeatureValue)));

		// then
		assertThat(output.isPresent()).isTrue();
		assertThat(output.get()).isEqualTo(RangeParserUtils.RANGE_PREFIX + fromValue + RangeParserUtils.RANGE_DELIMITER + toValue
				+ RangeParserUtils.RANGE_SUFFIX);
	}

	@Test
	public void shouldImportRangeValues()
	{
		// given
		given(creator.create(any(), any())).willReturn(StringUtils.EMPTY);

		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getRange()).willReturn(true);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);

		final String left = "value1";
		final String right = "value2";
		final String cellValue = RangeParserUtils.RANGE_PREFIX + RangeParserUtils.RANGE_DELIMITER + right
				+ RangeParserUtils.RANGE_SUFFIX;
		final ImportParameters importParameters = new ImportParameters(null, null, cellValue, null, createParams(left, right));

		// when
		final Impex impex = translator.importData(attribute, importParameters, new ExcelImportContext());

		// then
		assertThat(impex.getImpexes().get(0).getImpexTable().row(0).values())
				.containsExactly(left + ExcelTemplateConstants.MULTI_VALUE_DELIMITER + right);
	}

	@Test
	public void shouldRangeImportParametersBeSplitInMultiValueCase()
	{
		// given
		given(creator.create(any(), any())).willReturn(StringUtils.EMPTY);

		final ExcelClassificationAttribute attribute = mock(ExcelClassificationAttribute.class);
		final ClassAttributeAssignmentModel assignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(assignmentModel.getRange()).willReturn(true);
		given(assignmentModel.getAttributeType()).willReturn(ClassificationAttributeTypeEnum.NUMBER);
		given(attribute.getAttributeAssignment()).willReturn(assignmentModel);

		final Map<String, String> from1 = new HashMap<>();
		from1.put(RangeParserUtils.prependFromPrefix(ImportParameters.RAW_VALUE), "val");
		final Map<String, String> from2 = new HashMap<>();
		from2.put(RangeParserUtils.prependFromPrefix(ImportParameters.RAW_VALUE), "val");

		final Map<String, String> to1 = new HashMap<>();
		to1.put(RangeParserUtils.prependToPrefix(ImportParameters.RAW_VALUE), "val");
		final Map<String, String> to2 = new HashMap<>();
		to2.put(RangeParserUtils.prependToPrefix(ImportParameters.RAW_VALUE), "val");

		final List<Map<String, String>> params = Lists.newArrayList(from1, from2, to1, to2);
		final String range1 = "RANGE[val;val]";
		final String range2 = "RANGE[val;val]";
		final String cellValue = range1 + ExcelTemplateConstants.MULTI_VALUE_DELIMITER + range2;

		final ImportParameters importParameters = new ImportParameters(null, null, cellValue, null, params);

		// when
		final Impex impex = translator.importData(attribute, importParameters, new ExcelImportContext());

		// then
		then(translator).should(times(4)).importSingle(any(), any(), any());
		assertThat(impex.getImpexes().get(0).getImpexTable().cellSet()).hasSize(1) //
				.hasOnlyOneElementSatisfying(cell -> {
					assertThat(cell.getValue()).isEqualTo("val,val,val,val");
				});
	}

	private FeatureValue mockFeatureValue(final String value)
	{
		final FeatureValue featureValue = mock(FeatureValue.class);
		given(featureValue.getValue()).willReturn(value);
		return featureValue;
	}

	private List<Map<String, String>> createParams(final String left, final String right)
	{
		final Map<String, String> fromParams = new LinkedHashMap<>();
		fromParams.put(RangeParserUtils.prependFromPrefix("someValue1"), left);
		fromParams.put(RangeParserUtils.prependFromPrefix(ImportParameters.RAW_VALUE), left);

		final Map<String, String> toParams = new LinkedHashMap<>();
		toParams.put(RangeParserUtils.prependToPrefix("someValue2"), right);
		toParams.put(RangeParserUtils.prependToPrefix(ImportParameters.RAW_VALUE), right);

		return Arrays.asList(fromParams, toParams);
	}

}
