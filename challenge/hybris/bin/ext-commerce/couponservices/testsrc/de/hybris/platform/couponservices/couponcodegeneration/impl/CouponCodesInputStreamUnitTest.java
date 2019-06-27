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
package de.hybris.platform.couponservices.couponcodegeneration.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.couponcodegeneration.CouponCodesGenerator;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;

import java.io.InputStream;
import java.util.StringJoiner;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CouponCodesInputStreamUnitTest
{

	private static final String SINGLE_SAMPLE_CODE = "CODE";

	private CouponCodesInputStream couponCodesInputStream;
	@Mock
	private MultiCodeCouponModel multiCodeCouponModel;
	@Mock
	private CouponCodesGenerator couponCodesGenerator;

	@Before
	public void setUp()
	{
		couponCodesInputStream = new CouponCodesInputStream(multiCodeCouponModel, couponCodesGenerator, 10, 95);
		when(couponCodesGenerator.generateNextCouponCode(multiCodeCouponModel)).thenReturn(SINGLE_SAMPLE_CODE);
	}

	@Test
	public void testRead() throws Exception
	{
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();)
		{

			int byteValue = couponCodesInputStream.read();
			while (byteValue != -1)
			{
				baos.write(byteValue);
				byteValue = couponCodesInputStream.read();
			}
			final String codes = new String(baos.toByteArray(), "UTF-8");
			assertThat(codes).isNotEmpty().isEqualTo(formSampleCouponCodes());
		}
	}

	@Test
	public void testAvailable() throws Exception
	{
		couponCodesInputStream.read();
		int numOfCalls = 1;
		while(couponCodesInputStream.available() > 0)
		{
			couponCodesInputStream.read();
			numOfCalls++;
		}
		assertThat(couponCodesInputStream.available()).isEqualTo(0);
		assertThat(numOfCalls).isEqualTo(formSampleCouponCodes().getBytes().length);
	}

	@Test
	public void testInputStream() throws Exception
	{
		final InputStream is = IOUtils.toBufferedInputStream(couponCodesInputStream);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		IOUtils.closeQuietly(is);
		final String codes = new String(baos.toByteArray(), "UTF-8");
		assertThat(codes).isNotEmpty().isEqualTo(formSampleCouponCodes());
	}

	private String formSampleCouponCodes()
	{
		final StringJoiner stringJoiner = new StringJoiner("\n");
		for(int i=0; i< 95; i++)
		{
			stringJoiner.add(SINGLE_SAMPLE_CODE);
		}
		return stringJoiner.toString();
	}

}
