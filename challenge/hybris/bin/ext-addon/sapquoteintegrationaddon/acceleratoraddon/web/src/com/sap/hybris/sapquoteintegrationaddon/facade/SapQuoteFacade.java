/**
 *
 */
package com.sap.hybris.sapquoteintegrationaddon.facade;

/**
 * @author setup
 *
 */
public interface SapQuoteFacade
{

	/**
	 * @param quoteCode
	 */
	public byte[] downloadQuoteProposalDocument(String quoteCode);

}
