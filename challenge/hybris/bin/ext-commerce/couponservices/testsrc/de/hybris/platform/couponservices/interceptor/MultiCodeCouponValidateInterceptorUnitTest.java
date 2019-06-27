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

import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_PREFIX_REGEX_DEFAULT_VALUE;
import static de.hybris.platform.couponservices.constants.CouponServicesConstants.COUPON_CODE_GENERATION_PREFIX_REGEX_PROPERTY;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MultiCodeCouponValidateInterceptorUnitTest
{

	private static final String COUPON_ID = "testCouponId123";
	private static final String CODE_SEPARATOR = "-";
	private static final String MEDIA_MODEL_CODE = "mmTestCode";
	private static final String CODE_GENERATION_CONFIGURATION_NAME = "TEST_CONFIG";
	private static final String CODE_GENERATION_CONFIGURATION_NAME_ORIGINAL = "TEST_CONFIG_NEW";

	private MultiCodeCouponValidateInterceptor validator;
	@Mock
	private InterceptorContext ctx;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	@Before
	public void setUp() throws Exception
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString(COUPON_CODE_GENERATION_PREFIX_REGEX_PROPERTY,
				COUPON_CODE_GENERATION_PREFIX_REGEX_DEFAULT_VALUE)).thenReturn(COUPON_CODE_GENERATION_PREFIX_REGEX_DEFAULT_VALUE);

		validator = new MultiCodeCouponValidateInterceptor();
		validator.setConfigurationService(configurationService);
		validator.afterPropertiesSet();

		setCouponIdModified(FALSE);
		setCodeGenerationConfigModified(FALSE);
		setCodeGeneratedCodesModified(FALSE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOnValidateModelIsNull() throws InterceptorException
	{
		validator.onValidate(null, ctx);
	}

	@Test
	public void testOnValidateGeneratedCodeIsNull() throws InterceptorException
	{
		final MultiCodeCouponModel model = getMultiCodeCouponModel(TRUE);
		model.setGeneratedCodes(null);
		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, TRUE);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		validator.onValidate(model, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateActiveGeneratedCodesEmptyAndCouponIdIsModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(TRUE);
		setCouponIdModified(TRUE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);

		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, TRUE);

		validator.onValidate(couponModel, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateNonActiveGeneratedCodesPresentAndCouponIdIsModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		setCouponIdModified(TRUE);
		final Collection<MediaModel> generatedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1));
		couponModel.setGeneratedCodes(generatedCodes);

		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateNonActiveGeneratedCodesEmptyAndCouponIdIsModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		setCouponIdModified(TRUE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);

		validator.onValidate(couponModel, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateActiveGeneratedCodesEmptyAndCodeGenerationConfigModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(TRUE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGenerationConfigModified(TRUE);

		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, TRUE);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME_ORIGINAL));

		validator.onValidate(couponModel, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateNonActiveGeneratedCodesPresentAndCodeGenerationConfigModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		final Collection<MediaModel> generatedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1));
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGenerationConfigModified(TRUE);
		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, TRUE);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME_ORIGINAL));
		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateNonActiveGeneratedCodesEmptyAndCodeGenerationConfigModified() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGenerationConfigModified(TRUE);

		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateActiveGeneratedCodesEmptyAndCodeGeneratCodesModifiedEmptyCodes() throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(TRUE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGeneratedCodesModified(TRUE);
		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, TRUE);
		loadOriginalValueForGeneratedCodes(validator, Collections.EMPTY_LIST);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		validator.onValidate(couponModel, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateActiveGeneratedCodesEmptyAndCodeGeneratCodesModifiedNonEmptyOriginalCodes()
			throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(TRUE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGeneratedCodesModified(TRUE);

		validator = spy(validator);
		loadOriginalValueForGeneratedCodes(validator,
				Arrays.asList(getMediaModel(MEDIA_MODEL_CODE), getMediaModel(MEDIA_MODEL_CODE + 1)));
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		loadOriginalValueForCouponActive(validator, TRUE);

		validator.onValidate(couponModel, ctx);
	}

	@Test(expected = CouponInterceptorException.class)
	public void testOnValidateNonActiveGeneratedCodesEmptyAndCodeGeneratCodesModifiedNonEmptyOriginalCodes()
			throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGeneratedCodesModified(TRUE);
		validator = spy(validator);
		loadOriginalValueForGeneratedCodes(validator,
				Arrays.asList(getMediaModel(MEDIA_MODEL_CODE), getMediaModel(MEDIA_MODEL_CODE + 1)));

		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateNonActiveGeneratedCodesPresentAndCodeGeneratedCodesModifiedNonEmptyGenerated()
			throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		final Collection<MediaModel> generatedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1));
		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGeneratedCodesModified(TRUE);
		validator = spy(validator);
		loadOriginalValueForGeneratedCodes(validator, Collections.EMPTY_LIST);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateNonActiveGeneratedCodesEmptyAndCodeGeneratedCodesModifiedConsistentCodeCollections()
			throws InterceptorException
	{
		final MultiCodeCouponModel couponModel = getMultiCodeCouponModel(FALSE);
		final Collection<MediaModel> generatedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1), getMediaModel(MEDIA_MODEL_CODE + 2));
		final Collection<MediaModel> originalGeneratedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1));

		couponModel.setGeneratedCodes(generatedCodes);
		setCodeGeneratedCodesModified(TRUE);
		validator = spy(validator);
		loadOriginalValueForGeneratedCodes(validator, originalGeneratedCodes);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		validator.onValidate(couponModel, ctx);
	}

	@Test
	public void testOnValidateTrue() throws InterceptorException
	{
		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, FALSE);
		loadOriginalValueForCodeGenerationConfiguration(validator,
				getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME));
		validator.onValidate(getMultiCodeCouponModel(TRUE), ctx);
	}

	@Test
	public void testValidateSeedNumberWasNull() throws InterceptorException
	{
		testSeedNumberChange(null, Long.valueOf(1L), null);
	}

	@Test
	public void testValidateSeedNumberIsEqual() throws InterceptorException
	{
		testSeedNumberChange(Long.valueOf(1L), Long.valueOf(1L), null);
	}

	@Test
	public void testValidateSeedNumberIsIncreased() throws InterceptorException
	{
		testSeedNumberChange(Long.valueOf(1L), Long.valueOf(2L), null);
	}

	@Test
	public void testValidateSeedNumberIsDecreased() throws InterceptorException
	{
		testSeedNumberChange(Long.valueOf(2L), Long.valueOf(1L), CouponInterceptorException.class);
	}

	@Test
	public void testValidateSeedNumberWriteNull() throws InterceptorException
	{
		testSeedNumberChange(Long.valueOf(2L), null, CouponInterceptorException.class);
	}

	private void testSeedNumberChange(final Long origNumber, final Long newNumber,
			final Class<? extends Throwable> expectedException) throws InterceptorException
	{
		validator = spy(validator);
		loadOriginalValueForCouponActive(validator, FALSE);
		loadOriginalValueForCouponCodeNumber(validator, origNumber);
		final MultiCodeCouponModel coupon = getActiveMultiCodeCouponModel(newNumber);
		when(Boolean.valueOf(ctx.isModified(coupon, MultiCodeCouponModel.COUPONCODENUMBER))).thenReturn(TRUE);
		if (Objects.isNull(expectedException))
		{
			validator.onValidate(coupon, ctx);
		}
		else
		{
			assertThatThrownBy(() -> validator.onValidate(coupon, ctx)).isInstanceOf(expectedException)
					.hasMessageContaining("Coupon code seed number cannot be decremented").hasNoCause();
		}
		verify(validator).checkSeedNumberIsNotDecremented(coupon, ctx);
	}

	private MultiCodeCouponModel getMultiCodeCouponModel(final Boolean active)
	{
		final MultiCodeCouponModel model = new MultiCodeCouponModel();
		model.setCouponId(COUPON_ID);
		model.setActive(active);
		final CodeGenerationConfigurationModel configModel = getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME);
		model.setCodeGenerationConfiguration(configModel);
		return model;
	}

	private MultiCodeCouponModel getActiveMultiCodeCouponModel(final Long couponCodeNumber)
	{
		final MultiCodeCouponModel model = new MultiCodeCouponModel();
		model.setCouponId(COUPON_ID);
		model.setActive(TRUE);
		final CodeGenerationConfigurationModel configModel = getCodeGenerationConfiguration(CODE_GENERATION_CONFIGURATION_NAME);
		model.setCodeGenerationConfiguration(configModel);
		model.setCouponCodeNumber(couponCodeNumber);
		return model;
	}

	private CodeGenerationConfigurationModel getCodeGenerationConfiguration(final String configName)
	{
		final CodeGenerationConfigurationModel configModel = new CodeGenerationConfigurationModel();
		configModel.setCodeSeparator(CODE_SEPARATOR);
		configModel.setCouponPartCount(3);
		configModel.setName(configName);
		return configModel;
	}

	private MediaModel getMediaModel(final String code)
	{
		final MediaModel mediaModel = new MediaModel();
		mediaModel.setCode(code);
		return mediaModel;
	}

	private void setCouponIdModified(final Boolean modified)
	{
		when(valueOf(ctx.isModified(any(MultiCodeCouponModel.class), eq(MultiCodeCouponModel.COUPONID)))).thenReturn(modified);
	}

	private void setCodeGenerationConfigModified(final Boolean modified)
	{
		when(valueOf(ctx.isModified(any(MultiCodeCouponModel.class), eq(MultiCodeCouponModel.CODEGENERATIONCONFIGURATION))))
				.thenReturn(modified);
	}

	private void setCodeGeneratedCodesModified(final Boolean modified)
	{
		when(valueOf(ctx.isModified(any(MultiCodeCouponModel.class), eq(MultiCodeCouponModel.GENERATEDCODES))))
				.thenReturn(modified);
	}

	private void loadOriginalValueForGeneratedCodes(final MultiCodeCouponValidateInterceptor validator,
			final Collection<MediaModel> mediaModels)
	{
		doReturn(mediaModels).when(validator).getOriginal(any(MultiCodeCouponModel.class), eq(ctx),
				eq(MultiCodeCouponModel.GENERATEDCODES));
	}

	private void loadOriginalValueForCouponActive(final MultiCodeCouponValidateInterceptor validator, final Boolean activeFlag)
	{
		doReturn(activeFlag).when(validator).getOriginal(any(MultiCodeCouponModel.class), eq(ctx), eq(MultiCodeCouponModel.ACTIVE));

	}

	private void loadOriginalValueForCodeGenerationConfiguration(final MultiCodeCouponValidateInterceptor validator,
			final CodeGenerationConfigurationModel codeGenerationConfigurationModel)
	{
		doReturn(codeGenerationConfigurationModel).when(validator).getOriginal(any(MultiCodeCouponModel.class), eq(ctx),
				eq(MultiCodeCouponModel.CODEGENERATIONCONFIGURATION));

	}

	private void loadOriginalValueForCouponCodeNumber(final MultiCodeCouponValidateInterceptor validator,
			final Long origCouponCodeNumber)
	{
		doReturn(origCouponCodeNumber).when(validator).getOriginal(any(MultiCodeCouponModel.class), eq(ctx),
				eq(MultiCodeCouponModel.COUPONCODENUMBER));
	}
}
