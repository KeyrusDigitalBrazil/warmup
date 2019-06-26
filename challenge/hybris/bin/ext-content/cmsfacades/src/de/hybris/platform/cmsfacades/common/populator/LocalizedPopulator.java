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
package de.hybris.platform.cmsfacades.common.populator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Interface {@link LocalizedPopulator} is used to retrieve the languages supported by the current base store and to
 * populate localized attributes.
 */
public interface LocalizedPopulator
{

	/**
	 * Bi-directional method to populate the localized attributes. 
	 * It can be used to populate Model classes by reading the content from the {@code getter} function, 
	 * and also populate localized {@code Map}s by reading content from a localized data model.
	 * 
	 * This method will iterate over all locales ({@code Locale}) available at the execution time. 
	 * For each locale, it will execute the function {@code getter} to get the localized value that may 
	 * be coming from different sources (see examples bellow). 
	 * The result of the {@code getter} function will then be used on the {@link BiConsumer} {@code setter} function. 
	 * Examples of its usage:
	 * 
	 * <b>Populate a {@code Map<String, String>} from a localized model</b>
	 * {@code 
	 * 	final Map<String, String> map = new HashMap<>();
	 * 	getLocalizedPopulator().populate(
	 * 			(locale, value) -> map.put(getLocalizedPopulator().getLanguage(locale), value),
	 *			(locale) -> source.getSomeLocalizedField(locale)
	 *	);			
	 * }
	 *
	 * * <b>Populate an {@code ItemModel} from a localized {@code Map<String, String>}</b>
	 * {@code
	 *  // source.getTitle() returns Map<String, String>
	 * 	Optional.ofNullable(source.getTitle())
	 * 			.ifPresent(title -> 
	 * 				getLocalizedPopulator()
	 * 						.populate((locale, value) -> target.setTitle(value, locale), 
	 * 						(locale) -> title.get(getLocalizedPopulator().getLanguage(locale))));
	 * }
	 *
	 * @param consumer A {@link BiConsumer} function that will accept the Locale and the Value extracted from the {@code getter} function.
	 * @param getter A {@link Function} that will be applied to get the localized value for a given {@code Locale}.
	 */
	<T> void populate(final BiConsumer<Locale, T> consumer, final Function<Locale, T> getter);

	/**
	 * Default implementation for populating a {@code Map<String, T} applying the function {@code getter} on each locale.
	 * @param getter A {@link Function} that will be applied to each {@code Locale}.
	 * @param <T> the type of the object that is put as a value in the returned map.
	 * @return map with the localized values.
	 */
	default <T> Map<String, T> populateAsMapOfLanguages(final Function<Locale, T> getter)
	{
		final Map<String, T> map = new HashMap<>();
		populate(
				(locale, o) -> map.put(getLanguage(locale), o),
				getter);
		return map;
	}

	/**
	 * Default implementation for populating a {@code Map<Locale, T} applying the function {@code getter} on each language ISO
	 * code.
	 * @param getter A {@link Function} that will be applied to each language ISO code
	 * @param <T> the type of the object that is put as a value in the returned map.
	 * @return map with the localized values.
	 */
	default <T> Map<Locale, T> populateAsMapOfLocales(final Function<String, T> getter)
	{
		final Map<Locale, T> map = new HashMap<>();
		populate(
				(locale, o) -> map.put(locale, o),
				o -> getter.apply(getLanguage(o)));
		return map;
	}
	
	/**
	 * Returns the language for that specific locale. 
	 * The default implementation returns the result of {@link Locale#toString()}.
	 * More specialized implementations should opt to return the format in which the data 
	 * can be correctly translated in other domains.  
	 * @param locale the locale we want to get the language from. 
	 * @return the language for that locale
	 */
	default String getLanguage(final Locale locale)
	{
		if (Objects.isNull(locale))
		{
			throw new IllegalArgumentException("Locale cannot be null");
		}
		return locale.toString();
	}

}
