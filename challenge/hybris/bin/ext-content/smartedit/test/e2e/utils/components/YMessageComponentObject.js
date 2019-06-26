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

    var yMessageObject = {
        elements: {
            getYMessageById: function(id) {
                return element(by.css('div[message-id=' + id + ']'));
            },
            getTitle: function(id) {
                return this.getSubElementByClass(id, '.y-message-info-title').getText();
            },
            getDescription: function(id) {
                return this.getSubElementByClass(id, '.y-message-info-description').getText();
            },
            getSubElementByClass: function(id, className) {
                var el = this.getYMessageById(id);
                return el.element(by.css(className));
            },
            getWrapperClasses: function(id) {
                var el = this.getYMessageById(id);
                return el.getAttribute('class');
            }
        },
        actions: {
            openAndBeReady: function() {
                browser.get('test/e2e/yMessage/yMessageTest.html');
            }
        },
        assertions: {
            assertTitleContainsText: function(id, text) {
                expect(yMessageObject.elements.getTitle(id)).toBe(text, "Expected the title to be present");
            },
            assertDescriptionContainsText: function(id, text) {
                expect(yMessageObject.elements.getDescription(id)).toBe(text, "Expected the description to be present");
            },
            assertInfoTypeWasApplied: function(id) {
                expect(yMessageObject.utils.isIconClassExists(id)).toBe(true,
                    "Expected the yMessage to have y-message-info class");

                expect(yMessageObject.elements.getWrapperClasses(id)).toContain('y-message-info',
                    "Expected the yMessage to have hyicon-msginfo icon class");
            },
            assertComplexDescriptionWasTranscluded: function(id) {
                expect(yMessageObject.elements.getSubElementByClass(id, '.inner-class').isPresent()).toBe(true,
                    "Expected the yMessage to have a transcluded element class");
            },
            assertDefaultIdWasProvided: function() {
                expect(yMessageObject.elements.getYMessageById(yMessageObject.constants.YMESSAGE_DEFAULT_ID).isPresent()).toBe(true,
                    "Expected the yMessage to use a default id");
            }
        },
        constants: {
            YMESSAGE_WITH_TYPE_ID: "y-message-id",
            YMESSAGE_WITHOUT_TYPE_ID: "y-message-id",
            YMESSAGE_WITH_COMPLEX_DESCRIPTION: "y-message-with-complex-description-id",
            YMESSAGE_DEFAULT_ID: "y-message-default-id"
        },
        utils: {
            isIconClassExists: function(id) {
                return yMessageObject.elements.getSubElementByClass(id, '.hyicon-msginfo').isPresent();
            }
        }
    };

    return yMessageObject;
})();
