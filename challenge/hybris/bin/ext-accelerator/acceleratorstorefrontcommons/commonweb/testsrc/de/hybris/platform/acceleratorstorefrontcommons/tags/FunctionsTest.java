package de.hybris.platform.acceleratorstorefrontcommons.tags;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class FunctionsTest
{

	@Test
	public void shouldEncodeHtml()
	{
		assertEquals("&lt;div&gt;", Functions.encodeHTML("<div>"));
	}

	@Test
	public void shouldEncodeQuotesInHtml()
	{
		assertEquals("&lt;div&#x20;attr&#x3d;&quot;value&quot;&gt;", Functions.encodeHTML("<div attr=\"value\">"));
		assertEquals("&lt;div&#x20;attr&#x3d;&#x27;value&#x27;&gt;", Functions.encodeHTML("<div attr='value'>"));
	}

	@Test
	public void shouldEncodeHtmlWithTabsAndLineFeeds()
	{
		assertEquals("&lt;div&#xa;&#x9;&gt;", Functions.encodeHTML("<div\n\t>"));
	}

	@Test
	public void shouldEncodeUrl()
	{
		assertEquals("http%3a%2f%2fwww.google.com", Functions.encodeUrl("http://www.google.com"));
		assertEquals("http%3a%2f%2fwww.google.com%2fq%3dencoding", Functions.encodeUrl("http://www.google.com/q=encoding"));
		assertEquals("http%3a%2f%2fwww.google.com%2fq%3dencoding%26t%3dutf-8", Functions.encodeUrl("http://www.google.com/q=encoding&t=utf-8"));
	}

	@Test
	public void shouldSanitizeHtmlTagNames() {
		assertEquals("div", Functions.sanitizeHtmlTagName(null));
		assertEquals("div", Functions.sanitizeHtmlTagName(""));
		assertEquals("tag", Functions.sanitizeHtmlTagName("tag"));
		assertEquals("div", Functions.sanitizeHtmlTagName("&abc"));
		assertEquals("this_isaverylong-tag", Functions.sanitizeHtmlTagName("this_isaverylong-tag"));
		assertEquals("3a", Functions.sanitizeHtmlTagName("3a"));
		assertEquals("div", Functions.sanitizeHtmlTagName("<div>"));
		assertEquals("form:form", Functions.sanitizeHtmlTagName("form:form"));
		assertEquals("div", Functions.sanitizeHtmlTagName("svg onload"));
	}
}
