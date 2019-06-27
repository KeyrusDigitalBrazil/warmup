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
module.exports = (function() {

    var componentObject = {};

    componentObject.constants = {};

    componentObject.elements = {
        getOpenedEditorModals: function() {
            return element.all(by.xpath('//div[@uib-modal-window="modal-window"][.//generic-editor]'));
        },
        getTopEditorModal: function() {
            return this.getOpenedEditorModals().get(0);

        },
        getFieldByQualifier: function(fieldQualifier) {

            var modalElement = this.getTopEditorModal();
            browser.waitForPresence(modalElement);

            var field = modalElement.element(by.css(".ySEGenericEditorFieldStructure[data-cms-field-qualifier='" + fieldQualifier + "']"));
            browser.waitForPresence(field);

            return field;
        }
    };

    componentObject.actions = {};

    componentObject.assertions = {};

    componentObject.utils = {};

    return componentObject;

}());
