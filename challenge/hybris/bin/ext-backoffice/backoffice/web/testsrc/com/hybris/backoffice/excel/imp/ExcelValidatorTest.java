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
package com.hybris.backoffice.excel.imp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Sets;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.editor.defaultfileupload.FileUploadResult;


@RunWith(Parameterized.class)
public class ExcelValidatorTest
{
	@InjectMocks
	private ExcelValidator validator;

	@Mock
	private NotificationService notificationService;

	private final Set<FileUploadResult> results = IntStream.range(0, 4).boxed().map(idx -> {
		final FileUploadResult result = new FileUploadResult();
		result.setName("abc" + idx);
		return result;
	}).collect(Collectors.toSet());

	@Parameterized.Parameter(0)
	public FileUploadResult input;

	@Parameterized.Parameter(1)
	public Boolean formatOutput;

	@Parameterized.Parameter(2)
	public Boolean nameOutput;

	@Parameterized.Parameters
	public static Collection<Object[]> data()
	{
		final FileUploadResult fileUploadResult1 = new FileUploadResult();
		fileUploadResult1.setFormat("pdf");
		fileUploadResult1.setName("abc1");
		final FileUploadResult fileUploadResult2 = new FileUploadResult();
		fileUploadResult2.setFormat("PDF");
		fileUploadResult2.setName("bcx");
		final FileUploadResult fileUploadResult3 = new FileUploadResult();
		fileUploadResult3.setFormat("JPG");
		fileUploadResult3.setName("bcxyty");
		final FileUploadResult fileUploadResult4 = new FileUploadResult();
		fileUploadResult4.setFormat("jpg");
		fileUploadResult4.setName("lkjlkji");
		final FileUploadResult fileUploadResult5 = new FileUploadResult();
		fileUploadResult5.setFormat("PNG");
		fileUploadResult5.setName("xnsndfn");
		final FileUploadResult fileUploadResult6 = new FileUploadResult();
		fileUploadResult6.setFormat("png");
		fileUploadResult6.setName("abc2");

		return Arrays.asList(new Object[][]
		{
				{ fileUploadResult1, true, true },
				{ fileUploadResult2, true, false },
				{ fileUploadResult3, true, false },
				{ fileUploadResult4, true, false },
				{ fileUploadResult5, false, false },
				{ fileUploadResult6, false, true } });
	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		validator.setFormats(Sets.newHashSet("PDF", "jpg"));
	}

	@Test
	public void testFileFormatValidator()
	{
		// when
		final boolean result = validator.isCorrectFormat(input);

		// then
		assertThat(result).isEqualTo(formatOutput);
	}

	@Test
	public void testFileNameValidator()
	{
		// when
		final boolean result = validator.fileAlreadyExists(results, input);

		// then
		assertThat(result).isEqualTo(nameOutput);
	}

}
