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
package de.hybris.platform.cms2.version.converter.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.version.converter.customattribute.CMSVersionCustomAttribute;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.persistence.audit.payload.PayloadDeserializer;
import de.hybris.platform.persistence.audit.payload.json.AuditPayload;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


/**
 * Analyzer is used to parse a JSON payload and prepare a data structure usable for analysis.
 */
public class CMSVersionPayloadAnalyzer
{
	private final PayloadDeserializer payloadDeserializer;

	private final ModelService modelService;

	public CMSVersionPayloadAnalyzer(final PayloadDeserializer payloadDeserializer, final FlexibleSearchService flexibleSearchService, final ModelService modelService)
	{
		this.payloadDeserializer = payloadDeserializer;
		this.modelService = modelService;
	}

	protected class PayloadValue
	{
		public boolean containsVersionPK;
		public boolean containsPK;
		public String language;
		public String rawValue;
	}

	protected class PayloadAttribute
	{
		public String name;
		public boolean isCollection;
		public String type;
		public List<PayloadValue> values;
	}

	public List<PayloadAttribute> attributes = new ArrayList<>();

	public void analyse(final String payload)
	{
		attributes.clear();
		final AuditPayload auditPayload = payloadDeserializer.deserialize(payload);
		final List<PayloadAttribute> payloadAttributes = auditPayload.getAttributes().entrySet().stream().map(entry -> {
			final PayloadAttribute payloadAttribute = new PayloadAttribute();
			payloadAttribute.name = entry.getKey();
			payloadAttribute.isCollection = !entry.getValue().getType().getCollection().isEmpty();
			payloadAttribute.type = entry.getValue().getType().getType();
			List<PayloadValue> payloadValues = entry.getValue().getValue().stream().map(value -> {
				PayloadValue payloadValue = new PayloadValue();
				if (isCustomAttributeType(payloadAttribute.type))
				{
					String val = value.split(CMSVersionCustomAttribute.DELIMITER)[1];
					payloadValue.containsVersionPK = isVersionPK(val);
					payloadValue.rawValue = val;
				}
				else
				{
					payloadValue.containsVersionPK = isVersionPK(value);
					payloadValue.rawValue = value;
				}
				payloadValue.containsPK = isPK(payloadValue.rawValue);
				return payloadValue;
			}).collect(toList());

			payloadAttribute.values = payloadValues;
			return payloadAttribute;
		}).collect(toList());

		final List<PayloadAttribute> localizedPayloadAttributes = auditPayload.getLocAttributes().entrySet().stream().map(entry -> {
			final PayloadAttribute payloadAttribute = new PayloadAttribute();
			payloadAttribute.name = entry.getKey();
			payloadAttribute.isCollection = !entry.getValue().getType().getCollection().isEmpty();
			payloadAttribute.type = entry.getValue().getType().getType();

			List<PayloadValue> payloadValues = entry.getValue().getValues().stream().flatMap(value -> {
				Stream<PayloadValue> payloadValueStream = value.getValue().stream().map(val -> {
					PayloadValue payloadValue = new PayloadValue();
					payloadValue.language = value.getLanguage();
					payloadValue.containsVersionPK = isVersionPK(val);
					payloadValue.rawValue = val;
					payloadValue.containsPK = isPK(val);
					return payloadValue;
				});
				return payloadValueStream;
			}).collect(toList());

			payloadAttribute.values = payloadValues;

			return payloadAttribute;
		}).collect(toList());

		attributes.addAll(payloadAttributes);
		attributes.addAll(localizedPayloadAttributes);
	}

	public PayloadAttribute getAttributeByName(final String name)
	{
		return attributes.stream().filter(attr -> attr.name.equals(name)).findFirst().get();
	}

	protected boolean isCustomAttributeType(final String payloadType)
	{
		Class typeClass = null;
		try
		{
			typeClass = Class.forName(payloadType);
		}
		catch (final ClassNotFoundException e)
		{
			// ignore
		}

		return typeClass != null && CMSVersionCustomAttribute.class.isAssignableFrom(typeClass);
	}

	protected boolean isVersionPK(final String pkFromVersion)
	{
		try
		{
			final ItemModel itemModel = modelService.get(PK.parse(pkFromVersion));
			return itemModel instanceof CMSVersionModel;
		}
		catch (final Exception e)
		{
			return false;
		}
	}

	protected boolean isPK(final String pkFromVersion)
	{
		try
		{
			final PK pk = PK.parse(pkFromVersion);
			return pk != null;
		}
		catch (final PK.PKException e)
		{
			return false;
		}
	}

	protected boolean isPKAttributeType(final String payloadType)
	{
		Class typeClass = null;
		try
		{
			typeClass = Class.forName(payloadType);
		}
		catch (final ClassNotFoundException e)
		{
			// ignore
		}

		return typeClass != null && PK.class.isAssignableFrom(typeClass);
	}
}
