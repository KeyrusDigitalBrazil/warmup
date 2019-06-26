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
package de.hybris.platform.cmsfacades.uniqueidentifier;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;

/**
 * This class is used as a composed key when an itemId cannot, alone, be uniquely identified and
 * the other attributes, such as catalog and catalog version, are needed to accomplish the task.
 *
 * This class provides an inner class to help building the object from a string that can
 * either be from a JSON format or an encoded version of the JSONified string.
 *
 * It is guaranteed that {@code Builder.fromEncoded(key.toEncoded())} is always equal to {@code key}.
 *
 * @see BaseEncoding#base64() for more details about the encoding algorithm used.
 * @see ObjectMapper for more details about JSON from/to object conversion.
 */
public final class EncodedItemComposedKey extends ItemComposedKey
{
	// base encoding/decoding class
	public static final BaseEncoding BASE_ENCODING = BaseEncoding.base64();
	// JSON mapper
	public static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	public static class Builder
	{
		private final String content;
		private boolean encoded;

		public Builder(final String content)
		{
			this.content = content;
		}

		/**
		 * Set encoded attribute to true, which will affect how the build() method works.
		 * @return the same builder.
		 */
		public Builder encoded()
		{
			this.encoded = true;
			return this;
		}

		/**
		 * Returns the object represented by the JSON string parameter.
		 * The following JSON string format is expected:
		 * <pre>
		 * {
		 *     "itemId": "item-id",
		 *     "catalogId": "catalog-id",
		 *     "catalogVersion": "catalog-version"
		 * }
		 * </pre>
		 * @param jsonString the JSON string representation of this object
		 * @return the Object represented by the JSON string.
		 */
		protected EncodedItemComposedKey fromJsonString(final String jsonString)
		{
			Preconditions.checkNotNull(jsonString, "The JSON string representation should not be empty or null.");
			try
			{
				return JSON_MAPPER.readValue(jsonString, EncodedItemComposedKey.class);
			}
			catch (final IOException e)
			{
				throw new ConversionException("Error converting the Json string to Unique Identifier composed key.", e);
			}
		}

		/**
		 * Returns the object represented by the Base64 value.
		 * The Base 64 String will be converted into a string, that should match the following JSON string format:
		 * <pre>
		 * {
		 *     "itemId": "item-id",
		 *     "catalogId": "catalog-id",
		 *     "catalogVersion": "catalog-version"
		 * }
		 * </pre>
		 * The object returned will be returned after the following transformation:
		 * <pre>
		 *     object = fromJson(base64Decode(value))
		 * </pre>
		 * @param encodedString the encoded content to be converted
		 * @return the Object represented by the JSON string.
		 * @see BaseEncoding#decode(CharSequence)  for more details about the encoding algorithm.
		 */
		protected EncodedItemComposedKey fromEncodedString(final String encodedString)
		{
			try
			{
				return JSON_MAPPER.readValue(BASE_ENCODING.decode(encodedString), EncodedItemComposedKey.class);
			}
			catch (final IOException e)
			{
				throw new ConversionException("Error converting the Json data to Unique Identifier composed key.", e);
			}
		}

		/**
		 * Builds an {@link EncodedItemComposedKey}.
		 * @return the {@link EncodedItemComposedKey} that was built
		 */
		public EncodedItemComposedKey build()
		{
			Preconditions.checkNotNull(this.content, "Encoded String cannot be null in order to use this builder.");
			if (this.encoded)
			{
				return fromEncodedString(this.content);
			}
			else
			{
				return fromJsonString(this.content);
			}
		}
	}


	/**
	 * Returns a json string representation of the object.
	 * It uses all its attributes to generate a Json string.
	 * For  example, if you define a key with the following attributes:
	 * <pre>
	 *     	EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey();
	 *		itemComposedKey.setItemId("item-id");
	 *		itemComposedKey.setCatalogId("catalog-id");
	 *		itemComposedKey.setCatalogVersion("catalog-version");
	 * </pre>,
	 * this method will return a JSON string with the following format:
	 * <pre>
	 * {
	 *     "itemId": "item-id",
	 *     "catalogId": "catalog-id",
	 *     "catalogVersion": "catalog-version"
	 * }
	 * </pre>

	 * @return a json string representation of the object.
	 */
	public final String toJsonString()
	{
		try
		{
			return JSON_MAPPER.writeValueAsString(this);
		}
		catch (final JsonProcessingException e)
		{
			throw new ConversionException("Error converting Unique Identifier composed key to Json string", e);
		}
	}

	/**
	 * Returns a Base 64 representation of the object.
	 * First it generates a JSON representation, and then it
	 * encodes with a Base64 algorithm.
	 * <pre>
	 *     value = base64Encode(toJson(this))
	 * </pre>
	 *
	 * @return The base64 encoded representation of the JSONified string version of this object.
	 * @see BaseEncoding#encode(byte[])  for more details about the encoding algorithm.
	 */
	public final String toEncoded()
	{
		return BASE_ENCODING.encode(toJsonString().getBytes());
	}

}
