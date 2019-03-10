package br.com.keyrus.warmup.fulfilmentprocess;

import de.hybris.platform.core.model.user.CustomerModel;

/**
 * Used by CheckBlacklistedUserAction, this service is designed to validate the user email before processing the order.
 */
public interface CheckFraudService
{
    /**
     * Performs email user check.
     *
     * @param customer
     *           the customer
     * @return whether the customer is fraudulent or not
     */
    boolean checkBlacklistedUser(CustomerModel customer);
}