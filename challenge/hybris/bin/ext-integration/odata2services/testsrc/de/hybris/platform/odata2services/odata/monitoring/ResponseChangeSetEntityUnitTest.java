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
 */

package de.hybris.platform.odata2services.odata.monitoring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.inboundservices.model.InboundRequestErrorModel;
import de.hybris.platform.integrationservices.util.HttpStatus;

import org.junit.Test;

@UnitTest
public class ResponseChangeSetEntityUnitTest
{
	private static final InboundRequestErrorModel ERROR = error();

	private static InboundRequestErrorModel error()
	{
		return mock(InboundRequestErrorModel.class);
	}

	@Test
	public void testConstructorValuesCanBeReadBack()
	{
		final ResponseChangeSetEntity entity = new ResponseChangeSetEntity("key", HttpStatus.OK, ERROR);

		assertThat(entity.getIntegrationKey()).isEqualTo("key");
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getRequestError()).contains(ERROR);
	}

	@Test
	public void testConstructorHandlesNulls()
	{
		final ResponseChangeSetEntity entity = new ResponseChangeSetEntity(null, HttpStatus.OK, null);

		assertThat(entity.getIntegrationKey()).isEqualTo("");
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(entity.getRequestError()).isEmpty();
	}

	@Test
	public void testIsSuccessful()
	{
		assertThat(new ResponseChangeSetEntity("", HttpStatus.CONTINUE, null).isSuccessful()).isFalse();
		assertThat(new ResponseChangeSetEntity("", HttpStatus.OK, null).isSuccessful()).isTrue();
		assertThat(new ResponseChangeSetEntity("", HttpStatus.MULTIPLE_CHOICES, null).isSuccessful()).isFalse();
		assertThat(new ResponseChangeSetEntity("", HttpStatus.BAD_REQUEST, null).isSuccessful()).isFalse();
		assertThat(new ResponseChangeSetEntity("", HttpStatus.INTERNAL_SERVER_ERROR, null).isSuccessful()).isFalse();
	}

	@Test
	public void testEquals()
	{
		final ResponseChangeSetEntity entity = new ResponseChangeSetEntity("key", HttpStatus.OK, ERROR);

		assertThat(entity)
				.describedAs("self").isEqualTo(entity)
				.describedAs("copy").isEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, ERROR))
				.describedAs("null").isNotEqualTo(null)
				.describedAs("different key").isNotEqualTo(new ResponseChangeSetEntity("id", HttpStatus.OK, ERROR))
				.describedAs("different status").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.CREATED, ERROR))
				.describedAs("different error").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, error()))
				.describedAs("null error").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, null));
	}

	@Test
	public void testHashCode()
	{
		final ResponseChangeSetEntity entity = new ResponseChangeSetEntity("key", HttpStatus.OK, ERROR);

		assertThat(entity.hashCode())
				.describedAs("copy").isEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, ERROR).hashCode())
				.describedAs("different key").isNotEqualTo(new ResponseChangeSetEntity("id", HttpStatus.OK, ERROR).hashCode())
				.describedAs("different status").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.CREATED, ERROR).hashCode())
				.describedAs("different error").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, error()).hashCode())
				.describedAs("null error").isNotEqualTo(new ResponseChangeSetEntity("key", HttpStatus.OK, null).hashCode());
	}
}
