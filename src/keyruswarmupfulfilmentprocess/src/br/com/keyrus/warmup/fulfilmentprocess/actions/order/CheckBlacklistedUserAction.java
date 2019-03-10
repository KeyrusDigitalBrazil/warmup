package br.com.keyrus.warmup.fulfilmentprocess.actions.order;

import br.com.keyrus.warmup.fulfilmentprocess.CheckFraudService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This example action checks if the customer is fraudulent. Skipping this action may result in
 * failure in one of the subsequent steps of the process.
 */
public class CheckBlacklistedUserAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
    private static final Logger LOG = Logger.getLogger(CheckBlacklistedUserAction.class);

    private CheckFraudService checkFraudService;


    @Override
    public Transition executeAction(final OrderProcessModel process)
    {
        final OrderModel order = process.getOrder();

        if (order == null)
        {
            LOG.error("Missing the order, exiting the process");
            return Transition.NOK;
        }

        CustomerModel customer = (CustomerModel) order.getUser();

        if (!getCheckFraudService().checkBlacklistedUser(customer))
        {
            setOrderStatus(order, OrderStatus.FRAUD_CHECKED);
            return Transition.OK;
        }
        else
        {
            setOrderStatus(order, OrderStatus.WAIT_FRAUD_MANUAL_CHECK);
            order.setFraudulent(Boolean.TRUE);
            return Transition.NOK;
        }
    }


    public CheckFraudService getCheckFraudService() {
        return checkFraudService;
    }

    @Required
    public void setCheckFraudService(CheckFraudService checkFraudService) {
        this.checkFraudService = checkFraudService;
    }

}