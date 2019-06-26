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
CPQ.imagehandler = {
    makeLabelsUnderImagesSameHeight: function (containerClass, labelClass, newLineCheck) {
        $(containerClass).each(function () {
            var maxHeight = 0;
            var lastYOffSet = 0;
            var actualLine = [];
            var imagesOfCstic = $(this).find(labelClass);
            imagesOfCstic.height('auto');
            imagesOfCstic.each(function () {
                var actualHeight = $(this).height();
                var actualOffSet = $(this).offset().top;
                if (lastYOffSet === 0) {
                    // first elem
                    lastYOffSet = actualOffSet;
                    maxHeight = actualHeight;
                } else if (newLineCheck && lastYOffSet !== actualOffSet) {
                    // new line
                    $(actualLine).height(maxHeight);
                    maxHeight = actualHeight;
                    lastYOffSet = actualOffSet;
                    actualLine = [];
                } else if (actualHeight > maxHeight) {
                    // new max height in actual line
                    maxHeight = actualHeight;
                }
                actualLine.push(this);
            });
            if (actualLine.length > 0) {
                $(actualLine).height(maxHeight);
                actualLine = [];
            }
        });
    },

    checkValueImageClicked: function (elem) {
        if (elem.hasClass("cpq-cstic-value-image")
            || elem.find(".cpq-cstic-value-image").length > 0) {
            CPQ.config.valueChangeViaImage = true;
        }
    },

    csticValueImageMultiClicked: function (e) {
        CPQ.imagehandler.checkValueImageClicked($(e.target));
        var containerId = $(e.currentTarget).attr('id');
        // remove suffix .container
        var csticValueIdInput = containerId.substring(0,
                containerId.length - 10)
            + ".checkBoxWithImage";
        var input = $(CPQ.core.encodeId(csticValueIdInput));
        if (input.val() === "true") {
            input.val("false");
        } else {
            input.val("true");
        }
        var cpqAction = "VALUE_CHANGED";
        var path = input.attr('name');
        var data = CPQ.config.getSerializedConfigForm(cpqAction, path, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    csticValueImageSingleClicked: function (e) {
        CPQ.imagehandler.checkValueImageClicked($(e.target));
        var containerId = $(e.currentTarget).attr('id');
        // remove suffix .container
        var csticValueNameId = containerId
                .substring(0, containerId.length - 10)
            + ".valueName";
        var csticValueNameDiv = $(CPQ.core.encodeId(csticValueNameId));
        var csticValueName = csticValueNameDiv.text();
        var imageGroupId = csticValueNameDiv.parent().attr('id');
        // suffix .radioGroupWithImage
        var inputId = imageGroupId.substring(0, imageGroupId.length - 20)
            + ".radioButtonWithImage";
        var input = $(CPQ.core.encodeId(inputId));
        input.val(csticValueName);
        var cpqAction = "VALUE_CHANGED";
        var path = input.attr('name');
        var data = CPQ.config.getSerializedConfigForm(cpqAction, path, false);
        CPQ.core.firePost(CPQ.config.doUpdatePost, [e, data]);
    },

    updateVariantImageHeight: function (counter) {
        // reset height, in case all items get smaller due to screen width change
        $(".cpq-vc-imgContainer").css("line-height", "0px");
        $(".cpq-vc-imgContainer").css("height", "auto");
        var maxImageHeight = Math.max.apply(null, $(".cpq-vc-imgContainer").map(function () {
                return $(this).height();
            }
        ).get());
        // if images are not loaded yet, height of every container is '1'.
        // This is because we set the line-heigt of each image container to '0' and its height to 'auto'.
        // Do not set this height, instead give more time to wait
        if (maxImageHeight > 1) {
            $(".cpq-vc-imgContainer").css("height", maxImageHeight + "px");
            // set line height as well, to force vertical middle alignment for image
            $(".cpq-vc-imgContainer").css("line-height", maxImageHeight + "px");
        } else {
            if (undefined === counter) {
                setTimeout(function () {
                    CPQ.imagehandler.updateVariantImageHeight(1);
                }, 50);
            } else {
                counter++;
                // give up after 10 tries, maybe none of the variants has an image assigned?
                if (counter < 10) {
                    setTimeout(function () {
                        CPQ.imagehandler.updateVariantImageHeight(counter);
                    }, 50);
                }
            }
        }
    },

    clickHideShowImageGallery:     function(postFx, e) {
        var element = $("#productName");
        var imageComponent = $("#configImage");
        CPQ.uihandler.toggleExpandCollapseStyle(element, imageComponent, "product-details-glyphicon-chevron");
        $("#cpqAction").val("TOGGLE_IMAGE_GALLERY");
        var data = $(CPQ.core.formNameId).serialize();
        CPQ.core.firePost(postFx, [ e, data ]);
    }
};