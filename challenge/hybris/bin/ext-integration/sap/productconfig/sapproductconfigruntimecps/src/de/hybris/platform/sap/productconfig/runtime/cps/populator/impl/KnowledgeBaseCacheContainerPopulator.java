/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.impl.KnowledgeBaseContainerCacheValueLoader;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataClass;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataKnowledgeBase;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataProduct;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataClassContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates Knowledgebase master data cache during cache load. See also {@link KnowledgeBaseContainerCacheValueLoader}.
 */
public class KnowledgeBaseCacheContainerPopulator
		implements Populator<CPSMasterDataKnowledgeBase, CPSMasterDataKnowledgeBaseContainer>
{
	private Converter<CPSMasterDataProduct, CPSMasterDataProductContainer> productConverter;
	private Converter<CPSMasterDataClass, CPSMasterDataClassContainer> classConverter;
	private Converter<CPSMasterDataCharacteristic, CPSMasterDataCharacteristicContainer> characteristicConverter;

	@Override
	public void populate(final CPSMasterDataKnowledgeBase source, final CPSMasterDataKnowledgeBaseContainer target)
	{
		populateCoreAttributes(source, target);
		populateProducts(source, target);
		populateClasses(source, target);
		populateCharacteristics(source, target);
	}


	protected void populateCharacteristics(final CPSMasterDataKnowledgeBase source,
			final CPSMasterDataKnowledgeBaseContainer target)
	{
		if (source.getCharacteristics() == null || source.getCharacteristics().isEmpty())
		{
			target.setCharacteristics(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataCharacteristicContainer> characteristics = new HashMap<>();
			for (final de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataCharacteristic characteristic : source
					.getCharacteristics())
			{
				final CPSMasterDataCharacteristicContainer convertedCharacteristic = getCharacteristicConverter()
						.convert(characteristic);
				characteristics.put(convertedCharacteristic.getId(), convertedCharacteristic);
			}
			target.setCharacteristics(characteristics);
		}
	}

	protected void populateClasses(final CPSMasterDataKnowledgeBase source, final CPSMasterDataKnowledgeBaseContainer target)
	{
		if (source.getClasses() == null || source.getClasses().isEmpty())
		{
			target.setClasses(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataClassContainer> classes = new HashMap<>();
			for (final CPSMasterDataClass clazz : source.getClasses())
			{
				final CPSMasterDataClassContainer convertedClass = getClassConverter().convert(clazz);
				classes.put(convertedClass.getId(), convertedClass);
			}
			target.setClasses(classes);
		}

	}

	protected void populateProducts(final CPSMasterDataKnowledgeBase source, final CPSMasterDataKnowledgeBaseContainer target)
	{
		if (source.getProducts() == null || source.getProducts().isEmpty())
		{
			target.setProducts(Collections.emptyMap());
		}
		else
		{
			final Map<String, CPSMasterDataProductContainer> products = new HashMap<>();
			for (final CPSMasterDataProduct product : source.getProducts())
			{
				final CPSMasterDataProductContainer convertedProduct = getProductConverter().convert(product);
				products.put(convertedProduct.getId(), convertedProduct);
			}
			target.setProducts(products);
		}
	}

	protected void populateCoreAttributes(final CPSMasterDataKnowledgeBase source,
			final CPSMasterDataKnowledgeBaseContainer target)
	{
		target.setLanguage(source.getLanguage());
		target.setHeaderInfo(source.getHeaderInfo());
	}

	protected Converter<CPSMasterDataProduct, CPSMasterDataProductContainer> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * @param productConverter
	 *           product converter for product master data
	 */
	public void setProductConverter(final Converter<CPSMasterDataProduct, CPSMasterDataProductContainer> productConverter)
	{
		this.productConverter = productConverter;
	}

	protected Converter<CPSMasterDataClass, CPSMasterDataClassContainer> getClassConverter()
	{
		return classConverter;
	}

	/**
	 * @param classConverter
	 *           class converter for class master data
	 */
	public void setClassConverter(final Converter<CPSMasterDataClass, CPSMasterDataClassContainer> classConverter)
	{
		this.classConverter = classConverter;
	}

	protected Converter<CPSMasterDataCharacteristic, CPSMasterDataCharacteristicContainer> getCharacteristicConverter()
	{
		return characteristicConverter;
	}

	/**
	 * @param characteristicConverter
	 *           cstic converter for cstic master data
	 */
	@Required
	public void setCharacteristicConverter(
			final Converter<CPSMasterDataCharacteristic, CPSMasterDataCharacteristicContainer> characteristicConverter)
	{
		this.characteristicConverter = characteristicConverter;
	}

}
