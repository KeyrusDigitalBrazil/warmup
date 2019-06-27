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
package com.hybris.backoffice.cockpitng.dataaccess.facades.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.cockpitng.dataaccess.facades.common.PlatformFacadeStrategyHandleCache;
import com.hybris.backoffice.cockpitng.dataaccess.facades.type.expression.AttributeExpressionResolverFactory;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;


@IntegrationTest
public class DefaultPlatformTypeFacadeStrategyIntegrationTest extends ServicelayerTransactionalBaseTest
{
	private DefaultPlatformTypeFacadeStrategy typeFacade;
	@Resource
	private TypeService typeService;
	@Resource
	private ModelService modelService;
	@Resource
	private VariantsService variantsService;
	@Resource
	private I18NService i18nService;

	private PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache;

	private AttributeExpressionResolverFactory attributeExpressionResolverFactory;

	@Before
	public void setUp() throws Exception
	{
		attributeExpressionResolverFactory = mock(AttributeExpressionResolverFactory.class);

		platformFacadeStrategyHandleCache = new PlatformFacadeStrategyHandleCache();
		platformFacadeStrategyHandleCache.setTypeService(typeService);

		typeFacade = new DefaultPlatformTypeFacadeStrategy();
		typeFacade.setTypeService(typeService);
		typeFacade.setI18nService(i18nService);
		typeFacade.setModelService(modelService);
		typeFacade.setPlatformFacadeStrategyHandleCache(platformFacadeStrategyHandleCache);
		typeFacade.setResolverFactory(attributeExpressionResolverFactory);
		typeFacade.setVariantsService(variantsService);
	}

	@Test
	public void shouldConvertProduct()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForCode("Product");

		//when
		final DataType dataType = typeFacade.convertType(type, false, new DefaultContext());

		//then
		final long attributesCount = getAttributesCount(type);
		assertThat(dataType.getCode()).isEqualTo(type.getCode());
		assertThat(dataType.getAttributes()).hasSize((int) attributesCount);
	}

	@Test
	public void shouldConvertVariantProduct()
	{
		//given
		final ComposedTypeModel type = typeService.getComposedTypeForCode("VariantProduct");

		//when
		final DataType dataType = typeFacade.convertType(type, false, new DefaultContext());

		//then
		final long attributesCount = getAttributesCount(type);
		assertThat(dataType.getCode()).isEqualTo(type.getCode());
		assertThat(dataType.getAttributes()).hasSize((int) attributesCount);
	}

	@Test
	public void shouldConvertPriceRow()
	{
		// given
		final ComposedTypeModel type = typeService.getComposedTypeForCode("PriceRow");
		final String priceGroup = "pg";
		final String userGroup = "ug";

		// when
		final DataType dataType = typeFacade.convertType(type, false, new DefaultContext());

		// then
		final long attributesCount = getAttributesCount(type);
		assertThat(dataType.getCode()).isEqualTo(type.getCode());
		assertThat(dataType.getAttributes()).hasSize((int) attributesCount);
		// re-declared attributes have correct type
		assertThat(dataType.getAttribute(priceGroup).getValueType().getCode()).isEqualTo(getAttributeTypeCode(type, priceGroup));
		assertThat(dataType.getAttribute(userGroup).getValueType().getCode()).isEqualTo(getAttributeTypeCode(type, userGroup));

	}

	private long getAttributesCount(final ComposedTypeModel type)
	{
		return typeService.getAttributeDescriptorsForType(type)//
				.stream()//
				.filter(attr -> !BooleanUtils.isTrue(attr.getHiddenForUI()))//
				.count();
	}

	private String getAttributeTypeCode(final ComposedTypeModel type, final String qualifier)
	{
		return typeService.getAttributeDescriptor(type.getCode(), qualifier).getAttributeType().getCode();
	}
}
