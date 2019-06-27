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
package com.hybris.ymkt.clickstream.listeners;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class ODataServer
{
	protected class ImportHeaderHandler implements HttpHandler
	{
		private final byte[] payload;

		public ImportHeaderHandler(final byte[] payload)
		{
			this.payload = payload;
		}

		@Override
		public void handle(final HttpExchange exchange) throws IOException
		{
			LOGGER.info("Method={}, URI={}", exchange.getRequestMethod(), exchange.getRequestURI());
			try (InputStream requestBody = exchange.getRequestBody())
			{
				ODataServer.this.requestBody = ODataServer.convertStreamToString(requestBody);
			}

			exchange.sendResponseHeaders(201, this.payload.length);
			try (OutputStream os = exchange.getResponseBody())
			{
				os.write(this.payload);
				os.flush();
			}
			exchange.close();
		}
	}

	protected static class MyHandler implements HttpHandler
	{
		private final byte[] payload;

		public MyHandler(final byte[] payload)
		{
			this.payload = payload;
		}

		@Override
		public void handle(final HttpExchange exchange) throws IOException
		{
			LOGGER.info("Method={}, URI={}", exchange.getRequestMethod(), exchange.getRequestURI());

			exchange.getResponseHeaders().add("Set-Cookie", "ThisCookieIsNotFood");
			exchange.getResponseHeaders().add("X-CSRF-Token", "1X-CSRF-Fake-Token1234==");
			exchange.sendResponseHeaders(200, this.payload.length);
			try (OutputStream os = exchange.getResponseBody())
			{
				os.write(this.payload);
				os.flush();
			}
			exchange.close();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ODataServer.class);

	protected String requestBody;
	protected final HttpServer server;

	public ODataServer() throws IOException
	{
		this(new InetSocketAddress(44300));
	}

	public ODataServer(final InetSocketAddress port) throws IOException
	{
		final byte[] service = readFile("CUAN_IMPORT_SRV.xml").getBytes(StandardCharsets.UTF_8);
		final byte[] metadata = readFile("$metadata.xml").getBytes(StandardCharsets.UTF_8);
		final byte[] importheaders = readFile("CUAN_IMPORT_SRV-ImportHeaders.xml").getBytes(StandardCharsets.UTF_8);

		this.server = HttpServer.create(port, 0);
		this.server.createContext("/sap/opu/odata/sap/CUAN_IMPORT_SRV/", new MyHandler(service));
		this.server.createContext("/sap/opu/odata/sap/CUAN_IMPORT_SRV/$metadata", new MyHandler(metadata));
		this.server.createContext("/sap/opu/odata/sap/CUAN_IMPORT_SRV/ImportHeaders", new ImportHeaderHandler(importheaders));
		this.server.setExecutor(null); // creates a default executor
		this.server.start();
	}

	protected static final String readFile(final String path) throws IOException
	{
		try (final InputStream is = ODataServer.class.getResourceAsStream("/".concat(path)))
		{
			return convertStreamToString(is);
		}
	}

	protected static String convertStreamToString(final InputStream is)
	{
		try (final Scanner scanner = new Scanner(is))
		{
			try (final Scanner s = scanner.useDelimiter("\\A"))
			{
				return s.hasNext() ? s.next() : "";
			}
		}
	}

	public String getRequestBody()
	{
		return this.requestBody;
	}

	public void stop()
	{
		System.out.println("Stopping Web server on port " + this.server.getAddress().getPort());
		this.server.stop(0);
	}
}
