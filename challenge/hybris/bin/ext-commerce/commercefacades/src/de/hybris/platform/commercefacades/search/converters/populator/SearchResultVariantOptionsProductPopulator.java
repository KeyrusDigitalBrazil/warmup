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
package de.hybris.platform.commercefacades.search.converters.populator;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 * Populator that populates top level variants in product data from search results.
 *
 */
public class SearchResultVariantOptionsProductPopulator extends SearchResultVariantProductPopulator
{
	public static final String ITEMTYPE_VARIANT_PROPERTY = "itemtype";
	public static final String CODE_VARIANT_PROPERTY = "code";
	public static final String URL_VARIANT_PROPERTY = "url";
	public static final String PRICE_VALUE_VARIANT_PROPERTY = "priceValue";

	public static final String ROLLUP_PROPERTY = "rollupProperty";
	public static final String VARIANT_ROLLUP_PROPERTY_CONFIG = "commerceservices.variant.rollup.property";

	private VariantsService variantsService;
	private ConfigurationService configurationService;
	private BaseSiteService baseSiteService;

	@Override
	public void populate(final SearchResultValueData source, final ProductData target)
	{
		super.populate(source, target);
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			return;
		}

		final String variantType = (String) getValue(source, ITEMTYPE_VARIANT_PROPERTY);
		final Set<String> variantTypeAttributes = getVariantsService().getVariantAttributes(variantType);
		final String rollupProperty = getRollupProperty(variantTypeAttributes);
		if (StringUtils.isBlank(rollupProperty))
		{
			return;
		}

		final List<SearchResultValueData> variants = source.getVariants().stream()
				.filter(distinctByKey(p -> getValue(p, rollupProperty))).collect(Collectors.toList());

		target.setVariantOptions(getVariantOptions(variants, variantTypeAttributes, rollupProperty));
	}

	protected List<VariantOptionData> getVariantOptions(final List<SearchResultValueData> variants,
			final Set<String> variantTypeAttributes, final String rollupProperty)
	{
		final List<VariantOptionData> variantOptions = new ArrayList<>();

		for (final SearchResultValueData variant : variants)
		{
			final VariantOptionData variantOption = new VariantOptionData();
			variantOption.setCode((String) getValue(variant, CODE_VARIANT_PROPERTY));
			final Double priceValue = this.<Double> getValue(variant, PRICE_VALUE_VARIANT_PROPERTY);
			if (priceValue != null)
			{
				final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY,
						BigDecimal.valueOf(priceValue.doubleValue()), getCommonI18NService().getCurrentCurrency());
				variantOption.setPriceData(priceData);
			}

			variantOption.setUrl((String) getValue(variant, URL_VARIANT_PROPERTY));
			variantOption.setVariantOptionQualifiers(getVariantOptionQualifiers(variant, variantTypeAttributes, rollupProperty));

			variantOptions.add(variantOption);
		}
		return variantOptions;
	}

	protected Collection<VariantOptionQualifierData> getVariantOptionQualifiers(final SearchResultValueData variant,
			final Set<String> variantTypeAttributes, final String rollupProperty)
	{
		final Collection<VariantOptionQualifierData> variantOptionQualifiers = new ArrayList<>();

		for (final String variantTypeAttribute : variantTypeAttributes)
		{
			final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
			variantOptionQualifier.setQualifier(variantTypeAttribute);
			variantOptionQualifier.setValue((String) getValue(variant, variantTypeAttribute));

			variantOptionQualifiers.add(variantOptionQualifier);
		}

		// add images
		final List<ImageData> imageData = createImageData(variant);
		for (final ImageData image : imageData)
		{
			final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
			variantOptionQualifier.setQualifier(image.getFormat());
			variantOptionQualifier.setImage(image);
			variantOptionQualifier.setValue(image.getUrl());
			variantOptionQualifiers.add(variantOptionQualifier);
		}

		final VariantOptionQualifierData rollupQualifier = new VariantOptionQualifierData();
		rollupQualifier.setQualifier(ROLLUP_PROPERTY);
		rollupQualifier.setValue(rollupProperty);
		variantOptionQualifiers.add(rollupQualifier);

		final VariantOptionQualifierData variantOptionQualifier = new VariantOptionQualifierData();
		variantOptionQualifier.setQualifier(rollupProperty);
		variantOptionQualifier.setValue(getValue(variant, rollupProperty).toString());
		variantOptionQualifiers.add(variantOptionQualifier);

		return variantOptionQualifiers;
	}

	protected String getRollupProperty(final Set<String> variantTypeAttributes)
	{
		String firstVariantCategory = configurationService.getConfiguration()
				.getString(VARIANT_ROLLUP_PROPERTY_CONFIG + "." + baseSiteService.getCurrentBaseSite().getUid());
		if (StringUtils.isNotBlank(firstVariantCategory))
		{
			return firstVariantCategory;
		}

		if (CollectionUtils.isNotEmpty(variantTypeAttributes))
		{
			firstVariantCategory = variantTypeAttributes
					.toArray(new String[variantTypeAttributes.size()])[variantTypeAttributes.size() - 1];
		}
		return firstVariantCategory;
	}

	protected static <T> Predicate<T> distinctByKey(final Function<? super T, Object> keyExtractor)
	{
		final Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> {
			final Object key = keyExtractor.apply(t);
			if (key == null)
			{
				return false;
			}
			return seen.putIfAbsent(key, Boolean.TRUE) == null;
		};
	}

	public VariantsService getVariantsService()
	{
		return variantsService;
	}

	@Required
	public void setVariantsService(final VariantsService variantsService)
	{
		this.variantsService = variantsService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}