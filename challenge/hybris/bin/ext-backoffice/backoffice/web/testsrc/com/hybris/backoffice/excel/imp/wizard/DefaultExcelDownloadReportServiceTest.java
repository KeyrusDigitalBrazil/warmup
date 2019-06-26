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
package com.hybris.backoffice.excel.imp.wizard;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

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
import org.mockito.ArgumentMatcher;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;
import com.hybris.backoffice.excel.validators.data.ValidationMessage;


@RunWith(Parameterized.class)
public class DefaultExcelDownloadReportServiceTest
{

	private static final String LINE_SEPARATOR = System.lineSeparator();
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Parameterized.Parameters
	public static Collection<Object[]> data()
	{
		final BiFunction<String, String[], Object[]> preparer = (header, msgs) -> {
			final ValidationMessage validationHeader = new ValidationMessage(header);
			final List<ValidationMessage> validationMessages = Stream.of(msgs).map(ValidationMessage::new)
					.collect(Collectors.toList());
			final ExcelValidationResult excelValidationResult = new ExcelValidationResult(validationHeader, validationMessages);

			final String result = Stream.of(msgs)
					.collect(Collectors.joining(LINE_SEPARATOR, header + LINE_SEPARATOR, LINE_SEPARATOR + LINE_SEPARATOR));
			return new Object[]
			{ excelValidationResult, result };
		};

		final Collection<Pair<String, String[]>> data = Lists.newArrayList(//

				/*
				 * Case 1: Header1\r\nMessage1\r\nMessage2\r\nMessage3\r\n\r\n
				 */
				ImmutablePair.of("Header1", new String[]
				{ "Message1", "Message2", "Message3" }), //

				/*
				 * Case 2: Header1\r\nMessage1\r\nMessage2\r\n\r\n
				 */
				ImmutablePair.of("Header2", new String[]
				{ "Message1", "Message2", }), //

				/*
				 * Case 3: Header3\r\nMessage1\r\n\r\n
				 */
				ImmutablePair.of("Header3", new String[]
				{ "Message1", }));

		return data.stream().map(element -> preparer.apply(element.getLeft(), element.getRight())).collect(Collectors.toList());
	}

	@Parameterized.Parameter(0)
	public ExcelValidationResult input;

	@Parameterized.Parameter(1)
	public String output;

	@Spy
	private DefaultExcelDownloadReportService excelDownloadReportService = new DefaultExcelDownloadReportService();

	@Before
	public void setUp()
	{
		doNothing().when(excelDownloadReportService).triggerDownload(any());
		doAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]).when(excelDownloadReportService).getLabel(any(),
				anyVararg());
	}

	@Test
	public void shouldGenerateProperContent()
	{
		// when
		excelDownloadReportService.downloadReport(Lists.newArrayList(input));

		// then
		verify(excelDownloadReportService).triggerDownload(argThat(new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o.equals(output);
			}
		}));
	}

}
