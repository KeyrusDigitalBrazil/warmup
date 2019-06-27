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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.couponservices.model.CodeGenerationConfigurationModel;
import de.hybris.platform.couponservices.model.MultiCodeCouponModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class MultiCodeCouponValidationIT extends ServicelayerTest
{

	private static final String COUPON_ID = "testCouponId123";
	private static final String CODE_SEPARATOR = "-";
	private static final String MEDIA_MODEL_CODE = "mmTestCode";

	@Resource
	private ModelService modelService;

	private MultiCodeCouponModel couponModel;
	private CatalogModel catalog;

	@Before
	public void setUp()
	{
		couponModel = getMultiCodeCouponModel(TRUE);
		catalog = new CatalogModel();
		catalog.setId("testCatalog");
	}

	@Test
	public void testSave()
	{
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyWithWrongEndDate()
	{
		modelService.save(couponModel);
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		couponModel.setEndDate(yesterday.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveNewWithWrongEndDate()
	{
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DAY_OF_YEAR, -1);
		couponModel.setEndDate(yesterday.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveWithStartDateAfterEndDate()
	{
		final Calendar today = Calendar.getInstance();
		final Calendar startDate = (Calendar) today.clone();
		startDate.add(Calendar.DAY_OF_YEAR, 20);
		final Calendar endDate = (Calendar) today.clone();
		endDate.add(Calendar.DAY_OF_YEAR, 10);

		couponModel.setStartDate(startDate.getTime());
		couponModel.setEndDate(endDate.getTime());
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyCouponIdWhenActive()
	{
		modelService.save(couponModel);
		couponModel.setCouponId("newCouponId");
		modelService.save(couponModel);
	}

	@Test
	public void testModifyCouponIdWhenNonActive()
	{
		modelService.save(couponModel);
		couponModel.setActive(FALSE);
		couponModel.setCouponId("newCouponId");
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testSaveCouponIdWithSpecialCharacters()
	{
		couponModel.setCouponId("MultiCode_123-");
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyCouponIdWhenNonActiveCodesGenerated()
	{
		final Collection<MediaModel> generatedCodes = Arrays.asList(getMediaModel(MEDIA_MODEL_CODE),
				getMediaModel(MEDIA_MODEL_CODE + 1));
		couponModel.setGeneratedCodes(generatedCodes);
		modelService.save(couponModel);
		couponModel.setActive(FALSE);
		couponModel.setCouponId("newCouponId");
		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyCodeGenerationConfigWhenActive()
	{
		final Collection<MediaModel> generatedCodes = Collections.EMPTY_LIST;
		couponModel.setGeneratedCodes(generatedCodes);
		modelService.save(couponModel);

		final CodeGenerationConfigurationModel configModel = new CodeGenerationConfigurationModel();
		configModel.setCodeSeparator("|");
		configModel.setCouponPartCount(4);
		configModel.setName("TEST_CONFIG" + "T");
		couponModel.setCodeGenerationConfiguration(configModel);

		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyGeneratedCodesWhenActive()
	{
		final MediaModel mediaModel1 = getMediaModel(MEDIA_MODEL_CODE);
		final MediaModel mediaModel2 = getMediaModel(MEDIA_MODEL_CODE + 1);
		final Collection<MediaModel> generatedCodes = Arrays.asList(mediaModel1, mediaModel2);
		couponModel.setGeneratedCodes(generatedCodes);
		modelService.save(couponModel);

		final Collection<MediaModel> newGeneratedCodes = Arrays.asList(mediaModel1);
		couponModel.setGeneratedCodes(newGeneratedCodes);

		modelService.save(couponModel);
	}

	@Test(expected = ModelSavingException.class)
	public void testModifyGeneratedCodesWhenNonActive()
	{
		final MediaModel mediaModel1 = getMediaModel(MEDIA_MODEL_CODE);
		final MediaModel mediaModel2 = getMediaModel(MEDIA_MODEL_CODE + 1);
		final Collection<MediaModel> generatedCodes = Arrays.asList(mediaModel1, mediaModel2);
		couponModel.setGeneratedCodes(generatedCodes);
		modelService.save(couponModel);

		final Collection<MediaModel> newGeneratedCodes = Arrays.asList(mediaModel1);
		couponModel.setGeneratedCodes(newGeneratedCodes);

		couponModel.setActive(FALSE);

		modelService.save(couponModel);
	}

	@Test
	public void testSaveWithNullDates()
	{
		couponModel.setStartDate(null);
		couponModel.setEndDate(null);
		modelService.save(couponModel);
	}

	private MultiCodeCouponModel getMultiCodeCouponModel(final Boolean active)
	{
		final MultiCodeCouponModel model = new MultiCodeCouponModel();
		model.setCouponId(COUPON_ID);
		model.setActive(active);
		final CodeGenerationConfigurationModel configModel = new CodeGenerationConfigurationModel();
		configModel.setCodeSeparator(CODE_SEPARATOR);
		configModel.setCouponPartCount(3);
		configModel.setName("TEST_CONFIG");

		model.setCodeGenerationConfiguration(configModel);
		final Calendar today = Calendar.getInstance();
		final Calendar startDate = (Calendar) today.clone();
		startDate.add(Calendar.DAY_OF_YEAR, -10);
		model.setStartDate(startDate.getTime());
		final Calendar endDate = (Calendar) today.clone();
		endDate.add(Calendar.DAY_OF_YEAR, 10);
		model.setEndDate(endDate.getTime());
		return model;
	}

	private MediaModel getMediaModel(final String code)
	{
		final MediaModel mediaModel = new MediaModel();
		mediaModel.setCode(code);
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		catalogVersion.setActive(TRUE);
		catalogVersion.setVersion("testVersion" + code);
		catalogVersion.setCatalog(catalog);
		mediaModel.setCatalogVersion(catalogVersion);
		return mediaModel;
	}

}
