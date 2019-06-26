/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
angular.module('displayConditionsPrimaryPageControllerModule', [])
    .controller('displayConditionsPrimaryPageController', function() {
        this.associatedPrimaryPageLabelI18nKey = 'se.cms.display.conditions.primary.page.label';

        this.triggerOnPrimaryPageSelect = function() {
            this.onPrimaryPageSelect({
                primaryPage: this.associatedPrimaryPage
            });
        };
    });
