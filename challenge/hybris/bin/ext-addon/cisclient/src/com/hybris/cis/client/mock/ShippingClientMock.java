/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package com.hybris.cis.client.mock;

import com.hybris.cis.client.shared.models.Pair;
import com.hybris.cis.client.shipping.ShippingClient;
import com.hybris.cis.client.shipping.models.CisLabel;
import com.hybris.cis.client.shipping.models.CisShipment;
import com.hybris.cis.client.shipping.models.TrackingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;


/**
 * Mock implementation of {@link ShippingClient}
 */
public class ShippingClientMock extends SharedClientMock implements ShippingClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ShippingClientMock.class);

	private static final int LABEL_STREAM_BUFFER_SIZE = 100;

	public ShippingClientMock()
	{
		LOGGER.info("Using MOCK Client to simulate Shipping.");
	}

	/**
	 * Creates a test shipment.
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param cisShipment
	 *           a shipment for testing purposes
	 *
	 * @return a shipment with id 123 and tracking number 456, vendor name 123 and an empty list for labels
	 */
	@Override
	public CisShipment createShipment(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
									  @HeaderParam(value = "X-tenantId") final String tenantId, final CisShipment cisShipment)
	{
		LOGGER.info("Using MOCK Client - createShipment()");

		cisShipment.setId("123");
		final TrackingInfo trackingInfo = new TrackingInfo();
		trackingInfo.setTrackingNumber("456");
		trackingInfo.setVendorName("123");
		cisShipment.setTrackingInfos(Collections.singletonList(trackingInfo));

		final CisLabel cisLabel = new CisLabel();
		cisLabel.setId("456");
		cisLabel.setHref("http://localhost:8080/shipment/cisShippingMock/12345");
		cisShipment.setLabels(Arrays.asList(cisLabel));
		return cisShipment;
	}

	/**
	 * Returns a test stream for gathering the label.
	 *
	 * @param xClientRef
	 *           client ref to pass in the header
	 * @param tenantId
	 *           tenantId to pass in the header
	 * @param shipmentId
	 *           the shipment id
	 * @param labelId
	 *           the label id
	 *
	 * @return a ByteArray with a text
	 */
	@Override
	public byte[] getLabel(@HeaderParam(value = "X-CIS-Client-ref") final String xClientRef,
			@HeaderParam(value = "X-tenantId") final String tenantId, final String shipmentId, final String labelId)
	{
		LOGGER.info("Using MOCK Client - getLabel()");

		final InputStream inputStream = this.getClass().getResourceAsStream("/shipping/mockLabel.pdf");
		final Pair<InputStream, String> label = new Pair<>(inputStream, "application/pdf");

		// build streaming output
		final StreamingOutput result = new StreamingOutput()
		{
			private final byte[] buffer = new byte[LABEL_STREAM_BUFFER_SIZE];

			@Override
			public void write(final OutputStream output) throws IOException, WebApplicationException
			{
				int numRead;
				while ((numRead = label.getKey().read(this.buffer)) >= 0)
				{
					output.write(this.buffer, 0, numRead);
				}
			}
		};

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			result.write(out);
			return out.toByteArray();
		}
		catch (final IOException e) //NOSONAR
		{
			LOGGER.error("Could not write label to a byte stream"); //NOSONAR
		}

		return null;
	}
}
