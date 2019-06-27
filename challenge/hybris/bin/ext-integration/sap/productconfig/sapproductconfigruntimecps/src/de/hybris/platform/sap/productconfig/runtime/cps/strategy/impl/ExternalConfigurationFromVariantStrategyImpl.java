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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.strategy.ExternalConfigurationFromVariantStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;


/**
 * Default implementation of {@link ExternalConfigurationFromVariantStrategy}
 */
public class ExternalConfigurationFromVariantStrategyImpl implements ExternalConfigurationFromVariantStrategy
{

	protected static final String DEFAULT_CLASS_TYPE = "300";
	protected static final String INSTANCE_ID_ROOT = "1";
	private static final String CHARACTERISTIC_VALUE_SEPARATOR = "_";
	protected static final String AUTHOR_USER = " ";
	private ClassificationService classificationService;
	private MasterDataCacheAccessService masterDataCacheAccessService;
	private I18NService i18NService;
	private FlexibleSearchService flexibleSearchService;
	private ConfigurationProductUtil configurationProductUtil;
	private String classType;

	/**
	 * @return Class type for root instance
	 */
	protected String getClassType()
	{
		if (classType == null)
		{
			return DEFAULT_CLASS_TYPE;
		}
		return classType;
	}

	/**
	 * @param classType
	 */
	public void setClassType(final String classType)
	{
		this.classType = classType;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	protected MasterDataCacheAccessService getMasterDataCacheAccessService()
	{
		return masterDataCacheAccessService;
	}

	protected ClassificationService getClassificationService()
	{
		return classificationService;
	}

	/**
	 * @param classificationService
	 */
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;

	}

	@Override
	public CPSExternalConfiguration createExternalConfiguration(final String productcode, final String kbId)
	{
		final ProductModel product = getConfigurationProductUtil().getProductForCurrentCatalog(productcode);
		final CPSExternalConfiguration externalConfiguration = new CPSExternalConfiguration();
		final CPSExternalItem rootItem = createExternalRootItem(productcode);
		externalConfiguration.setRootItem(rootItem);
		externalConfiguration.setKbId(kbId);
		externalConfiguration.setConsistent(true);
		externalConfiguration.setComplete(true);
		final List<CPSExternalCharacteristic> characteristics = determineCharacteristics(kbId, product);
		rootItem.setCharacteristics(characteristics);

		return externalConfiguration;
	}

	protected List<CPSExternalCharacteristic> determineCharacteristics(final String kbId, final ProductModel product)
	{
		return getClassificationService().getFeatures(product).getFeatures().stream()//
				.filter(feature -> isFeatureRelatedToCurrentProduct(feature, kbId))//
				.map(feature -> mapToCPSCharacteristics(feature, product))//
				.filter(cpsCharacteristic -> !cpsCharacteristic.getValues().isEmpty()).collect(Collectors.toList());
	}


	protected CPSExternalCharacteristic mapToCPSCharacteristics(final Feature feature, final ProductModel product)
	{
		final String characteristicId = readCharacteristicName(feature);
		final CPSExternalCharacteristic cpsCharacteristic = new CPSExternalCharacteristic();
		cpsCharacteristic.setId(characteristicId);
		final List<CPSExternalValue> values = new ArrayList<>();
		cpsCharacteristic.setValues(values);

		feature.getValues().stream()//
				.map(this::mapToValueModel)//
				.forEach(valueModel -> addCPSCharacteristicValue(valueModel, values, characteristicId, product));

		return cpsCharacteristic;
	}

	/**
	 * @param valueModel
	 *           Is either instance of {@link ClassificationAttributeValueModel}, {@link String}, {@link Double}
	 * @param values
	 *           List of values for external configuration
	 * @param characteristicId
	 * @param product
	 */
	protected void addCPSCharacteristicValue(final Object valueModel, final List<CPSExternalValue> values,
			final String characteristicId, final ProductModel product)
	{
		final CPSExternalValue cpsValue = new CPSExternalValue();
		if (valueModel instanceof ClassificationAttributeValueModel)
		{
			final ClassificationAttributeValueModel classficationAttributeModel = (ClassificationAttributeValueModel) valueModel;
			final String csticValueFromClassificationSystem = classficationAttributeModel.getCode();
			if (!csticValueFromClassificationSystem.contains(characteristicId + CHARACTERISTIC_VALUE_SEPARATOR))
			{
				throw new IllegalStateException(
						"We expect that classification value contains characteristic ID which was not the case for: "
								+ csticValueFromClassificationSystem + "/ " + characteristicId);
			}
			cpsValue.setValue(classficationAttributeModel.getCode().substring(characteristicId.length() + 1,
					csticValueFromClassificationSystem.length()));
			cpsValue.setAuthor(findAuthor(classficationAttributeModel.getPk().toString(), product.getPk().toString()));
		}
		else
		{
			cpsValue.setValue(valueModel.toString());
			cpsValue.setAuthor(AUTHOR_USER);
		}

		values.add(cpsValue);
	}


