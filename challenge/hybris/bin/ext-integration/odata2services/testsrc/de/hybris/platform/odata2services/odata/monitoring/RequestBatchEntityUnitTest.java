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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

@UnitTest
public class RequestBatchEntityUnitTest
{
	@Test
	public void testConstructorParametersCanBeAccessed() throws IOException
	{
		final RequestBatchEntity entity = new RequestBatchEntity("Message", IOUtils.toInputStream("Body"), "Type", 2);

		assertThat(entity.getMessageId()).isEqualTo("Message");
		assertThat(IOUtils.contentEquals(entity.getContent(), IOUtils.toInputStream("Body"))).isTrue();
		assertThat(entity.getIntegrationObjectType()).isEqualTo("Type");
		assertThat(entity.getNumberOfChangeSets()).isEqualTo(2);
	}

	@Test
	public void testNullConstructorParameters()
	{
		final RequestBatchEntity entity = new RequestBatchEntity(null, null, null, 1);

		assertThat(entity.getMessageId()).isEqualTo("");
		assertThat(entity.getContent()).isNull();
		assertThat(entity.getIntegrationObjectType()).isEqualTo("");
	}

	@Test
	public void testConstructorWithNegativeNumberOfChangeSets()
	{
		assertThatThrownBy(() -> new RequestBatchEntity("M", IOUtils.toInputStream("C"), "T", -1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("change set");
	}

	@Test
	public void testEquals()
	{
		final RequestBatchEntity entity = new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 1);

		assertThat(entity)
				.describedAs("self").isEqualTo(entity)
				.describedAs("copy").isEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 1))
				.describedAs("null").isNotEqualTo(null)
				.describedAs("different message").isNotEqualTo(new RequestBatchEntity("123", IOUtils.toInputStream("Body"), "Type", 1))
				.describedAs("different content").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Content"), "Type", 1))
				.describedAs("different type").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Object", 1))
				.describedAs("different change sets count").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 2));
	}

	@Test
	public void testHashCode()
	{
		final RequestBatchEntity entity = new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 2);

		assertThat(entity.hashCode())
				.describedAs("equal").isEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 2).hashCode())
				.describedAs("different message").isNotEqualTo(new RequestBatchEntity("123", IOUtils.toInputStream("Body"), "Type", 2).hashCode())
				.describedAs("different content").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Content"), "Type", 2).hashCode())
				.describedAs("different type").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Object", 2).hashCode())
				.describedAs("different change sets count").isNotEqualTo(new RequestBatchEntity("Msg", IOUtils.toInputStream("Body"), "Type", 1).hashCode());
	}
}