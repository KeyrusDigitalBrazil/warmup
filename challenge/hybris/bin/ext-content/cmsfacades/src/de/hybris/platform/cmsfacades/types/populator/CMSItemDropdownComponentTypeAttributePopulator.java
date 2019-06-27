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
package de.hybris.platform.cmsfacades.types.populator;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator aimed at setting all necessary information for the receiving end to build a cms item dropdown widget:
 * <ul>
 * <li>identifies the cmsStructureType as {@link #CMS_ITEM_DROPDOWN}</li>
 * <li>marks the dropdown to use {@link #ID_ATTRIBUTE} as idAttribute</li>
 * </ul>
 */
public class CMSItemDropdownComponentTypeAttributePopulator implements
		Populator<AttributeDescriptorModel, ComponentTypeAttributeData>
{

	private static final String MODEL_CLASSES_PATERN = "(.*)Model$";

	private TypeService typeService;
	private ObjectFactory<ComponentTypeData> componentTypeDataFactory;
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;
	private I18nComponentTypePopulator i18nComponentTypePopulator;
	private String searchParams;
	private String placeholder;

	private static final String ID_ATTRIBUTE = "uuid";
	private static final String LABEL_ATTRIBUTE_NAME = "name";
	private static final String LABEL_ATTRIBUTE_UID = "uid";
	private final String TYPE_CODE = "typeCode";
	private final String ITEM_SEARCH_PARAMS_KEY = "itemSearchParams";

	private static final String CMS_ITEM_DROPDOWN = "CMSItemDropdown";

	@Override
	public void populate(final AttributeDescriptorModel source, final ComponentTypeAttributeData target)
			throws ConversionException
	{
		target.setCmsStructureType(CMS_ITEM_DROPDOWN);
		target.setIdAttribute(ID_ATTRIBUTE);
		target.setLabelAttributes(asList(LABEL_ATTRIBUTE_NAME, LABEL_ATTRIBUTE_UID));

		final Class<?> type = getAttributeDescriptorModelHelperService().getAttributeClass(source);

		target.setParams(this.getComponentParams(type, target));
		target.setSubTypes(this.getComponentSubTypes(type));

		setPlaceholder(target);
	}

	/**
	 * Sets placeholder for a dropdown if it's not empty.
	 * @param target the {@link ComponentTypeAttributeData}
	 */
	protected void setPlaceholder(final ComponentTypeAttributeData target)
	{
		if (isNotBlank(getPlaceholder()))
		{
			final String placeholder = Localization.getLocalizedString(getPlaceholder());
			target.setPlaceholder(placeholder);
		}
	}

	protected Map<String, String> getComponentParams(final Class<?> type, final ComponentTypeAttributeData target)
	{
		final Map<String, String> paramsMap = ofNullable(target.getParams()).orElse(new HashMap<>());
		paramsMap.put(TYPE_CODE, type.getSimpleName().replaceAll(MODEL_CLASSES_PATERN, "$1"));

		if (isNotBlank(getSearchParams()))
		{
			paramsMap.put(ITEM_SEARCH_PARAMS_KEY, getSearchParams());
		}

		return paramsMap;
	}

	/**
	 * This method retrieves a map of concrete subtypes of the provided type. (If the provided type
	 * is concrete it will also be included in the map).
	 * @param type The type for which to retrieve its map of subtypes.
	 * @return map Map of concrete component subtypes. The key is the code of the sub-type and the value is its
	 * i18n key.
	 */
	protected Map<String, String> getComponentSubTypes(final Class<?> type)
	{
		final ComposedTypeModel abstractPageComposedTypeModel = this.getTypeService().getComposedTypeForClass(AbstractPageModel.class);
		final ComposedTypeModel composedTypeModel = this.getTypeService().getComposedTypeForClass(type);

		final ArrayList<ComposedTypeModel> supportedSubTypes = new ArrayList<>(composedTypeModel.getAllSubTypes());
		if( !composedTypeModel.getAbstract() )
		{
			// If the original type itself is not abstract it should also be returned as a supported SubType.
			supportedSubTypes.add(composedTypeModel);
		}

		return supportedSubTypes.stream()
				.filter(ctm -> !typeService.isAssignableFrom(abstractPageComposedTypeModel, ctm))
				.collect(Collectors.toMap(ComposedTypeModel::getCode,
				typeModel -> getComponentTypeI18nKey(typeModel)));
	}

	/**
	 * This method retrieves the i18n key of the provided component type.
	 *
	 * @param typeModel The type for which to retrieve its map of subtypes.
	 * @return String The i18n key of the provided component type.
	 */
	protected String getComponentTypeI18nKey(final ComposedTypeModel typeModel)
	{
		final ComponentTypeData componentTypeData  = getComponentTypeDataFactory().getObject();
		getI18nComponentTypePopulator().populate(typeModel, componentTypeData);
		return componentTypeData.getI18nKey();
	}

	@Required
	public void setAttributeDescriptorModelHelperService(
			final AttributeDescriptorModelHelperService attributeDescriptorModelHelperService)
	{
		this.attributeDescriptorModelHelperService = attributeDescriptorModelHelperService;
	}

	protected AttributeDescriptorModelHelperService getAttributeDescriptorModelHelperService()
	{
		return attributeDescriptorModelHelperService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected  TypeService getTypeService()
	{
		return this.typeService;
	}

	@Required
	public void setI18nComponentTypePopulator(final I18nComponentTypePopulator i18nComponentTypePopulator)
	{
		this.i18nComponentTypePopulator = i18nComponentTypePopulator;
	}

	protected I18nComponentTypePopulator getI18nComponentTypePopulator()
	{
		return i18nComponentTypePopulator;
	}

	@Required
	public void setComponentTypeDataFactory(final ObjectFactory<ComponentTypeData> componentTypeDataFactory)
	{
		this.componentTypeDataFactory = componentTypeDataFactory;
	}

	protected ObjectFactory<ComponentTypeData> getComponentTypeDataFactory()
	{
		return componentTypeDataFactory;
	}

	/**
	 * If searchParams is set, the value will be added to the "params" map for the "itemSearchParams" key.
	 * This is useful when the "itemSearchParams" query-parameter needs to be set when calling the CMS Item Search API.
	 *
	 * Example of usage of this setter:
	 * <pre>
	 * {@code
	 * 	<property name="searchParams" value="pageStatus:active" />
	 * }
	 * </pre>
	 *
	 * @param searchParams
	 *           the searchParams value
	 */
	public void setSearchParams(final String searchParams)
	{
		this.searchParams = searchParams;
	}

	protected String getSearchParams()
	{
		return searchParams;
	}

	protected String getPlaceholder()
	{
		return placeholder;
	}

	public void setPlaceholder(final String placeholder)
	{
		this.placeholder = placeholder;
	}
}
