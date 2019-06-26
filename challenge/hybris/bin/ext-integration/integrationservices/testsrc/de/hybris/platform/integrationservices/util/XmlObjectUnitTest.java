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

package de.hybris.platform.integrationservices.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

@UnitTest
public class XmlObjectUnitTest
{
	@Test
	public void testCreateFromValidInputStream() throws IOException
	{
		final String xml = "<content />";
		try (final InputStream in = new ByteArrayInputStream(xml.getBytes()))
		{
			final XmlObject object = XmlObject.createFrom(in);
			assertThat(object).isNotNull();
		}
	}

	@Test
	public void testCreateFromClosedInputStream() throws IOException
	{
		final String xml = "<content />";
		final InputStream in = new ByteArrayInputStream(xml.getBytes());
		in.close();

		final XmlObject object = XmlObject.createFrom(in);

		assertThat(object).isNotNull();
	}

	@Test
	public void testCreateFromEmptyInputStream() throws IOException
	{
		try (final InputStream in = new ByteArrayInputStream("".getBytes()))
		{
			assertThatThrownBy(() -> XmlObject.createFrom(in)).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Test
	public void testCreateFromInputStreamContainingMalformedXml() throws IOException
	{
		try (final InputStream in = new ByteArrayInputStream("not XML".getBytes()))
		{
			assertThatThrownBy(() -> XmlObject.createFrom(in)).isInstanceOf(IllegalArgumentException.class);
		}
	}

	@Test
	public void testCreateFromValidXml()
	{
		final XmlObject xml = XmlObject.createFrom("<valid />");
		assertThat(xml).isNotNull();
	}

	@Test
	public void testCreateFromMalformedXml()
	{
		assertThatThrownBy(() -> XmlObject.createFrom("<invalid>")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testGetExistingPath()
	{
		final XmlObject xml = XmlObject.createFrom("<object string='some value' />");
		assertThat(xml.get("/object/@string")).isEqualTo("some value");
	}

	@Test
	public void testGetExistingPathContainingNumber()
	{
		final XmlObject xml = XmlObject.createFrom("<number>10</number>");
		assertThat(xml.get("//number")).isEqualTo("10");
	}

	@Test
	public void testGetNonExistentPath()
	{
		final XmlObject xml = XmlObject.createFrom("<empty />");
		assertThat(xml.get("/value")).isEmpty();
	}

	@Test
	public void testGetPathWithMultipleMatches()
	{
		final XmlObject xml = XmlObject.createFrom("<object><value>1</value><value>2</value></object>");
		assertThat(xml.get("//value")).isEqualTo("1");
	}

	@Test
	public void testGetInvalidPath()
	{
		final XmlObject xml = XmlObject.createFrom("<object />");
		assertThatThrownBy(() -> xml.get(">>")).isInstanceOf(IllegalArgumentException.class);
	}
}