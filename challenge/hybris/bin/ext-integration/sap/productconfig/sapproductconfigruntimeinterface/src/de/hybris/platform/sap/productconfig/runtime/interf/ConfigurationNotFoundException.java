package de.hybris.platform.sap.productconfig.runtime.interf;

/**
 * Checked exception for errors in the configuration runtime, special case when a configuration does not exist anymore.
 * This can happen if it was deleted diretly in CPS.
 */
public class ConfigurationNotFoundException extends ConfigurationEngineException
{

	/**
	 * default constructor
	 */
	public ConfigurationNotFoundException()
	{
		super();
	}

	/**
	 * Constructor with message and cause
	 *
	 * @param message
	 *           message of the exception
	 * @param cause
	 *           cause of the exception
	 */
	public ConfigurationNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

}
