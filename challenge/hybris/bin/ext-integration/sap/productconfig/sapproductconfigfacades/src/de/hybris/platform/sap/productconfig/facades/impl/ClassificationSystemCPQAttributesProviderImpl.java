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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.sap.productconfig.facades.CPQImageFormatMapping;
import de.hybris.platform.sap.productconfig.facades.CPQImageType;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link ClassificationSystemCPQAttributesProvider}.<br>
 */
public class ClassificationSystemCPQAttributesProviderImpl implements ClassificationSystemCPQAttributesProvider
{

	private static final Logger LOGGER = Logger.getLogger(ClassificationSystemCPQAttributesProviderImpl.class);
	private static final String LOG_SLOW_PERF = "Using deprecated Mapping without cacheMap for hybris classification, mapping for large KBs might be slow.";

	private static final String CSTIC_MODEL = "CsticModel";
	private static final String CSTIC_NAME = "CSTIC_NAME";
	private static final String LONG_TEXT = "LONG_TEXT";
	private static final String SOURCE_HYBRIS = "HYBRIS_NAME";
	private static final String DISPLAY_NAME = "DISPLAY_NAME";
	private static final String MODEL_LANGDEP_NAME = "MODEL_LANGDEP_NAME";
	private static final String SOURCE_MODEL = "MODEL_NAME";
	private static final String CSTIC_VALUE_MODEL = "CsticValueModel";
	private static final String VALUE_NAME = "VALUE_NAME";

	private ClassificationSystemService classificationService;
	private BaseStoreService baseStoreService;
	private UiTypeFinder uiTypeFinder;
	private ValueFormatTranslator valueFormatTranslator;
	private CPQImageFormatMapping cpqCsticImageFormatMapping;
	private CPQImageFormatMapping cpqCsticValueImageFormatMapping;
	private ImagePopulator imagePopulator;

	private FlexibleSearchService flexibleSearchService;

	@Override
	public ClassificationSystemCPQAttributesContainer getCPQAttributes(final String name,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		ClassificationSystemCPQAttributesContainer cpqAttributes = null;
		if (nameMap != null)
		{
			cpqAttributes = nameMap.get(name);
		}
		else
		{
			LOGGER.debug(LOG_SLOW_PERF);
		}
		if (cpqAttributes == null)
		{
			final ClassificationSystemVersionModel systemVersion = getSystemVersion();
			final ClassificationAttributeModel attr = getClassificationAttribute(name, systemVersion);
			cpqAttributes = getNameFromAttribute(attr, systemVersion);
			if (nameMap != null)
			{
				nameMap.put(name, cpqAttributes);
			}
		}
		return cpqAttributes;
	}

	protected ClassificationSystemCPQAttributesContainer getNameFromAttribute(final ClassificationAttributeModel attr,
			final ClassificationSystemVersionModel systemVersion)
	{
		ClassificationSystemCPQAttributesContainer cpqAttributes;
		if (attr == null)
		{
			cpqAttributes = ClassificationSystemCPQAttributesContainer.NULL_OBJ;
		}
		else
		{
			final String code = attr.getCode();
			final String name = attr.getName();
			final String description = attr.getDescription();

			Collection<ClassificationAttributeValueModel> attrValues = findClassificationAttributeValuesByCodePrefix(systemVersion,
					code);
			if (attrValues == null)
			{
				attrValues = Collections.emptyList();
			}

			final Map<String, String> valueNames = extractValueNamesFromAttributeModel(attrValues);
			final Map<String, String> valueDescriptions = extractValueDescriptionsFromAttributeModel(attrValues);
			final Collection<MediaModel> csticMedia = extractCsticMediaFromAttributeModel(attr);
			final Map<String, Collection<MediaModel>> csticValueMedia = extractCsticValueMediaFromAttributeModel(attrValues);
			cpqAttributes = new ClassificationSystemCPQAttributesContainer(code, name, description, valueNames, valueDescriptions,
					csticMedia, csticValueMedia);
		}
		return cpqAttributes;
	}

