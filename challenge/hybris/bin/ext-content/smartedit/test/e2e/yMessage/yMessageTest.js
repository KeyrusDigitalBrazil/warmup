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
describe('yMessage', function() {

    var yMessage = require("../utils/components/YMessageComponentObject.js");

    beforeEach(function() {
        yMessage.actions.openAndBeReady();
    });

    it('GIVEN a page with a yMessage ' +
        'THEN the yMessage is displayed with a title and description',
        function() {
            //THEN
            yMessage.assertions.assertTitleContainsText(yMessage.constants.YMESSAGE_WITH_TYPE_ID, "TEST TITLE");
            yMessage.assertions.assertDescriptionContainsText(yMessage.constants.YMESSAGE_WITH_TYPE_ID, "TEST DESCRIPTION");
        });

    it('GIVEN a page with a yMessage ' +
        'AND provide an info type ' +
        'THEN the yMessage should apply y-message-info and hyicon-msginfo classes',
        function() {
            //THEN
            yMessage.assertions.assertInfoTypeWasApplied(yMessage.constants.YMESSAGE_WITH_TYPE_ID);
        });

    it('GIVEN a page with a yMessage ' +
        'AND no type provided ' +
        'THEN the yMessage uses the default info type',
        function() {
            //THEN
            yMessage.assertions.assertInfoTypeWasApplied(yMessage.constants.YMESSAGE_WITHOUT_TYPE_ID);
        });

    it('GIVEN a page with a yMessage ' +
        'AND a complex description ' +
        'THEN the yMessage should transclude the description',
        function() {
            //THEN
            yMessage.assertions.assertComplexDescriptionWasTranscluded(yMessage.constants.YMESSAGE_WITH_COMPLEX_DESCRIPTION);
        });

    it('GIVEN a page with a yMessage ' +
        'AND an id was not provided ' +
        'THEN the yMessage should use default id',
        function() {
            yMessage.assertions.assertDefaultIdWasProvided();
        });
});
