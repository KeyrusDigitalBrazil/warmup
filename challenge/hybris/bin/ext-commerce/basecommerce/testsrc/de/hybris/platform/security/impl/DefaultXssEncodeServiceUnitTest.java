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
package de.hybris.platform.security.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.UnsupportedEncodingException;

import org.junit.Test;


@UnitTest
public class DefaultXssEncodeServiceUnitTest
{
	private final DefaultXssEncodeService xssEncodeService = new DefaultXssEncodeService();

	@Test(expected = IllegalArgumentException.class)
	public void testEncodeNull() throws UnsupportedEncodingException
	{
		xssEncodeService.encodeHtml(null);
	}

	protected void assertSafe(final String encodedValue)
	{
		assertFalse(encodedValue.contains("'"));
		assertFalse(encodedValue.contains("\""));
		assertFalse(encodedValue.contains("<"));
		assertFalse(encodedValue.contains(">"));
	}

	@Test
	public void testEncodeHTML() throws UnsupportedEncodingException
	{
		// This context is valid if you insert untrusted data between HTML or XML tags or attributes.
		// Rules and Recommendations
		// The data needs to be HTML encoded.
		// This context combines the OWASP RULE #1 and RULE #2 as described
		// at http://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.231_-_HTML_Escape_Before_Inserting_Untrusted_Data_into_HTML_Element_Content.
		// This context should not be used for special tags or attributes which are opening different contexts like
		// <script>, <style>, href, src, style or any of the event handlers like onmouseover.
		// in all examples '%s' placeholder is used for user provided input

		// example 1 - untrusted data inside HTML tag
		// attacker closes tag and injects malicious code
		// <p>__inject_here__</p>
		// becomes
		// <p></p><script>malicious code</script><p></p>
		assertSafe(xssEncodeService.encodeHtml("</p><script>malicious code</script><p>"));

		// example 2 - untrusted data inside HTML attribute
		// attacker closes attribute and tag and injects malicious code
		// <div title="__inject_here__">div content</div>
		// becomes
		// <div title=""></div><script>malicious code</script><div title="">div content</div>
		assertSafe(xssEncodeService.encodeHtml("\"></div><script>malicious code</script><div title=\""));
	}

	@Test
	public void testEncodeJavaScript() throws UnsupportedEncodingException
	{
		// This context is valid when inserting data within JavaScript string literals
		// Rules and Recommendations
		// This is the most common case where escaping inside JavaScript content is needed
		// as defined with OWASP rule #3 (see http://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.233_-_JavaScript_Escape_Before_Inserting_Untrusted_Data_into_HTML_JavaScript_Data_Values).
		// in all examples '%s' placeholder is used for user provided input

		// example 3 - untrusted data inside JavaScript content
		// attacker closes attribute and method and injects malicious code
		// alert("__inject_here__");
		// becomes
		// alert("");malicious code;alert("");
		assertSafe(xssEncodeService.encodeHtml("\");malicious code;alert(\""));
	}

	@Test
	public void testEncodeURL() throws UnsupportedEncodingException
	{
		// This context is valid for parameter names and values contained in the query string of a URL
		// Rules and Recommendations
		// It is defined with regards to OWASP RULE #5
		// (see https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.235_-_URL_Escape_Before_Inserting_Untrusted_Data_into_HTML_URL_Parameter_Values).
		// There is a special case where the query is a single data value instead of using named parameters that is also covered by this context.
		// in all examples '%s' placeholder is used for user provided input

		// example 4 - untruseted data in URL parameter names and values
		// attacker injects malicious code inside parameter's value
		// q=__inject_here__
		// becomes
		// q=%27]%29;;alert%28%27HACKED%27%29//&page=0
		assertSafe(xssEncodeService.encodeHtml("%27]%29;;alert%28%27HACKED%27%29//&page=0"));
	}
}
