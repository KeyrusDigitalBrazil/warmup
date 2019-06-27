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
describe('yTextMore', function() {

    var yMoreText = require("../utils/components/YMoreTextComponentObject.js");

    var COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT = 'yMoreTextLimitLessThenText';
    var COMPONENT_ID_WITH_TEXT_LIMIT_MORE_THEN_TEXT = 'yMoreTextLimitMoreThenText';
    var COMPONENT_ID_WITH_CUSTOM_LINKS = 'yMoreTextWithCustomLinks';
    var TEXT = 'hello, how are you? What time is it now?';
    var TRUNCATED_TEXT = 'hello, how';
    var ELLIPSIS = '.....';

    beforeEach(function() {
        yMoreText.actions.openAndBeReady();
    });

    it('GIVEN a yTextMore component with a text containing more than 10 characters AND limit is 10 AND custom ellipsis' +
        'WHEN the MoreLink is clicked ' +
        'THEN the yTextMore shows the full text AND the button with "LessLink" title AND custom ellipsis is not shown',
        function() {
            // GIVEN
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, TRUNCATED_TEXT);
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, 'MoreLink');
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, ELLIPSIS);

            // WHEN
            yMoreText.actions.clickOnButton(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT);

            // THEN
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, TEXT);
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, 'LessLink');
        });

    it('GIVEN a yTextMore component shows the full text AND the button with "LessLink" title is shown AND limit is 10' +
        'WHEN the LessLink is clicked ' +
        'THEN the yTextMore shows truncated text AND the button with "MoreLink" title',
        function() {
            // GIVEN
            yMoreText.actions.clickOnButton(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT);
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, TEXT);
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, 'LessLink');

            // WHEN
            yMoreText.actions.clickOnButton(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT);

            // THEN 
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, TRUNCATED_TEXT);
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_TEXT_LIMIT_LESS_THEN_TEXT, 'MoreLink');
        });

    it('GIVEN a yTextMore component shows the full text AND limit is more then the text length ' +
        'THEN the yTextMore does not show any button',
        function() {
            // THEN
            yMoreText.assertions.assertComponentContainsText(COMPONENT_ID_WITH_TEXT_LIMIT_MORE_THEN_TEXT, TEXT);
            yMoreText.assertions.assertButtonIsAbsent(COMPONENT_ID_WITH_TEXT_LIMIT_MORE_THEN_TEXT);
        });

    it('GIVEN a yTextMore component with a text containing more than 10 characters AND limit is 10 AND the button with custom MoreLink title is shown ' +
        'WHEN the MoreLink is clicked ' +
        'THEN the yTextMore shows the full text AND the button with custom LessLink title',
        function() {
            // GIVEN
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_CUSTOM_LINKS, 'CustomLinkMore');

            // WHEN
            yMoreText.actions.clickOnButton(COMPONENT_ID_WITH_CUSTOM_LINKS);

            // THEN
            yMoreText.assertions.assertButtonContainsTitle(COMPONENT_ID_WITH_CUSTOM_LINKS, 'CustomLinkLess');
        });
});