	protected Object mapToValueModel(final FeatureValue featureValue)
	{
		final Object value = featureValue.getValue();
		if (!(value instanceof ClassificationAttributeValueModel) && !(value instanceof Double) && !(value instanceof String))
		{
			//We expect only enum types for cstics with domains (that lead to ClassificationAttributeValueModel), Strings or Double
			throw new IllegalStateException("Feature value is of wrong type (" + value.getClass().toString() + "): " + value);
		}
		return value;
	}

	protected boolean isFeatureRelatedToCurrentProduct(final Feature feature, final String kbId)
	{
		final String language = getI18NService().getCurrentLocale().getLanguage();
		final CPSMasterDataKnowledgeBaseContainer kbContainer = getMasterDataCacheAccessService().getKbContainer(kbId, language);
		final Map<String, CPSMasterDataCharacteristicContainer> characteristics = kbContainer.getCharacteristics();

		final String characteristicId = readCharacteristicName(feature);

		return characteristics.containsKey(characteristicId);
	}

	protected String readCharacteristicName(final Feature feature)
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = feature.getClassAttributeAssignment();
		Preconditions.checkNotNull(classAttributeAssignment, "No classAttributeAssignment found");

		final ClassificationAttributeModel classificationAttribute = classAttributeAssignment.getClassificationAttribute();
		Preconditions.checkNotNull(classificationAttribute, "No classificationAttribute found");

		return classificationAttribute.getCode();
	}

	protected CPSExternalItem createExternalRootItem(final String productcode)
	{
		final CPSExternalItem rootItem = new CPSExternalItem();
		rootItem.setId(INSTANCE_ID_ROOT);
		rootItem.setComplete(true);
		rootItem.setConsistent(true);
		final CPSExternalObjectKey objectKey = new CPSExternalObjectKey();
		objectKey.setType(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA);
		objectKey.setClassType(getClassType());
		final ProductModel baseProduct = determineBaseProduct(productcode);
		objectKey.setId(baseProduct.getCode());
		rootItem.setObjectKey(objectKey);
		rootItem.setObjectKeyAuthor(AUTHOR_USER);
		final CPSQuantity quantity = new CPSQuantity();
		quantity.setValue(Double.valueOf(1));
		quantity.setUnit(baseProduct.getUnit().getCode());
		rootItem.setQuantity(quantity);
		return rootItem;
	}

	protected ProductModel determineBaseProduct(final String productcode)
	{
		final ProductModel productModel = getConfigurationProductUtil().getProductForCurrentCatalog(productcode);
		if (productModel instanceof ERPVariantProductModel)
		{
			final ERPVariantProductModel variantModel = (ERPVariantProductModel) productModel;
			return variantModel.getBaseProduct();
		}
		throw new IllegalStateException("Product is no variant: " + productcode);
	}

	@Required
	public void setMasterDataCacheAccessService(final MasterDataCacheAccessService masterDataCacheAccessService)
	{
		this.masterDataCacheAccessService = masterDataCacheAccessService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		this.i18NService = i18nService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;

	}

	protected String findAuthor(final String classificationvalueidentifier, final String productpk)
	{
		final SearchResult<ProductFeatureModel> searchResult = flexibleSearchService
				.search("select {pk} from {productfeature} where {stringvalue}='" + classificationvalueidentifier + "'"
						+ "and {product}='" + productpk + "'");

		final List<ProductFeatureModel> result = searchResult.getResult();
		if (result.size() != 1)
		{
			throw new IllegalStateException("No unique result found in productFeature");
		}
		final String author = result.get(0).getAuthor();
		return author != null ? author : AUTHOR_USER;

	}

	protected ConfigurationProductUtil getConfigurationProductUtil()
	{
		return configurationProductUtil;
	}

	@Required
	public void setConfigurationProductUtil(final ConfigurationProductUtil configurationProductUtil)
	{
		this.configurationProductUtil = configurationProductUtil;
	}

}
