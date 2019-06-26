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
package de.hybris.platform.mobileservices;

import static junit.framework.Assert.assertNotNull;

import de.hybris.platform.mobileservices.facade.Code2DService;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * The Class TestDatamatrixCodes.
 * All 3 test images consists on encoding the google url: "http://www.google.com"
 * Currently, our datamatrix decoding libraries fails to decode non alphanumeric characters
 * The errors produced, varies with the encoding string. The longer the string, the worse results
 */
public class DatamatrixCodesTest extends ServicelayerTest
{

	/** The code2d service. */
	@Resource
	private Code2DService code2dService;

	/** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
	private static final Logger LOG = Logger.getLogger(DatamatrixCodesTest.class.getName());

	/** The google1. */
	private BufferedImage google1 = null;

	/** The google2. */
	private BufferedImage google2 = null;

	/** The google3. */
	private BufferedImage google3 = null;

	/**
	 * Sets the up.
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception
	{

		google1 = ImageIO.read(new ClassPathResource("/test/dmgoogle1.png").getFile());
		google2 = ImageIO.read(new ClassPathResource("/test/dmgoogle2.png").getFile());
		google3 = ImageIO.read(new ClassPathResource("/test/dmgoogle3.png").getFile());


	}



	/**
	 * Test env is ready.
	 * @throws Exception the exception
	 */
	@Test
	public void testEnvisReady() throws Exception
	{
		assertNotNull("images loaded", google1);
		assertNotNull("images loaded", google2);
		assertNotNull("images loaded", google3);
	}


	/**
	 * Encode decode.
	 * @param testString the test string
	 * @param fileName the file name
	 * @throws Exception the exception
	 */
	private void encodeTest(final String testString, final String fileName) throws Exception 
	{
		final BufferedImage test = code2dService.encodeDatamatrixCode(testString, 300, 300);
		if (fileName != null)
		{
			try
			{

				ImageIO.write(test, "png", File.createTempFile("outputdm" + fileName, "png"));
			}
			catch (final Exception x)
			{
				LOG.warn("Unable to write to the filesystem");
			}
		}
		assertNotNull("encode failed", test);

	}

	/**
	 * Encode long url test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeLongTest() throws Exception 
	{
		encodeTest("http://localhost:9001/mobile/barcode/catalog/hwcatalog/version/Online/cat?voucherCode=A04", "longurl");
	}

	/**
	 * Encode short test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeShortTest() throws Exception 
	{
		encodeTest("http://www.google.com", "shorturl");
	}

	/**
	 * Encode short alpha test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeShortAlphaTest() throws Exception 
	{
		encodeTest("Awesome", "shortalpha");
	}

	/**
	 * Encode long alpha test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeLongAlphaTest() throws Exception 
	{
		encodeTest(
				"This is a text test phrased as long as possible to give the test case the chance to test decoding against a very long sentence such as this",
				"longalpha");
	}

	/**
	 * Encode short non alpha test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeShortNonAlphaTest() throws Exception 
	{
		encodeTest("././$$%%&&##", "shortnonalpha");
	}

	/**
	 * Encode long non alpha test.
	 * @throws Exception the exception
	 */
	@Test
	public void encodeLongNonAlphaTest() throws Exception 
	{
		encodeTest(":-) :-( ;-) :-P :-D :-/ :-S }:-) {:) ~|| *+-5?! <> [] '-_- # @ =", "lonngnonalpha");
	}

}
