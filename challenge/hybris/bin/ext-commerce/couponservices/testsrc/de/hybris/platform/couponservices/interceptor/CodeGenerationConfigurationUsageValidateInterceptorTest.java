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
package de.hybris.platform.couponservices.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CodeGenerationConfigurationUsageValidateInterceptorTest
{
	public static final String EXCEPTION_MESSAGE = "exception message";
	public static final String EXCEPTION_MESSAGE_KEY = "exception.codegenerationconfigurationusagevalidateinterceptor.cannot.delete";
	public static final String COUPON_NAME = "test";
	public static final String COUPON_ID = "test-coupon-id";
	@InjectMocks
	private RemoveInterceptor<CodeGenerationConfigurationModel> validator = new CodeGenerationConfigurationUsageValidateInterceptor();
	@Mock
	private L10NService l10nService;
	@Mock
	private CouponDao couponDao;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private CodeGenerationConfigurationModel modelToDelete;
	@Mock
	private MultiCodeCouponModel multiCodeCoupon;
	@Mock
	private MultiCodeCouponModel multiCodeCouponWithoutName;
	@Captor
	private ArgumentCaptor<Object[]> translationArgs;

	@Before
	public void setUp() throws Exception
	{
		given(multiCodeCoupon.getName()).willReturn("test");
		given(multiCodeCouponWithoutName.getCouponId()).willReturn("test-coupon-id");

	}

	@Test
	public void shouldRaiseExceptionIfCodeGenerationConfigurationIsUsedByMultiCodeCoupon() throws Exception
	{
		//given
		given(couponDao.findMultiCodeCouponsByCodeConfiguration(modelToDelete)).willReturn(newArrayList(multiCodeCoupon));
		//when
		final Throwable throwable = catchThrowable(() -> validator.onRemove(modelToDelete, null));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class);
	}

	@Test
	public void shouldTranslateExceptionMessage() throws Exception
	{
		//given
		given(couponDao.findMultiCodeCouponsByCodeConfiguration(modelToDelete)).willReturn(newArrayList(multiCodeCoupon));
		given(l10nService.getLocalizedString(eq(EXCEPTION_MESSAGE_KEY), anyVararg())).willReturn(EXCEPTION_MESSAGE);
		//when
		final Throwable throwable = catchThrowable(() -> validator.onRemove(modelToDelete, null));
		//then
		assertThat(throwable.getMessage()).endsWith(EXCEPTION_MESSAGE);
	}

	@Test
	public void shouldBeRemovedSuccessfullyIfCodeGenerationConfigurationIsNotUsedByAnyMultiCodeCoupon() throws Exception
	{
		//given
		given(couponDao.findMultiCodeCouponsByCodeConfiguration(modelToDelete)).willReturn(newArrayList());
		//when
		final Throwable throwable = catchThrowable(() -> validator.onRemove(modelToDelete, null));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void shouldFillTranslatedMessagesUsingCouponNameOrCouponIdAsFallback() throws Exception
	{
		//given
		given(couponDao.findMultiCodeCouponsByCodeConfiguration(modelToDelete))
				.willReturn(newArrayList(multiCodeCoupon, multiCodeCouponWithoutName));
		//when
		catchThrowable(() -> validator.onRemove(modelToDelete, null));
		//then
		verify(l10nService).getLocalizedString(eq(EXCEPTION_MESSAGE_KEY), translationArgs.capture());
		final Object[] values = translationArgs.getValue();
		assertThat((String) values[0]).contains(COUPON_NAME).contains(COUPON_ID);
	}
}
