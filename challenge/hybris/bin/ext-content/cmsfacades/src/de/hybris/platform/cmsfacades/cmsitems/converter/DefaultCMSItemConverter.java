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
package de.hybris.platform.cmsfacades.cmsitems.converter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static de.hybris.platform.cms2.common.functions.impl.Functions.ofSupplierConstrainedBy;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.CMSITEMS_INVALID_CONVERSION_ERROR;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONE_COMPONENT;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_COMPONENT_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CLONE_MODEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_MAP;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.cms2.cloning.strategy.impl.ComponentCloningStrategy;
import de.hybris.platform.cms2.cmsitems.converter.AttributeContentConverter;
import de.hybris.platform.cms2.cmsitems.converter.AttributeStrategyConverterProvider;
import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.cmsitems.AttributeContentValidator;
import de.hybris.platform.cmsfacades.cmsitems.AttributeValueToRepresentationStrategy;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemValidator;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.cmsitems.OriginalClonedItemProvider;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.common.predicate.attributes.NestedOrPartOfAttributePredicate;
import de.hybris.platform.cmsfacades.common.validator.ValidatableService;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrors;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultValidationErrors;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.types.service.CMSPermissionChecker;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.cmsfacades.validator.data.ValidationError;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * The CMSItemConverter is the first layer of converters applied to convert a given {@code CMSItemModel} into
 * {@code Map<String, Object>}. The reason why this class accepts any kind of {@link ItemModel} is that this same
 * converter will be reused recursively when attributes are part of ({@link AttributeDescriptorModel#getPartOf()}) the
 * enclosing item model, hence denoting a composition. This converter is also handling localized attributes and
 * collections.
 */
public class DefaultCMSItemConverter implements CMSItemConverter
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCMSItemConverter.class);

	private TypeService typeService;
	private ModelService modelService;
	private LocalizedPopulator localizedPopulator;
	private AttributeStrategyConverterProvider attributeStrategyConverter;
	private Converter<Date, String> dateConverter;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private CMSAdminItemService cmsAdminItemService;
	private AttributeContentValidator<?> baseAttributeContentValidator;
	private AttributeContentValidator<?> extendedAttributeContentValidator;
	private ValidationErrorsProvider validationErrorsProvider;
	private NestedOrPartOfAttributePredicate nestedOrPartOfAttributePredicate;
	private ValidatableService validatableService;
	private CMSItemValidator<ItemModel> cmsItemValidatorCreate;
	private CMSItemValidator<ItemModel> cmsItemValidatorUpdate;
	private OriginalClonedItemProvider<ItemModel> originalClonedItemProvider;
	private ComponentCloningStrategy componentCloningStrategy;
	private CloneComponentContextProvider cloneComponentContextProvider;
	private AttributeStrategyConverterProvider cloneAttributeStrategyConverter;
	private List<Populator<ItemModel, Map<String, Object>>> customPopulators;
	private AttributeValueToRepresentationStrategy attributeValueToRepresentationStrategy;
	private ComposedTypeToAttributeCollectionConverter composedTypeToAttributeCollectionConverter;
	private PermissionCRUDService permissionCRUDService;
	private CMSPermissionChecker cmsPermissionChecker;
	private Predicate<AttributeDescriptorModel> isCollectionPredicate;

	private CMSUserService cmsUserService;

	@Override
	public Map<String, Object> convert(final ItemModel source)
	{
		checkArgument(nonNull(source), "Item should not be null");

		final Map<String, Object> object = getAttributeValues() //
				.apply(source) //
				.entrySet() //
				.stream() //
				.collect(toMap(entry -> entry.getKey().getQualifier(), Map.Entry::getValue));

		getCustomPopulators().forEach(populator -> populator.populate(source, object));
		return object;
	}

	/*
	 * Suppress sonar warning (squid:S2259 | Null pointers should not be dereferenced) : "map" is already validated not
	 * to be null.
	 */
	@SuppressWarnings("squid:S2259")
	@Override
	public ItemModel convert(final Map<String, Object> map)
	{
		checkArgument(nonNull(map), "map should not be null");

		final String itemType = (String) map.get(ItemModel.ITEMTYPE);
		checkArgument(nonNull(itemType), "(sub)map should contain a value for key " + ItemModel.ITEMTYPE);

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(itemType);

		final String uuid = (String) map.get(FIELD_UUID);

		try
		{
			return getValidatableService().execute(() -> convertAndValidate(map, composedType));
		}
		catch (final UnknownIdentifierException e)
		{
			throw new ConversionException("could not convert map for uuid: [" + uuid + "].", e);
		}
		catch (final ValidationException e)
		{
			e.getValidationErrors().getValidationErrors()
					.forEach(error -> LOGGER.info(String.format("ValidationError for field: %s, lang: %s, code: %s", //
							error.getField(), error.getLanguage(), error.getErrorCode())));
			throw e;
		}
	}

	/**
	 * Converts and validates a deserializable {@link Map} to an {@link ItemModel}.
	 *
	 * @param map
	 *           the Map<String, Object> to convert
	 * @param composedType
	 *           the type to which the {@link Map} will be converted to.
	 * @return the {@link Map} converted to an {@link ItemModel}
	 */
	protected ItemModel convertAndValidate(final Map<String, Object> map, final ComposedTypeModel composedType)
	{
		final ItemModel itemModel = getItemModelFromRepresentation(map);

		try
		{
			getOriginalClonedItemProvider().initializeItem(itemModel);
			final List<AttributeDescriptorModel> attributes = getAttributes(composedType);
			attributes.stream() //
					.filter(AttributeDescriptorModel::getWritable) //
					.filter(attr -> !this.isDynamicAttribute(attr))
					.filter(attribute -> principalCanUpdateAttribute(composedType.getCode(), attribute.getQualifier())) //
					.forEach(attribute -> convertAndValidateAttribute(map, itemModel, attribute));

			getCmsItemValidator(map).validate(itemModel);
		}
		finally
		{
			getOriginalClonedItemProvider().finalizeItem();
		}
		return itemModel;
	}

	/**
	 * Converts and validates a primitive data or deserializable {@link Map} to an attribute value (primitive or
	 * {@link ItemModel}).
	 *
	 * @param map
	 *           the Map<String, Object> containing the attribute data to convert
	 * @param itemModel
	 *           the model which attribute value will be updated
	 * @param attribute
	 *           the attribute descriptor providing information about the attribute to be updated
	 */
	protected void convertAndValidateAttribute(final Map<String, Object> map, final ItemModel itemModel,
			final AttributeDescriptorModel attribute)
	{
		final String qualifier = attribute.getQualifier();
		final Object objValue = map.get(qualifier);

		final boolean hasNoDefaultValueOrIsNotNull = objValue != null || attribute.getDefaultValue() == null;
		final boolean isCloneComponentFlow = getCloneComponentContextProvider().isInitialized() && isCloneComponentFlow(map);
		final boolean isCloneComponentValueModified = //
				isCloneComponentFlow && initializeCloneComponentAttributeContext(qualifier, objValue);

		if (hasNoDefaultValueOrIsNotNull && (!isCloneComponentFlow || isCloneComponentValueModified))
		{
			/*
			 * skip set attribute only when objValue is null and a default value exists the default value will be applied
			 * when saving the model
			 */
			try
			{
				validate(objValue, attribute, getBaseAttributeContentValidator());

				final Object attributeValue = objValue == null ? null
						: convertRepresentationToAttributeValue(objValue, itemModel).apply(attribute).orElseGet(() -> null);
				getModelService().setAttributeValue(itemModel, qualifier, attributeValue);
			}
			catch (final ValidationException e)
			{
				collectValidationErrors(e, empty(), empty());
			}
			catch (final AttributeNotSupportedException e)
			{
				// attribute may not be writable
			}
		}
	}

	/**
	 * Check that the value for the given qualifier was modified during the cloning process and stores the source
	 * component attribute value in the clone component context in the session.
	 *
	 * @param qualifier
	 *           the attribute field name
	 * @param targetValue
	 *           the value to be applied to the component model
	 * @return <tt>TRUE</tt> when the attribute value was modified during the component cloning flow; <br>
	 *         <tt>FALSE</tt> when the given qualifier is {@code UID} or when the attribute value was not modified
	 */
	@SuppressWarnings("unchecked")
	protected boolean initializeCloneComponentAttributeContext(final String qualifier, final Object targetValue)
	{
		if (!getCloneComponentContextProvider().isInitialized())
		{
			throw new IllegalStateException("CloneComponentContextProvider must be initialized to perform this operation.");
		}

		// clone component flow
		final Object srcMapValue = getCloneComponentContextProvider().findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_MAP);
		final Object srcAttributeValue = ((Map<String, Object>) srcMapValue).get(qualifier);

		final boolean isValueAddedOrUpdated = targetValue != null && !targetValue.equals(srcAttributeValue);
		final boolean isValueRemoved = srcAttributeValue != null && !srcAttributeValue.equals(targetValue);

		// skip set attribute when the value was not modified during the component clone flow
		final boolean isCloneComponentValueModified;

		// skip set attribute for the attribute UID
		// the clone component model already has a newly generated unique UID, don't override it
		if (!CMSItemModel.UID.equals(qualifier) && (isValueAddedOrUpdated || isValueRemoved))
		{
			getCloneComponentContextProvider()
					.initializeItem(new SimpleImmutableEntry<>(SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE, srcAttributeValue));
			isCloneComponentValueModified = true;
		}
		else
		{
			isCloneComponentValueModified = false;
		}
		return isCloneComponentValueModified;
	}

	protected CMSItemValidator<ItemModel> getCmsItemValidator(final Map<String, Object> map)
	{
		final boolean hasUuid = modelHasAssignedUUID(map);
		if (hasUuid)
		{
			return getCmsItemValidatorUpdate();
		}
		else
		{
			return getCmsItemValidatorCreate();
		}
	}

	/**
	 * Get Item Model from request payload
	 *
	 * @param map
	 *           the map representing the Item Model
	 * @return the ItemModel related to this Map representation
	 */
	protected ItemModel getItemModelFromRepresentation(final Map<String, Object> map)
	{
		final String itemType = (String) map.get(ItemModel.ITEMTYPE);
		final String uuid = (String) map.get(FIELD_UUID);

		final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(itemType);
		final Class<? extends ItemModel> modelClass = getTypeService().getModelClass(composedType);

		final ItemModel model;
		if (isNotBlank(uuid))
		{
			// update flow
			model = getUniqueItemIdentifierService() //
					.getItemModel(uuid, modelClass) //
					.orElseThrow(() -> new ConversionException("unknown uuid was provided: " + uuid));
		}
		else if (isCloneComponentFlow(map))
		{
			// clone component flow
			model = getCloneModelFromRepresentation((String) map.get(FIELD_COMPONENT_UUID));
		}
		else if (CMSItemModel.class.isAssignableFrom(modelClass))
		{
			// create flow
			final String uid = (String) map.get(CMSItemModel.UID);
			model = getCmsAdminItemService().createItem((Class<? extends CMSItemModel>) modelClass);
			if (isNotBlank(uid))
			{
				((CMSItemModel) model).setUid(uid);
			}
			else
			{
				map.put(CMSItemModel.UID, ((CMSItemModel) model).getUid());
			}
		}
		else
		{
			// create flow
			model = getModelService().create(modelClass);
			getModelService().initDefaults(model);
		}
		return model;
	}

	/**
	 * Determine if a map that represents the item model is a component clone
	 *
	 * @param map
	 *           the map representing the Item Model
	 * @return a boolean that determines if the item being created is a component clone
	 */
	protected boolean isCloneComponentFlow(final Map<String, Object> map)
	{
		final String sourceComponentUUID = (String) map.get(FIELD_COMPONENT_UUID);
		final Boolean shouldCloneComponent = (Boolean) map.get(FIELD_CLONE_COMPONENT);
		return isNotBlank(sourceComponentUUID) && shouldCloneComponent.booleanValue();
	}

	/**
	 * Create a Clone Item Model and saves it to the {@link CloneComponentContextProvider} or get a Clone Item Model from
	 * the {@code CloneComponentContextProvider}
	 *
	 * @param sourceComponentUuid
	 *           the UUID representing the source Item Model to clone from
	 * @return the ItemModel cloned from the provided {@code sourceComponentUuid}
	 */
	protected ItemModel getCloneModelFromRepresentation(final String sourceComponentUuid)
	{
		final ItemModel model;
		if (!getCloneComponentContextProvider().isInitialized())
		{
			// this must only be called once per session
			final AbstractCMSComponentModel sourceComponentModel = getUniqueItemIdentifierService() //
					.getItemModel(sourceComponentUuid, AbstractCMSComponentModel.class) //
					.orElseThrow(() -> new ConversionException("unknown component uuid was provided: " + sourceComponentUuid));
			try
			{
				model = getComponentCloningStrategy().clone(sourceComponentModel, Optional.empty(), Optional.empty());

				getCloneComponentContextProvider()
						.initializeItem(new SimpleImmutableEntry<>(SESSION_CLONE_COMPONENT_SOURCE_MAP, convert(sourceComponentModel)));
				getCloneComponentContextProvider()
						.initializeItem(new SimpleImmutableEntry<>(SESSION_CLONE_COMPONENT_CLONE_MODEL, model));
			}
			catch (final CMSItemNotFoundException e)
			{
				throw new ConversionException("Failed to clone component from component uuid: " + sourceComponentUuid);
			}
		}
		else
		{
			model = (ItemModel) getCloneComponentContextProvider().findItemForKey(SESSION_CLONE_COMPONENT_CLONE_MODEL);
		}
		return model;
	}

	/**
	 * Convenience method to apply a transformation to all elements of a {@link Function}
	 *
	 * @param collection
	 *           the {@link Collection} the elements of which we need to transform
	 * @param transform
	 *           the transformation {@link Function} that will be applied on each element of the collection
	 * @return a new collection
	 */
	protected Collection<Object> transformCollection(final AttributeDescriptorModel attribute, final Collection<Object> collection,
			final Function<Object, Object> transform)
	{
		if (collection == null)
		{
			return collection;
		}
		final AtomicInteger counter = new AtomicInteger(0);
		final Collection<Object> transformedCollection = newArrayList();
		collection.iterator().forEachRemaining(value -> {
			final Integer index = counter.getAndIncrement();
			try
			{
				transformedCollection.add(transform.apply(value));
			}
			catch (final ValidationException e)
			{
				collectValidationErrors(e, empty(), of(index));
			}
			catch (final ConversionException e)
			{
				LOGGER.error(String.format("Error converting attribute for [%s] with value [%s]", attribute.getQualifier(), value),
						e);
				getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
						.field(attribute.getQualifier()) //
						.rejectedValue(value) //
						.position(index) //
						.errorCode(CMSITEMS_INVALID_CONVERSION_ERROR) //
						.exceptionMessage(e.getMessage()) //
						.build());
			}
		});
		return transformedCollection;
	}

	/**
	 * Convenience method to apply a transformation to values of a {@link Map}
	 *
	 * @param itemMap
	 *           the {@link Map} the values of which we need to transform
	 * @param itemModel
	 *           the model containing the original attribute value. If this is a new model, the attribute value will be
	 *           empty.
	 * @param transform
	 *           the transformation {@link Function} that will be applied on each values of the itemMap
	 * @return a new itemMap
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> transformLocalizedValue(final AttributeDescriptorModel attribute,
			final Map<String, Object> itemMap, final ItemModel itemModel, final Function<Object, Object> transform)
	{
		// BiFunction to transform value
		final BiFunction<String, Object, Object> transformValue = (language, value) -> {
			try
			{
				return transform.apply(value);
			}
			catch (final ValidationException e)
			{
				collectValidationErrors(e, of(language), empty());
			}
			catch (final ConversionException e)
			{
				LOGGER.error(String.format("Error converting attribute for [%s] and language [%s] with value [%s]",
						attribute.getQualifier(), language, value), e);
				getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
						.field(attribute.getQualifier()) //
						.language(language) //
						.rejectedValue(value) //
						.errorCode(CMSITEMS_INVALID_CONVERSION_ERROR) //
						.exceptionMessage(e.getMessage()) //
						.build());
			}
			return null;
		};

		if (itemMap != null)
		{
			final Map<String, Object> responseMap = new HashMap<>();
			final Set<String> writeableLanguages = getCmsUserService().getWriteableLanguagesForCurrentUser();
			final Set<String> restrictedLanguages = new HashSet<>();

			itemMap.entrySet().forEach(entry -> {
				if (getCloneComponentContextProvider().isInitialized())
				{
					getCloneComponentContextProvider()
							.initializeItem(new SimpleImmutableEntry<>(SESSION_CLONE_COMPONENT_LOCALE, entry.getKey()));
				}

				final String languageIsoCode = entry.getKey();
				// Note: To support deprecated methods, if the item model is null, no language permissions are checked; the value set
				// by the call is used.
				if (Objects.isNull(itemModel)
						|| principalCanWriteAttributeLanguage(attribute.getQualifier(), languageIsoCode, writeableLanguages))
				{
					responseMap.put(languageIsoCode, transformValue.apply(languageIsoCode, entry.getValue()));
				}
				else
				{
					restrictedLanguages.add(languageIsoCode);
				}
			});

			if (!restrictedLanguages.isEmpty())
			{
				convertAttributeValueToRepresentation(itemModel) //
						.apply(attribute) //
						.ifPresent(rawValue -> {
							final Map<String, Object> storedValues = (Map<String, Object>) rawValue;
							restrictedLanguages.forEach(languageKey -> {
								final Object result = transformValue.apply(languageKey, storedValues.getOrDefault(languageKey, null));
								responseMap.put(languageKey, result);
							});
						});
			}

			return responseMap;
		}
		return null;
	}

	/**
	 * Given an {@link AttributeDescriptorModel}, will return a {@link Function} that will do the following: <br/>
	 * if the attribute is a partOf of the owning type, it will return a conversion of it by means of
	 * {@link DefaultCMSItemConverter#convert(ItemModel)} <br/>
	 * if the attribute is not a partOf of the owning type, it will return a conversion of it by means of the appropriate
	 * model-to-data converter returned by the attributeStrategyConverter.
	 *
	 * @param attribute
	 *           the {@link AttributeDescriptorModel} describing the metadata of the property of a class
	 * @return a conversion of type Object
	 */
	protected Function<Object, Object> leafOrDeeperConvertToRepresentation(final AttributeDescriptorModel attribute)
	{
		return value -> {
			if (value != null)
			{
				if (getNestedOrPartOfAttributePredicate().test(attribute))
				{
					return convert((ItemModel) value);
				}
				else
				{
					return getAttributeStrategyConverter().getContentConverter(attribute).convertModelToData(attribute, value);
				}
			}
			return null;
		};
	}

	/**
	 * Given an {@link AttributeDescriptorModel}, will return a {@link Function} that will do the following: <br/>
	 * if the attribute is a partOf of the owning type, it will return a conversion of it by means of
	 * {@link DefaultCMSItemConverter#convert(Map<String, Object>)} <br/>
	 * if the attribute is not a partOf of the owning type, it will return a conversion of it by means of the appropriate
	 * data-to-model converter returned by the attributeStrategyConverter. if no converter is found,
	 * {@link AttributeContentConverter.Value.NOT_TO_BE_SET} is returned to instruct no to try to persist this property
	 *
	 * @param attribute
	 *           the {@link AttributeDescriptorModel} describing the metadata of the property of a class
	 * @return a conversion of type Object
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	protected Function<Object, Object> leafOrDeeperConvertToModel(final AttributeDescriptorModel attribute)
	{
		return value -> {
			if (value != null)
			{
				if (getNestedOrPartOfAttributePredicate().test(attribute) && value instanceof Map)
				{
					try
					{
						getValidationErrorsProvider().getCurrentValidationErrors().pushField(attribute.getQualifier());
						return convert((Map<String, Object>) value);
					}
					finally
					{
						getValidationErrorsProvider().getCurrentValidationErrors().popField();
					}
				}
				else
				{
					validate(value, attribute, getExtendedAttributeContentValidator());
					final AttributeContentConverter attributeContentConverter;
					if (getCloneComponentContextProvider().isInitialized())
					{
						attributeContentConverter = Optional
								.ofNullable(getCloneAttributeStrategyConverter().getContentConverter(attribute))
								.orElse(getAttributeStrategyConverter().getContentConverter(attribute));
					}
					else
					{
						attributeContentConverter = getAttributeStrategyConverter().getContentConverter(attribute);
					}
					return attributeContentConverter.convertDataToModel(attribute, value);
				}
			}
			return null;
		};
	}

	/**
	 * Returns true if the attribute is a dynamic attribute. Note that this method doesn't consider
	 * RelationDescriptorModels as dynamic.
	 *
	 * @param attribute
	 *           The attribute to evaluate.
	 * @return true if the attribute is dynamic, false otherwise.
	 */
	protected boolean isDynamicAttribute(final AttributeDescriptorModel attribute)
	{
		return attribute.getItemtype().equals(AttributeDescriptorModel._TYPECODE) && !attribute.getProperty();
	}

	/**
	 * Returns the list of all attributes defined for a given composed type, including the inherited ones.
	 *
	 * @param composedType
	 *           the composedType the attributes belong to
	 * @return a list of all attributes declared, including inherited attributes.
	 */
	protected List<AttributeDescriptorModel> getAttributes(final ComposedTypeModel composedType)
	{
		/* we only persist properties for which a converter was found */
		return getComposedTypeToAttributeCollectionConverter().convert(composedType).stream()
				.filter(attribute -> attribute.getPartOf() || getAttributeStrategyConverter().getContentConverter(attribute) != null)
				.collect(toList());
	}

	/**
	 * Function to get a Map of attribute descriptors and its {@code Object} value for a given {@link ItemModel}
	 *
	 * @return a function that when executed returns a map with attributes and their respective values.
	 */
	protected Function<ItemModel, Map<AttributeDescriptorModel, Object>> getAttributeValues()
	{
		return item -> {
			final ComposedTypeModel composedType = getTypeService().getComposedTypeForCode(item.getItemtype());
			final Predicate<AttributeDescriptorModel> canReadAttribute = attribute -> getPermissionCRUDService()
					.canReadAttribute(composedType.getCode(), attribute.getQualifier());

			return getAttributes(composedType).stream() //
					.collect(toMap(identity(), convertAttributeValueToRepresentation(item))) //
					.entrySet().stream() //
					// Write permissions are handled by the type structure
					.filter(entry -> canReadAttribute.test(entry.getKey())) //
					.filter(
							entry -> getCmsPermissionChecker().hasPermissionForContainedType(entry.getKey(), PermissionsConstants.READ))
					// null properties are not serialized
					.filter(entry -> entry.getValue().isPresent()) //
					.collect(toMap(Map.Entry::getKey, entry -> entry.getValue().get()));
		};
	}

	/**
	 * Function that returns the serializable representation value (String, Map or Collection) of all
	 * {@link AttributeDescriptorModel} of a given ItemModel source. This methods recursively handles the following cases
	 * and their possible combinations:
	 * <ul>
	 * <li>localized field</li>
	 * <li>collections</li>
	 * <li>partOf</li>
	 * </ul>
	 *
	 * @param source
	 *           the {@code ItemModel} owning all the {@link AttributeDescriptorModel}
	 * @return returns the serializable representations
	 */
	@SuppressWarnings("unchecked")
	protected Function<AttributeDescriptorModel, Optional<Object>> convertAttributeValueToRepresentation(final ItemModel source)
	{
		return attribute -> {
			final Function<Object, Object> goDeeperOrSerialize = leafOrDeeperConvertToRepresentation(attribute);

			final Supplier<Object> localizedCollectionGetter = getAttributeValueToRepresentationStrategy()
					.getLocalizedCollectionGetter(attribute, source, goDeeperOrSerialize);

			final Supplier<Object> localizedGetter = getAttributeValueToRepresentationStrategy().getLocalizedGetter(attribute,
					source, goDeeperOrSerialize);

			final Supplier<Object> collectionGetter = getAttributeValueToRepresentationStrategy().getCollectionGetter(attribute,
					source, goDeeperOrSerialize);

			final Supplier<Object> simpleGetter = getAttributeValueToRepresentationStrategy().getSimpleGetter(attribute, source,
					goDeeperOrSerialize);

			try
			{
				return getAttributeValue(localizedCollectionGetter, localizedGetter, collectionGetter, simpleGetter).apply(attribute);
			}
			catch (final AttributeNotSupportedException e)
			{
				// attribute may not be readable
				return empty();
			}

		};
	}

	/**
	 * Function that constructs the persistable representation of all {@link AttributeDescriptorModel} of a given
	 * serializable source (String, Map or Collection). This methods recursively handles the following cases and their
	 * possible combinations:
	 * <ul>
	 * <li>localized field</li>
	 * <li>collections</li>
	 * <li>partOf</li>
	 * </ul>
	 *
	 * @param source
	 *           serializable source (String, Map or Collection) described by a {@link AttributeDescriptorModel}
	 * @param itemModel
	 *           the model containing the original attribute value. If this is a new model, the attribute value will be
	 *           empty.
	 * @return returns the persistable representations
	 */
	@SuppressWarnings("unchecked")
	protected Function<AttributeDescriptorModel, Optional<Object>> convertRepresentationToAttributeValue(final Object source,
			final ItemModel itemModel)
	{
		return attribute -> {
			final Function<Object, Object> goDeeperOrDeSerialize = leafOrDeeperConvertToModel(attribute);

			final Supplier<Object> localizedCollectionGetter = () -> {
				final Object mapValue = transformLocalizedValue(attribute, (Map<String, Object>) source, itemModel,
						e -> transformCollection(attribute, (Collection<Object>) e, goDeeperOrDeSerialize));

				return getLocalizedPopulator().populateAsMapOfLocales(((Map<String, Object>) mapValue)::get);
			};

			final Supplier<Object> localizedGetter = () -> {
				final Object mapValue = transformLocalizedValue(attribute, (Map<String, Object>) source, itemModel,
						goDeeperOrDeSerialize);
				if (mapValue != null)
				{
					return getLocalizedPopulator().populateAsMapOfLocales(((Map<String, Object>) mapValue)::get);
				}
				return null;
			};

			final Supplier<Object> collectionGetter = () -> transformCollection(attribute, (Collection<Object>) source,
					goDeeperOrDeSerialize);

			final Supplier<Object> simpleGetter = () -> {
				try
				{
					return goDeeperOrDeSerialize.apply(source);
				}
				catch (final ValidationException e)
				{
					LOGGER.info("Error validating attribute for [" + attribute.getQualifier() + "] with value [" + source + "]", e);
					e.getValidationErrors().getValidationErrors()
							.forEach(validationError -> getValidationErrorsProvider().getCurrentValidationErrors().add(validationError));
				}
				catch (final ConversionException e)
				{
					LOGGER.error("Error converting attribute for [" + attribute.getQualifier() + "] with value [" + source + "]", e);
					getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
							.field(attribute.getQualifier()) //
							.errorCode(CMSITEMS_INVALID_CONVERSION_ERROR) //
							.exceptionMessage(e.getMessage()) //
							.build());
				}
				return empty();
			};

			return getAttributeValue(localizedCollectionGetter, localizedGetter, collectionGetter, simpleGetter).apply(attribute);
		};
	}

	/**
	 * Returns a {@link Function} aimed at converting any value described by a {@link AttributeDescriptorModel}. This
	 * function will execute one of the provided suppliers depending on whether the attribute is localized and/or a
	 * collection
	 *
	 * @param localizedCollectionGetter
	 *           a {@link Supplier} invoked if the attribute is both localized and a collection
	 * @param localizedGetter
	 *           a {@link Supplier} invoked if the attribute is localized and not a collection
	 * @param collectionGetter
	 *           a {@link Supplier} invoked if the attribute is a collection and not localized
	 * @param simpleGetter
	 *           a {@link Supplier} invoked if the attribute is neither localized nor an attribute
	 * @return a {@link Function} to convert a value
	 */
	protected Function<AttributeDescriptorModel, Optional<Object>> getAttributeValue(
			final Supplier<Object> localizedCollectionGetter, final Supplier<Object> localizedGetter,
			final Supplier<Object> collectionGetter, final Supplier<Object> simpleGetter)
	{

		final Predicate<AttributeDescriptorModel> isLocalized = AttributeDescriptorModel::getLocalized;
		final Predicate<AttributeDescriptorModel> isCollection = getIsCollectionPredicate();

		return ofSupplierConstrainedBy(localizedCollectionGetter, isLocalized.and(isCollection))
				.orElse(ofSupplierConstrainedBy(localizedGetter, isLocalized.and(isCollection.negate())))
				.orElse(ofSupplierConstrainedBy(collectionGetter, isLocalized.negate().and(isCollection)))
				.orElse(ofSupplierConstrainedBy(simpleGetter, isLocalized.negate().and(isCollection.negate())));
	}

	/**
	 * Validates and throws an exception if there are validation errors
	 *
	 * @param value
	 *           the value being validated
	 * @param attribute
	 *           the attribute descriptor model
	 * @param validator
	 *           the validator to be used
	 */
	protected void validate(final Object value, final AttributeDescriptorModel attribute,
			final AttributeContentValidator validator)
	{
		final List<ValidationError> errors = validator.validate(value, attribute);
		if (!CollectionUtils.isEmpty(errors))
		{
			final ValidationErrors localValidationErrors = new DefaultValidationErrors();
			errors.forEach(localValidationErrors::add);
			throw new ValidationException(localValidationErrors);
		}
	}

	/**
	 * Collects the errors in the validation exception and adds to the global validation context.
	 *
	 * @param e
	 *           the exception
	 * @param language
	 *           optional; the validated language
	 * @param position
	 *           optional; the position in which the object value in the collection
	 */
	protected void collectValidationErrors(final ValidationException e, final Optional<String> language,
			final Optional<Integer> position)
	{
		e.getValidationErrors().getValidationErrors().forEach(validationError -> {
			language.ifPresent(validationError::setLanguage);
			position.ifPresent(validationError::setPosition);
			getValidationErrorsProvider().getCurrentValidationErrors().add(validationError);
		});
	}

	protected boolean modelHasAssignedUUID(final Map<String, Object> valueMap)
	{
		final String uuid = (String) valueMap.get(FIELD_UUID);
		return !isBlank(uuid);
	}

	/**
	 * This method determines whether the current principal can update the attribute identified by the qualifier in the
	 * enclosing type. A user can update an attribute when both read and change attribute permissions are granted.
	 *
	 * @param typeCode
	 *           the item model type code
	 * @param qualifier
	 *           the attribute qualifier
	 * @return {@code TRUE} when the user has read and change permissions on the attribute; otherwise {@code FALSE}
	 */
	protected boolean principalCanUpdateAttribute(final String typeCode, final String qualifier)
	{
		final boolean canUpdate = getPermissionCRUDService().canReadAttribute(typeCode, qualifier)
				&& getPermissionCRUDService().canChangeAttribute(typeCode, qualifier);

		final AttributeDescriptorModel attribute = getTypeService().getAttributeDescriptor(typeCode, qualifier);
		final boolean canUpdateContainingType = getCmsPermissionChecker().hasPermissionForContainedType(attribute,
				PermissionsConstants.CHANGE);

		if ((!canUpdate || !canUpdateContainingType) && LOGGER.isInfoEnabled())
		{
			LOGGER.info(
					String.format("Skipping %s attribute of %s. Principal does not have required change permissions.", qualifier, typeCode));
		}

		return canUpdate && canUpdateContainingType;
	}

	/**
	 * This method is used to check if the current principal can write a language of a localized attribute.
	 *
	 * @param qualifier
	 *           - the qualifier of the attribute whose language wants to be changed.
	 * @param languageIsoCode
	 *           - the ISO code of the language that wants to be updated.
	 * @param writeableLanguages
	 *           - the list of languages the current principal can write. Each element represents the ISO code of a
	 *           language
	 * @return a boolean flag. True, if the user can write the language for the localized element; False, otherwise.
	 */
	protected boolean principalCanWriteAttributeLanguage(final String qualifier, final String languageIsoCode,
			final Set<String> writeableLanguages)
	{
		final boolean canWrite = writeableLanguages.contains(languageIsoCode);
		if (!canWrite && LOGGER.isInfoEnabled())
		{
			LOGGER.info(String.format("Skipping %s language of attribute %s. Principal does not have required write permissions.",
					languageIsoCode, qualifier));
		}

		return canWrite;
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Deprecated Methods
	// ---------------------------------------------------------------------------------------------------------------
	/**
	 * Function that constructs the persistable representation of all {@link AttributeDescriptorModel} of a given
	 * serializable source (String, Map or Collection). This methods recursively handles the following cases and their
	 * possible combinations:
	 * <ul>
	 * <li>localized field</li>
	 * <li>collections</li>
	 * <li>partOf</li>
	 * </ul>
	 *
	 * @param source
	 *           serializable source (String, Map or Collection) described by a {@link AttributeDescriptorModel}
	 * @return returns the persistable representations
	 *
	 * @deprecated since 1811. Please use
	 *             {@link de.hybris.platform.cmsfacades.cmsitems.converter.DefaultCMSItemConverter#convertRepresentationToAttributeValue(Object, ItemModel)}
	 *             instead.
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "1811")
	protected Function<AttributeDescriptorModel, Optional<Object>> convertRepresentationToAttributeValue(final Object source)
	{
		return convertRepresentationToAttributeValue(source, null);
	}

	/**
	 * Convenience method to apply a transformation to values of a {@link Map}
	 *
	 * @param itemMap
	 *           the {@link Map} the values of which we need to transform
	 * @param transform
	 *           the transformation {@link Function} that will be applied on each values of the itemMap
	 * @return a new itemMap
	 *
	 * @deprecated since 1811. Please use
	 *             {@link de.hybris.platform.cmsfacades.cmsitems.converter.DefaultCMSItemConverter#transformLocalizedValue(AttributeDescriptorModel, Map, ItemModel, Function)}
	 *             instead.
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "1811")
	protected Map<String, Object> transformLocalizedValue(final AttributeDescriptorModel attribute,
			final Map<String, Object> itemMap, final Function<Object, Object> transform)
	{
		return transformLocalizedValue(attribute, itemMap, null, transform);
	}

	// ---------------------------------------------------------------------------------------------------------------
	// Getters/Setters
	// ---------------------------------------------------------------------------------------------------------------
	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}

	public AttributeStrategyConverterProvider getAttributeStrategyConverter()
	{
		return attributeStrategyConverter;
	}

	@Required
	public void setAttributeStrategyConverter(final AttributeStrategyConverterProvider attributeStrategyConverter)
	{
		this.attributeStrategyConverter = attributeStrategyConverter;
	}

	protected Converter<Date, String> getDateConverter()
	{
		return dateConverter;
	}

	/**
	 * @deprecated since 6.7
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.7")
	@Required
	public void setDateConverter(final Converter<Date, String> dateConverter)
	{
		this.dateConverter = dateConverter;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}

	protected AttributeContentValidator getBaseAttributeContentValidator()
	{
		return baseAttributeContentValidator;
	}

	@Required
	public void setBaseAttributeContentValidator(final AttributeContentValidator baseAttributeContentValidator)
	{
		this.baseAttributeContentValidator = baseAttributeContentValidator;
	}

	protected AttributeContentValidator getExtendedAttributeContentValidator()
	{
		return extendedAttributeContentValidator;
	}

	@Required
	public void setExtendedAttributeContentValidator(final AttributeContentValidator extendedAttributeContentValidator)
	{
		this.extendedAttributeContentValidator = extendedAttributeContentValidator;
	}

	protected ValidationErrorsProvider getValidationErrorsProvider()
	{
		return validationErrorsProvider;
	}

	@Required
	public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
	{
		this.validationErrorsProvider = validationErrorsProvider;
	}

	public NestedOrPartOfAttributePredicate getNestedOrPartOfAttributePredicate()
	{
		return nestedOrPartOfAttributePredicate;
	}

	@Required
	public void setNestedOrPartOfAttributePredicate(final NestedOrPartOfAttributePredicate nestedOrPartOfAttributePredicate)
	{
		this.nestedOrPartOfAttributePredicate = nestedOrPartOfAttributePredicate;
	}

	public ValidatableService getValidatableService()
	{
		return validatableService;
	}

	@Required
	public void setValidatableService(final ValidatableService validatableService)
	{
		this.validatableService = validatableService;
	}

	public CMSItemValidator<ItemModel> getCmsItemValidatorCreate()
	{
		return cmsItemValidatorCreate;
	}

	@Required
	public void setCmsItemValidatorCreate(final CMSItemValidator<ItemModel> cmsItemValidatorCreate)
	{
		this.cmsItemValidatorCreate = cmsItemValidatorCreate;
	}

	public CMSItemValidator<ItemModel> getCmsItemValidatorUpdate()
	{
		return cmsItemValidatorUpdate;
	}

	@Required
	public void setCmsItemValidatorUpdate(final CMSItemValidator<ItemModel> cmsItemValidatorUpdate)
	{
		this.cmsItemValidatorUpdate = cmsItemValidatorUpdate;
	}

	protected OriginalClonedItemProvider<ItemModel> getOriginalClonedItemProvider()
	{
		return originalClonedItemProvider;
	}

	@Required
	public void setOriginalClonedItemProvider(final OriginalClonedItemProvider<ItemModel> originalClonedItemProvider)
	{
		this.originalClonedItemProvider = originalClonedItemProvider;
	}

	protected ComponentCloningStrategy getComponentCloningStrategy()
	{
		return componentCloningStrategy;
	}

	@Required
	public void setComponentCloningStrategy(final ComponentCloningStrategy componentCloningStrategy)
	{
		this.componentCloningStrategy = componentCloningStrategy;
	}

	protected CloneComponentContextProvider getCloneComponentContextProvider()
	{
		return cloneComponentContextProvider;
	}

	@Required
	public void setCloneComponentContextProvider(final CloneComponentContextProvider cloneComponentContextProvider)
	{
		this.cloneComponentContextProvider = cloneComponentContextProvider;
	}

	protected AttributeStrategyConverterProvider getCloneAttributeStrategyConverter()
	{
		return cloneAttributeStrategyConverter;
	}

	@Required
	public void setCloneAttributeStrategyConverter(final AttributeStrategyConverterProvider cloneAttributeStrategyConverter)
	{
		this.cloneAttributeStrategyConverter = cloneAttributeStrategyConverter;
	}

	protected AttributeValueToRepresentationStrategy getAttributeValueToRepresentationStrategy()
	{
		return attributeValueToRepresentationStrategy;
	}

	@Required
	public void setAttributeValueToRepresentationStrategy(
			final AttributeValueToRepresentationStrategy attributeValueToRepresentationStrategy)
	{
		this.attributeValueToRepresentationStrategy = attributeValueToRepresentationStrategy;
	}

	protected ComposedTypeToAttributeCollectionConverter getComposedTypeToAttributeCollectionConverter()
	{
		return composedTypeToAttributeCollectionConverter;
	}

	@Required
	public void setComposedTypeToAttributeCollectionConverter(
			final ComposedTypeToAttributeCollectionConverter composedTypeToAttributeCollectionConverter)
	{
		this.composedTypeToAttributeCollectionConverter = composedTypeToAttributeCollectionConverter;
	}

	protected List<Populator<ItemModel, Map<String, Object>>> getCustomPopulators()
	{
		return customPopulators;
	}

	@Required
	public void setCustomPopulators(final List<Populator<ItemModel, Map<String, Object>>> customPopulators)
	{
		this.customPopulators = customPopulators;
	}

	protected PermissionCRUDService getPermissionCRUDService()
	{
		return permissionCRUDService;
	}

	@Required
	public void setPermissionCRUDService(final PermissionCRUDService permissionCRUDService)
	{
		this.permissionCRUDService = permissionCRUDService;
	}

	protected CMSUserService getCmsUserService()
	{
		return cmsUserService;
	}

	@Required
	public void setCmsUserService(final CMSUserService cmsUserService)
	{
		this.cmsUserService = cmsUserService;
	}

	protected CMSPermissionChecker getCmsPermissionChecker()
	{
		return cmsPermissionChecker;
	}

	@Required
	public void setCmsPermissionChecker(final CMSPermissionChecker cmsPermissionChecker)
	{
		this.cmsPermissionChecker = cmsPermissionChecker;
	}

	protected Predicate<AttributeDescriptorModel> getIsCollectionPredicate()
	{
		return isCollectionPredicate;
	}

	@Required
	public void setIsCollectionPredicate(
			final Predicate<AttributeDescriptorModel> isCollectionPredicate)
	{
		this.isCollectionPredicate = isCollectionPredicate;
	}
}