	protected Map<String, String> extractValueDescriptionsFromAttributeModel(
			final Collection<ClassificationAttributeValueModel> attrValues)
	{
		Map<String, String> hybrisValueDescriptionMap = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(attrValues))
		{
			final int size = (int) (attrValues.size() / 0.75 + 1);
			hybrisValueDescriptionMap = new HashMap<>(size);
			for (final ClassificationAttributeValueModel attrValue : attrValues)
			{
				hybrisValueDescriptionMap.put(attrValue.getCode(), attrValue.getDescription());
			}
		}
		return hybrisValueDescriptionMap;
	}

	protected Map<String, Collection<MediaModel>> extractCsticValueMediaFromAttributeModel(
			final Collection<ClassificationAttributeValueModel> attrValues)
	{
		Map<String, Collection<MediaModel>> csticValueMedia = Collections.emptyMap();
		if (!CollectionUtils.isEmpty(attrValues))
		{
			csticValueMedia = addMediaEntries(attrValues);
		}
		return csticValueMedia;
	}

	protected final Map<String, Collection<MediaModel>> addMediaEntries(
			final Collection<ClassificationAttributeValueModel> attrValues)
	{
		final int size = (int) (attrValues.size() / 0.75 + 1);
		final Map<String, Collection<MediaModel>> csticValueMedia = new HashMap<>(size);
		for (final ClassificationAttributeValueModel attrValue : attrValues)
		{
			final MediaContainerModel mediaContainer = attrValue.getCpqMedia();
			if (mediaContainer != null)
			{
				final Collection<MediaModel> media = mediaContainer.getMedias();
				if (!CollectionUtils.isEmpty(media))
				{
					csticValueMedia.put(attrValue.getCode(), media);
				}
			}
		}
		return csticValueMedia;
	}

	protected Collection<MediaModel> extractCsticMediaFromAttributeModel(final ClassificationAttributeModel attr)
	{
		List<MediaModel> csticMedia = Collections.emptyList();

		final MediaContainerModel mediaContainer = attr.getCpqMedia();
		if (mediaContainer != null)
		{
			final Collection<MediaModel> media = mediaContainer.getMedias();
			if (CollectionUtils.isNotEmpty(media))
			{
				csticMedia = new ArrayList<>(media.size());
				csticMedia.addAll(media);
			}
		}

		return csticMedia;
	}

	protected Map<String, String> extractValueNamesFromAttributeModel(
			final Collection<ClassificationAttributeValueModel> attrValues)
	{
		Map<String, String> hybrisValueNameMap = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(attrValues))
		{
			final int size = (int) (attrValues.size() / 0.75 + 1);
			hybrisValueNameMap = new HashMap<>(size);
			for (final ClassificationAttributeValueModel attrValue : attrValues)
			{
				hybrisValueNameMap.put(attrValue.getCode(), attrValue.getName());
			}
		}
		return hybrisValueNameMap;
	}

	@Override
	public List<ImageData> getCsticMedia(final ClassificationSystemCPQAttributesContainer cpqAttributes)
	{
		List<ImageData> images = new ArrayList<>();
		final Collection<MediaModel> csticMedia = cpqAttributes.getCsticMedia();
		if (CollectionUtils.isNotEmpty(csticMedia))
		{
			images = convertMediaToImages(csticMedia, getCpqCsticImageFormatMapping().getCPQMediaFormatQualifiers());
		}
		return images;
	}

	@Override
	public List<ImageData> getCsticValueMedia(final String csticValueKey,
			final ClassificationSystemCPQAttributesContainer cpqAttributes)
	{
		List<ImageData> images = new ArrayList<>();
		final Map<String, Collection<MediaModel>> csticValueMedia = cpqAttributes.getCsticValueMedia();
		if (MapUtils.isNotEmpty(csticValueMedia))
		{
			final Collection<MediaModel> media = csticValueMedia.get(csticValueKey);
			if (!CollectionUtils.isEmpty(media))
			{
				images = convertMediaToImages(media, getCpqCsticValueImageFormatMapping().getCPQMediaFormatQualifiers());
			}
		}
		return images;
	}

	protected List<ImageData> convertMediaToImages(final Collection<MediaModel> media,
			final Map<String, CPQImageType> cpqMediaFormatQualifiers)
	{
		final List<ImageData> images = new ArrayList<>();
		for (final MediaModel medium : media)
		{
			if (cpqMediaFormatQualifiers.containsKey(medium.getMediaFormat().getQualifier()))
			{
				final ImageData image = new ImageData();
				getImagePopulator().populate(medium, image);
				final CPQImageType cpqImageQualifier = cpqMediaFormatQualifiers.get(medium.getMediaFormat().getQualifier());
				image.setFormat(cpqImageQualifier.toString());
				images.add(image);
			}
		}
		return images;
	}

	@Override
	public String getLongText(final CsticModel model, final ClassificationSystemCPQAttributesContainer cpqAttributes,
			final boolean isDebugEnabled)
	{
		String longText;
		final String source;

		final String hybrisLongText = cpqAttributes.getDescription();
		if (!StringUtils.isEmpty(hybrisLongText))
		{
			longText = hybrisLongText;
			source = SOURCE_HYBRIS;
		}
		else
		{
			longText = model.getLongText();
			source = SOURCE_MODEL;
		}
		if (StringUtils.isEmpty(longText))
		{
			longText = null;
		}
		logValue(CSTIC_MODEL, CSTIC_NAME, model.getName(), LONG_TEXT, source, longText, isDebugEnabled);

		return longText;
	}

	protected void logValue(final String className, final String nameType, final String name, final String targetType,
			final String sourceType, final String value, final boolean isDebugEnabled)
	{
		if (isDebugEnabled)
		{
			LOGGER.debug(className + " [" + nameType + "='" + name + "'; " + targetType + "(" + sourceType + ")='" + value + "']");
		}
	}

	@Override
	public String getDisplayName(final CsticModel csticModel, final ClassificationSystemCPQAttributesContainer cpqAttributes,
			final boolean isDebugEnabled)
	{

		final String langDepName = csticModel.getLanguageDependentName();
		final String hybrisName = cpqAttributes.getName();
		String displayName;
		if (!StringUtils.isEmpty(hybrisName))
		{
			displayName = hybrisName;
			logValue(CSTIC_MODEL, CSTIC_NAME, csticModel.getName(), DISPLAY_NAME, SOURCE_HYBRIS, displayName, isDebugEnabled);
		}
		else if (!StringUtils.isEmpty(langDepName))
		{
			displayName = langDepName;
			logValue(CSTIC_MODEL, CSTIC_NAME, csticModel.getName(), DISPLAY_NAME, MODEL_LANGDEP_NAME, displayName, isDebugEnabled);
		}
		else
		{
			displayName = "[" + csticModel.getName() + "]";
			logValue(CSTIC_MODEL, CSTIC_NAME, csticModel.getName(), DISPLAY_NAME, SOURCE_MODEL, displayName, isDebugEnabled);
		}

		return displayName;
	}

	/**
	 * Retrieves the hybris classification attribute for a characteristic name (which is part of a CPQ configuraton model).
	 * <br>
	 *
	 * @param name
	 *           Language independent name of characteristic
	 * @param systemVersion
	 * @return Classification attribute
	 */
	protected ClassificationAttributeModel getClassificationAttribute(final String name,
			final ClassificationSystemVersionModel systemVersion)
	{
		if (isDebugEnabled())
		{
			LOGGER.debug("getClassificationAttribute for: " + name);
		}

		ClassificationAttributeModel attribute = null;
		if (systemVersion != null)
		{
			final Collection<ClassificationAttributeModel> attributes = findClassificationAttributesByCode(systemVersion, name);
			if (CollectionUtils.isEmpty(attributes))
			{
				LOGGER.debug("The classification attribute is not found for the name '" + name + "'");
			}
			else if (attributes.size() > 1)
			{
				LOGGER.debug("Classification attribute name '" + name + "' is ambigious!");
			}
			else
			{
				attribute = attributes.iterator().next();
			}
		}

		return attribute;
	}

	@Override
	public String getDisplayValueName(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes, final boolean isDebugEnabled)
	{
		String displayName = getValueName(valueModel, csticModel, cpqAttributes, isDebugEnabled);
		if (displayName == null)
		{
			displayName = "[" + valueModel.getName() + "]";
			logValue(CSTIC_VALUE_MODEL, VALUE_NAME, valueModel.getName(), DISPLAY_NAME, SOURCE_MODEL, displayName, isDebugEnabled);
		}
		return displayName;
	}

	@Override
	public String getOverviewValueName(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes, final boolean isDebugEnabled)
	{
		if (UiType.NOT_IMPLEMENTED == getUiTypeFinder().findUiTypeForCstic(csticModel))
		{
			return UiType.NOT_IMPLEMENTED.toString();
		}

		String displayName = getValueName(valueModel, csticModel, cpqAttributes, isDebugEnabled);
		if (displayName == null)
		{
			displayName = getValueFormatTranslator().format(csticModel, valueModel.getName());
			logValue(CSTIC_VALUE_MODEL, VALUE_NAME, valueModel.getName(), DISPLAY_NAME, SOURCE_MODEL, displayName, isDebugEnabled);
		}
		return displayName;
	}

	protected String getValueName(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes, final boolean isDebugEnabled)
	{
		String hybrisValueName = null;
		if (cpqAttributes.getValueNames().size() > 0)
		{
			hybrisValueName = cpqAttributes.getValueNames().get(getValueKey(valueModel, csticModel));
		}
		final String langDepName = valueModel.getLanguageDependentName();

		String displayName = null;
		if (!StringUtils.isEmpty(hybrisValueName))
		{
			displayName = hybrisValueName;
			logValue(CSTIC_VALUE_MODEL, VALUE_NAME, valueModel.getName(), DISPLAY_NAME, SOURCE_HYBRIS, displayName, isDebugEnabled);
		}
		else if (!StringUtils.isEmpty(langDepName))
		{
			displayName = langDepName;
			logValue(CSTIC_VALUE_MODEL, VALUE_NAME, valueModel.getName(), DISPLAY_NAME, MODEL_LANGDEP_NAME, displayName,
					isDebugEnabled);
		}
		return displayName;
	}

	protected String getValueKey(final CsticValueModel valueModel, final CsticModel csticModel)
	{
		return csticModel.getName() + "_" + valueModel.getName();
	}

	/**
	 * Determines the classification system we use for retrieving the hybris classification attributes for CPQ
	 * characteristics. Checks the classification systems that are attached to a base store. <br>
	 * <br>
	 * If no system is available, null is returned. <br>
	 * <br>
	 * If at least one system is available: Returns the first one which contains '300' as part of its name, as this matches
	 * the customizing proposal for SAP Integration. '300' stands for class type 300 in ERP, which is typically used for
	 * product configuration. <br>
	 * <br>
	 * If no system contains '300', the first one found is returned. <br>
	 * <br>
	 * NOTE The pattern '300' can also be replaced, in this case override method getClassificationSystemPattern
	 *
	 *
	 * @return classification system Version model
	 */
	protected ClassificationSystemVersionModel getSystemVersion()
	{
		ClassificationSystemVersionModel classificationSystemVersionModel = null;
		final BaseStoreModel baseStore = getBaseStore();
		if (isDebugEnabled())
		{
			LOGGER.debug("Current base store: " + baseStore.getName());
		}

		final List<CatalogModel> catalogs = baseStore.getCatalogs();
		final List<ClassificationSystemModel> availableClassificationSystems = new ArrayList<>();
		final List<ClassificationSystemModel> availableClassificationSystemsMatchingSubstring = new ArrayList<>();

		determineAvailableClassificationSystems(catalogs, availableClassificationSystems,
				availableClassificationSystemsMatchingSubstring);

		ClassificationSystemModel cpqClassificationSystem = null;
		if (!availableClassificationSystemsMatchingSubstring.isEmpty())
		{
			cpqClassificationSystem = availableClassificationSystemsMatchingSubstring.get(0);
		}
		else if (!availableClassificationSystems.isEmpty())
		{
			cpqClassificationSystem = availableClassificationSystems.get(0);
		}
		if (cpqClassificationSystem != null)
		{
			classificationSystemVersionModel = classificationService.getSystemVersion(cpqClassificationSystem.getId(),
					cpqClassificationSystem.getVersion());
		}

		return classificationSystemVersionModel;
	}

	protected void determineAvailableClassificationSystems(final List<CatalogModel> catalogs,
			final List<ClassificationSystemModel> availableClassificationSystems,
			final List<ClassificationSystemModel> availableClassificationSystemsMatchingPattern)
	{
		LOGGER.debug("determineAvailableClassificationSystems");
		for (final CatalogModel catalog : catalogs)
		{
			if (isDebugEnabled())
			{
				LOGGER.debug("determineAvailableClassificationSystems, catalog ID: " + catalog.getId());
			}
			if (catalog instanceof ClassificationSystemModel)
			{
				LOGGER.debug("is classification system");
				final ClassificationSystemModel classificationSystem = (ClassificationSystemModel) catalog;
				availableClassificationSystems.add(classificationSystem);
				if (classificationSystem.getId().contains(getClassificationSystemSubString()))
				{
					availableClassificationSystemsMatchingPattern.add(classificationSystem);
				}
			}
		}
	}

	/**
	 * Returns substring we search for in the available classification systems. The first system which contains this
	 * substring will be used to determine hybris attributes for CPQ characteristics names
	 *
	 * @return "300"
	 */
	protected String getClassificationSystemSubString()
	{
		return "300";
	}

	protected BaseStoreModel getBaseStore()
	{
		final BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
		if (baseStore == null)
		{
			throw new IllegalStateException("No base store available");
		}
		return baseStore;
	}

	@Override
	public boolean isDebugEnabled()
	{
		return LOGGER.isDebugEnabled();
	}

	/**
	 * @param classificationService
	 *           injects the classification service, required to access the hybris classification system
	 */
	public void setClassificationService(final ClassificationSystemService classificationService)
	{
		this.classificationService = classificationService;
	}

	/**
	 * @param baseStoreService
	 *           injects the base store service, required to access the hybris classification system
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected UiTypeFinder getUiTypeFinder()
	{
		return uiTypeFinder;
	}

	/**
	 * @param uiTypeFinder
	 *           characteristic UI type finder
	 */
	public void setUiTypeFinder(final UiTypeFinder uiTypeFinder)
	{
		this.uiTypeFinder = uiTypeFinder;
	}

	protected ValueFormatTranslator getValueFormatTranslator()
	{
		return valueFormatTranslator;
	}

	/**
	 * @param valueFormatTranslator
	 *           value format translator
	 */
	public void setValueFormatTranslator(final ValueFormatTranslator valueFormatTranslator)
	{
		this.valueFormatTranslator = valueFormatTranslator;
	}

	protected CPQImageFormatMapping getCpqCsticImageFormatMapping()
	{
		return cpqCsticImageFormatMapping;
	}

	/**
	 * @param cpqCsticImageFormatMapping
	 *           the cpqCsticImageFormatMapping to set
	 */
	public void setCpqCsticImageFormatMapping(final CPQImageFormatMapping cpqCsticImageFormatMapping)
	{
		this.cpqCsticImageFormatMapping = cpqCsticImageFormatMapping;
	}

	protected CPQImageFormatMapping getCpqCsticValueImageFormatMapping()
	{
		return cpqCsticValueImageFormatMapping;
	}

	/**
	 * @param cpqCsticValueImageFormatMapping
	 *           the cpqCsticValueImageFormatMapping to set
	 */
	public void setCpqCsticValueImageFormatMapping(final CPQImageFormatMapping cpqCsticValueImageFormatMapping)
	{
		this.cpqCsticValueImageFormatMapping = cpqCsticValueImageFormatMapping;
	}

	protected ImagePopulator getImagePopulator()
	{
		return imagePopulator;
	}

	/**
	 * @param imagePopulator
	 *           the imagePopulator to set
	 */
	public void setImagePopulator(final ImagePopulator imagePopulator)
	{
		this.imagePopulator = imagePopulator;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


	protected Collection<ClassificationAttributeModel> findClassificationAttributesByCode(
			final ClassificationSystemVersionModel systemVersion, final String code)
	{
		final StringBuilder query = new StringBuilder("SELECT {" + ClassificationAttributeModel.PK + "} FROM ");
		query.append(" {" + ClassificationAttributeModel._TYPECODE + "} WHERE ");
		query.append(" {" + ClassificationAttributeModel.SYSTEMVERSION + "}=?" + ClassificationAttributeModel.SYSTEMVERSION);
		query.append(" AND {" + ClassificationAttributeModel.CODE + "}=?" + ClassificationAttributeModel.CODE);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());
		searchQuery.addQueryParameter(ClassificationAttributeModel.SYSTEMVERSION, systemVersion);
		searchQuery.addQueryParameter(ClassificationAttributeModel.CODE, code);

		final SearchResult<ClassificationAttributeModel> result = getFlexibleSearchService().search(searchQuery);
		return result.getResult();
	}

	protected Collection<ClassificationAttributeValueModel> findClassificationAttributeValuesByCodePrefix(
			final ClassificationSystemVersionModel systemVersion, final String code)
	{
		final StringBuilder query = new StringBuilder("SELECT {" + ClassificationAttributeValueModel.PK + "} FROM ");
		query.append(" {" + ClassificationAttributeValueModel._TYPECODE + "} WHERE ");
		query.append(
				" {" + ClassificationAttributeValueModel.SYSTEMVERSION + "}=?" + ClassificationAttributeValueModel.SYSTEMVERSION);
		query.append(" AND {" + ClassificationAttributeValueModel.CODE + "} LIKE ?" + ClassificationAttributeValueModel.CODE);

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());
		searchQuery.addQueryParameter(ClassificationAttributeValueModel.SYSTEMVERSION, systemVersion);
		searchQuery.addQueryParameter(ClassificationAttributeValueModel.CODE, code + "_%");

		final SearchResult<ClassificationAttributeValueModel> result = getFlexibleSearchService().search(searchQuery);
		return result.getResult();
	}

	@Override
	public String getValueLongText(final CsticValueModel valueModel, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled)
	{
		String longText;
		String source;
		final String hybrisLongText = hybrisNames.getValueDescriptions().get(getValueKey(valueModel, csticModel));
		if (!StringUtils.isEmpty(hybrisLongText))
		{
			longText = hybrisLongText;
			source = SOURCE_HYBRIS;
		}
		else
		{
			longText = valueModel.getLongText();
			source = SOURCE_MODEL;
		}
		if (StringUtils.isEmpty(longText))
		{
			longText = null;
		}

		logValue(CSTIC_VALUE_MODEL, VALUE_NAME, valueModel.getName(), LONG_TEXT, source, longText, isDebugEnabled);
		return longText;
	}

}
