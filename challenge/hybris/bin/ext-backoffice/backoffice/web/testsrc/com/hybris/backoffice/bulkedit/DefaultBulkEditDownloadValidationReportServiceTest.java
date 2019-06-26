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
package com.hybris.backoffice.bulkedit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.validation.impl.DefaultValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationInfo;


@RunWith(Parameterized.class)
public class DefaultBulkEditDownloadValidationReportServiceTest
{

	private static final String LINE_SEPARATOR = System.lineSeparator();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private LabelService labelService;

	@Parameterized.Parameters
	public static Collection<Object[]> data()
	{
		final BiFunction<String, String[], Object[]> preparer = (header, msgs) -> {
			final ProductModel product = mock(ProductModel.class);
			when(product.getCode()).thenReturn(header);
			final List<ValidationInfo> validationInfos = Stream.of(msgs).map(msg -> {
				final DefaultValidationInfo info = new DefaultValidationInfo();
				info.setValidationMessage(msg);
				return info;
			}).collect(Collectors.toList());
			final ValidationResult validationResult = new ValidationResult(product, validationInfos);

			final String result = Stream.of(msgs).collect(
					Collectors.joining(LINE_SEPARATOR + "\t", header + LINE_SEPARATOR + "\t", LINE_SEPARATOR + LINE_SEPARATOR));
			return new Object[]
			{ Lists.newArrayList(validationResult), result };
		};

		final Collection<Pair<String, String[]>> data = Lists.newArrayList(//

				/*
				 * Case 1: Header1\r\n\tMessage1\r\n\tMessage2\r\n\tMessage3\r\n\r\n
				 */
				ImmutablePair.of("Header1", new String[]
				{ "Message1", "Message2", "Message3" }), //

				/*
				 * Case 2: Header1\r\n\tMessage1\r\n\tMessage2\r\n\r\n
				 */
				ImmutablePair.of("Header2", new String[]
				{ "Message1", "Message2", }), //

				/*
				 * Case 3: Header3\r\n\tMessage1\r\n\r\n
				 */
				ImmutablePair.of("Header3", new String[]
				{ "Message1", }));

		return data.stream().map(element -> preparer.apply(element.getLeft(), element.getRight())).collect(Collectors.toList());
	}

	@Parameterized.Parameter(0)
	public List<ValidationResult> input;

	@Parameterized.Parameter(1)
	public String output;

	@Spy
	@InjectMocks
	private DefaultBulkEditDownloadValidationReportService bulkEditDownloadValidationReportService;

	@Before
	public void setUp()
	{
		doNothing().when(bulkEditDownloadValidationReportService).triggerDownload(any());
		doAnswer(invocationOnMock -> {
			final ProductModel product = (ProductModel) invocationOnMock.getArguments()[0];
			return product.getCode();
		}).when(labelService).getShortObjectLabel(any());
	}

	@Test
	public void shouldGenerateProperContent()
	{
		// when
		bulkEditDownloadValidationReportService.downloadReport(input);

		// then
		verify(bulkEditDownloadValidationReportService).triggerDownload(output);
	}

}
