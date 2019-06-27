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
package de.hybris.platform.acceleratorstorefrontcommons.tags;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * JUnit tests for {@link de.hybris.platform.acceleratorstorefrontcommons.tags.HTMLSanitizer}
 */
@UnitTest
public class HTMLSanitizerTest extends HybrisJUnit4TransactionalTest
{
	private static final Logger LOG = Logger.getLogger(HTMLSanitizerTest.class);

	@Test
	public void testAllowedElements()
	{
		final String testHtml = "<hr /><address>Nymphenburger Str. 86</address><pre>München</pre><em class=\"country\">DE</em><a "
				+ "href=\"https://www.sap.com/canada/index.html\" class=\"link\" rel=\"nofollow\">SAP</a>";
		assertEquals(testHtml, HTMLSanitizer.sanitizeHTML(testHtml));
	}

	@Test
	public void testForbiddenHtmlElements()
	{
		final String form = "<form action=\"xxx\" method=\"post\"><label for=\"POST-name\">Name:</label>"
				+ "<input id=\"POST-name\" type=\"text\" name=\"name\"></form>";
		assertEquals("Name:", HTMLSanitizer.sanitizeHTML(form));

		final String button = "<button>click me</button>";
		assertEquals("click me", HTMLSanitizer.sanitizeHTML(button));

		final String html = "<html lang=\"en\"><head>head</head><body>body</body></html>";
		assertEquals("headbody", HTMLSanitizer.sanitizeHTML(html));

		final String scriptHtml4 = "<script type=\"text/javascript\" src=\"javascript.js\"></script>";
		assertEquals("", HTMLSanitizer.sanitizeHTML(scriptHtml4));

		final String scriptHtml5 = "<script src=\"javascript.js\"></script>";
		assertEquals("", HTMLSanitizer.sanitizeHTML(scriptHtml5));
	}

	@Test
	public void testAttacks() {
		final String img = "<img src=x onerror=alert(1)//>";
		final String svg = "<svg><g/onload=alert(2)//<p>";
		final String pIframe = "<p>abc<iframe/\\/src=jAva&Tab;script:alert(3)>def";
		final String math = "<math><mi//xlink:href=\"data:x,<script>alert(4)</script>\">";
		final String table = "<TABLE><tr><td>HELLO</tr></TABLE>";

		assertEquals("", HTMLSanitizer.sanitizeHTML(img));
		assertEquals("", HTMLSanitizer.sanitizeHTML(svg));
		assertEquals("<p>abc</p>", HTMLSanitizer.sanitizeHTML(pIframe));
		assertEquals("", HTMLSanitizer.sanitizeHTML(math));
		assertEquals("<table><tbody><tr><td>HELLO</td></tr></tbody></table>", HTMLSanitizer.sanitizeHTML(table));
	}

	@Test
	public void testSchemasAttack()
	{
		final String javascript = "<a href='javascript:alert('XSS')'>ClickMe</a>";
		assertEquals("ClickMe", HTMLSanitizer.sanitizeHTML(javascript));

		final String ftp = "<a href=\"ftp://someftpserver.com/\">Browse the FTP server</a>";
		assertEquals("Browse the FTP server", HTMLSanitizer.sanitizeHTML(ftp));
	}

	@Test
	public void testStyles()
	{
		final String testHtml = "<div style=\"background:red;position:absolute;display:block;top:0;\">foo</div>";
		final String sanitizedHtml = "<div style=\"background:red\">foo</div>";
		assertEquals(sanitizedHtml, HTMLSanitizer.sanitizeHTML(testHtml));
	}

	@Test
	public void testForbiddenCssElements() {
		final String positionSticky = "<div style=\"position:sticky\">XSS</div>";
		final String positionFixed = "<div style=\"position:fixed\">XSS</div>";

		assertEquals("<div>XSS</div>", HTMLSanitizer.sanitizeHTML(positionSticky));
		assertEquals("<div>XSS</div>", HTMLSanitizer.sanitizeHTML(positionFixed));
	}
}
