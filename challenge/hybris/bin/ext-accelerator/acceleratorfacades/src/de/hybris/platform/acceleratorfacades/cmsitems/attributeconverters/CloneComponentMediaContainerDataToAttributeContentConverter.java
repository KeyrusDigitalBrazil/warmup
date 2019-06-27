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
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CLONE_MODEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.collect.Sets;


/**
 * The {@link Converter} is invoked when cloning a component which contain a {@code MediaContainer}. This converts a
 * Map<String, String> representation of a {@code MediaContainerModel} into an actual {@link MediaContainerModel}
 *
 * When cloning a component, the "source" Map contains the values as inputed by the request payload. The
 * "cloneComponentModel" is the component model created by cloning from the source component uuid specified in the
 * request payload.
 *
 * When editing a MediaContainer, if at least one media was modified in the "source" Map for any given MediaFormat, the
 * resulting "cloneComponentModel" should update the reference to the updated "source" Map value. Otherwise, a reference
 * to the cloned media model is used instead.
 */
public class CloneComponentMediaContainerDataToAttributeContentConverter
		implements Converter<Map<String, String>, MediaContainerModel>
{
	private CMSMediaFormatService mediaFormatService;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private MediaContainerFacade mediaContainerFacade;
	private CloneComponentContextProvider cloneComponentContextProvider;
	private ModelService modelService;

	@Override
	public MediaContainerModel convert(final Map<String, String> source)
	{
		if (isNull(source))
		{
			return null;
		}

		final Map<String, MediaFormatModel> mediaFormatModelMap = getMediaFormatService()
				.getMediaFormatsByComponentType(AbstractMediaContainerComponentModel.class).stream()
				.collect(toMap(MediaFormatModel::getQualifier, identity()));

		// find the map representation of the source component
		final Map<String, Object> srcAttributeMap = (Map<String, Object>) getCloneComponentContextProvider()
				.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE);

		// find the model representation of the clone component
		final AbstractMediaContainerComponentModel cloneComponentModel = (AbstractMediaContainerComponentModel) getCloneComponentContextProvider()
				.findItemForKey(SESSION_CLONE_COMPONENT_CLONE_MODEL);

		// find the current context language locale
		final String languageTag = (String) getCloneComponentContextProvider().findItemForKey(SESSION_CLONE_COMPONENT_LOCALE);
		final MediaContainerModel mediaContainerModel = cloneComponentModel.getMedia(Locale.forLanguageTag(languageTag));

		final Map<String, MediaModel> mediaModelMap = buildMediaModelMapForAllMediaFormats(mediaFormatModelMap,
				mediaContainerModel);

		Sets.newHashSet(mediaModelMap.keySet()).forEach(mediaFormat -> {
			final String mediaCode = source.get(mediaFormat);
			final MediaModel cloneMediaModel = mediaModelMap.get(mediaFormat);

			/*
			 * compare the mediaCode in the "source" Map and in the "source attribute" Map to determine if it was modified
			 * for a given MediaFormat when the mediaCode is different, add the updated value to the map
			 */
			final Optional<Entry<String, Object>> mediaCodeExistsInBothMaps = findMediaCodeExistsInSourceAndAttributeMap(
					srcAttributeMap, languageTag, mediaFormat, mediaCode);

			if (!mediaCodeExistsInBothMaps.isPresent())
			{
				if (cloneMediaModel != null)
				{
					/*
					 * detach cloned Media model created initially. It is no longer needed because it will be replaced by the
					 * newMediaModel or set to null
					 */
					getModelService().detach(cloneMediaModel);
					mediaModelMap.remove(mediaFormat);
				}

				if (mediaCode != null)
				{
					final MediaModel newMediaModel = getUniqueItemIdentifierService().getItemModel(mediaCode, MediaModel.class)
							.orElseThrow(() -> new ConversionException(
									format("could not find a media for code %s in current catalog version", mediaCode)));

					final MediaFormatModel mediaFormatModel = mediaFormatModelMap.get(mediaFormat);
					newMediaModel.setMediaFormat(mediaFormatModel);
					mediaModelMap.put(mediaFormat, newMediaModel);
				}
			}
		});

		mediaContainerModel.setMedias(mediaModelMap.values().stream().filter(Objects::nonNull).collect(Collectors.toList()));
		return mediaContainerModel;
	}

	/**
	 * Builds a {@link Map} of all media format qualifiers defined in the platform and all {@link MediaModel} defined in
	 * the cloned component model. If a {@code MediaModel} is not found for a given media format, the value is set to
	 * <tt>NULL</tt>
	 *
	 * {@link Map} of key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaModel}>
	 *
	 * @param mediaFormatModelMap
	 *           map containing key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaFormatModel}>
	 * @param mediaContainerModel
	 *           the media container model defined on the cloned component model
	 * @return a map of of key-value pairs of <{@link MediaFormatModel#getQualifier()} and {@link MediaModel}>. If a
	 *         {@code MediaModel} is not found for a given media format, the entry value is <tt>NULL</tt>.
	 */
	protected Map<String, MediaModel> buildMediaModelMapForAllMediaFormats(final Map<String, MediaFormatModel> mediaFormatModelMap,
			final MediaContainerModel mediaContainerModel)
	{
		final Map<String, MediaModel> mediaModelMap = new HashMap<>();
		mediaFormatModelMap.keySet().forEach(mediaFormatKey -> mediaModelMap.put(mediaFormatKey, null));
		mediaModelMap.putAll(mediaContainerModel.getMedias().stream()
				.collect(toMap((final MediaModel media) -> media.getMediaFormat().getQualifier(), identity())));
		return mediaModelMap;
	}

	/**
	 * Compare the mediaCode in the "source" Map and in the "source attribute" Map to determine if it was modified for a
	 * given MediaFormat when the mediaCode is different, add the updated value to the map
	 *
	 * @param srcAttributeMap
	 *           the Map representation of the source attribute (saved in the session)
	 * @param languageTag
	 *           the current language locale (saved in the session)
	 * @param mediaFormat
	 *           the media format defined in the source Map
	 * @param mediaCode
	 *           the media code defined in the source Map
	 * @return {@link Optional} containing an {@link Entry} from the {@code srcAttributeMap} which matches the given
	 *         {@code mediaCode} and {@code mediaFormat}; otherwise {@link Optional#empty()}
	 */
	protected Optional<Entry<String, Object>> findMediaCodeExistsInSourceAndAttributeMap(final Map<String, Object> srcAttributeMap,
			final String languageTag, final String mediaFormat, final String mediaCode)
	{
		final Map<String, Object> srcLocalizedAttributeMap = (Map<String, Object>) srcAttributeMap.get(languageTag);
		return srcLocalizedAttributeMap.entrySet().stream() //
				.filter(mapEntry -> mapEntry.getKey().equals(mediaFormat) && mapEntry.getValue().toString().equals(mediaCode))
				.findAny();
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

	protected CMSMediaFormatService getMediaFormatService()
	{
		return mediaFormatService;
	}

	@Required
	public void setMediaFormatService(final CMSMediaFormatService mediaFormatService)
	{
		this.mediaFormatService = mediaFormatService;
	}

	protected MediaContainerFacade getMediaContainerFacade()
	{
		return mediaContainerFacade;
	}

	@Required
	public void setMediaContainerFacade(final MediaContainerFacade mediaContainerFacade)
	{
		this.mediaContainerFacade = mediaContainerFacade;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
