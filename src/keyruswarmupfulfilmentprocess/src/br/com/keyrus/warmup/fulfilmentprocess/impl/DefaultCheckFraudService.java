package br.com.keyrus.warmup.fulfilmentprocess.impl;

import br.com.keyrus.warmup.core.model.BlackListUsersModel;
import br.com.keyrus.warmup.fulfilmentprocess.CheckFraudService;
import br.com.keyrus.warmup.fulfilmentprocess.CheckOrderService;
import br.com.keyrus.warmup.fulfilmentprocess.dao.FraudCheckDao;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.List;


/**
 * Default implementation of {@link CheckOrderService}
 */
public class DefaultCheckFraudService implements CheckFraudService
{

    private FraudCheckDao fraudCheckDao;

    @Override
    public boolean checkBlacklistedUser(CustomerModel customer) {
        List<BlackListUsersModel> blockedCustomerByEmail = getFraudCheckDao().findBlockedCustomerByEmail(customer.getUid());
        return blockedCustomerByEmail.size()>0;
    }


    public FraudCheckDao getFraudCheckDao() {
        return fraudCheckDao;
    }

    public void setFraudCheckDao(FraudCheckDao fraudCheckDao) {
        this.fraudCheckDao = fraudCheckDao;
    }

}