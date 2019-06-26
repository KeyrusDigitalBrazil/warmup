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
package de.hybris.platform.entitlementservices.interceptor.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.entitlementservices.model.EntitlementModel;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.jalo.GenericItem;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.localization.Localization;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class ProductEntitlementValidatorTest
{

	@Mock
	private InterceptorContext ctx;

	private ProductEntitlementValidateInterceptor validator = new ProductEntitlementValidateInterceptor();
	private ProductEntitlementModel modelWithUU;
	private ProductEntitlementModel modelWithoutUU;
	private EntitlementModel entitlement;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final GenericItem item = mock(GenericItem.class);
		final ModelService modelService = mock(ModelService.class);
		when(modelService.getSource(null)).thenReturn(item);
		validator.setModelService(modelService);

		modelWithUU = new ProductEntitlementModel();
		entitlement = new EntitlementModel();
		modelWithUU.setEntitlement(entitlement);

		modelWithoutUU = new ProductEntitlementModel();
		modelWithoutUU.setEntitlement(new EntitlementModel());

	}

	@Test
	public void testProductEntitlementValidatorQuantityIsPositive() throws InterceptorException
	{
		modelWithUU.setQuantity(5);
		validator.onValidate(modelWithUU, ctx);
	}

	@Test
	public void testProductEntitlementValidatorQuantityIsZero() throws InterceptorException
	{
		modelWithUU.setQuantity(0);
		validator.onValidate(modelWithUU, ctx);
	}

	@Test
	public void testProductEntitlementValidatorQuantityIsMinusOne() throws InterceptorException
	{
		modelWithUU.setQuantity(-1);
		validator.onValidate(modelWithUU, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void testProductEntitlementValidatorQuantityIsNegative() throws InterceptorException
	{
		modelWithUU.setQuantity(-5);
		validator.onValidate(modelWithUU, ctx);
	}

	@Test
	public void testProductEntitlementValidatorWithoutUsageUnit() throws InterceptorException
	{
		validator.onValidate(modelWithoutUU, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectTimeUnitWithoutStartDate() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitDuration(1);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectDurationWithoutStartDate() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitDuration(1);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test
	public void shouldAcceptZeroDuration() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(new EntitlementModel());
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitStart(1);
		productEntitlement.setTimeUnitDuration(0);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test
	public void shouldAcceptUnlimitedDuration() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(new EntitlementModel());
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitStart(1);
		productEntitlement.setTimeUnitDuration(0);
		validator.onValidate(productEntitlement, ctx);
	}
	
	@Test(expected = InterceptorException.class)
	public void shouldRejectPathEndingWithSeparator() throws InterceptorException
	{
		final ProductEntitlementModel model = new ProductEntitlementModel();
		model.setEntitlement(new EntitlementModel());
		model.setConditionPath("root/");
		validator.onValidate(model, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectNegativeDuration() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitStart(1);
		productEntitlement.setTimeUnitDuration(-10);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectZeroStartTime() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitStart(0);
		productEntitlement.setTimeUnitDuration(1);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectNegativeStartTime() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setTimeUnit(EntitlementTimeUnit.DAY);
		productEntitlement.setTimeUnitStart(-1);
		productEntitlement.setTimeUnitDuration(1);
		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectCommasInGeoPath() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setConditionGeo(Arrays.asList("Germany,Russia"));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void shouldRejectTrailingSlash() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setConditionGeo(Arrays.asList("Deutschland/"));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void locationWithTrailingSlash() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		final String location = "Deutschland/Bayern/";

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionGeo(Arrays.asList(location));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void locationWithTooManyLevels() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		final String location = "1/2/3/4";

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionGeo(Arrays.asList(location));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void locationWithMissingRegion() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		final String location = "Deutschland//München";

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionGeo(Arrays.asList(location));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void locationWithLeadingSlash() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		final String location = "/Deutschland";

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionGeo(Arrays.asList(location));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void locationWithComma() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();
		final String location = "Deutschland,Great Britain";

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionGeo(Arrays.asList(location));

		validator.onValidate(productEntitlement, ctx);
	}

	@Test(expected = InterceptorException.class)
	public void pathWithTrailingSlash() throws InterceptorException
	{
		final ProductEntitlementModel productEntitlement = new ProductEntitlementModel();

		productEntitlement.setEntitlement(entitlement);
		productEntitlement.setQuantity(0);
		productEntitlement.setConditionPath("/video/");

		validator.onValidate(productEntitlement, ctx);
	}

}
