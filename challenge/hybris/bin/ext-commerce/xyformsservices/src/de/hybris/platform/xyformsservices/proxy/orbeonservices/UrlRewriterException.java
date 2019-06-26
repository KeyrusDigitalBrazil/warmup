package de.hybris.platform.xyformsservices.proxy.orbeonservices;

/**
 * Exception thrown when dealing with UrlRewriter
 */
public class UrlRewriterException extends Exception
{
	/**
	 * Message based exception.
	 *
	 * @param message
	 */
	public UrlRewriterException(final String message)
	{
		super(message);
	}

	/**
	 * Message and throwable based Exception
	 *
	 * @param message
	 * @param t
	 */
	public UrlRewriterException(final String message, final Throwable t)
	{
		super(message, t);
	}

	/**
	 * Throwable based Exception
	 *
	 * @param t
	 */
	public UrlRewriterException(final Throwable t)
	{
		super(t);
	}
}
