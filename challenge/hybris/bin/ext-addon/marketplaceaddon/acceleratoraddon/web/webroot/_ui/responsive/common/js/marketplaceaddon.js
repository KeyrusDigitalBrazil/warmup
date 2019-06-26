/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
ACC.slideViewer = {

    _autoload: [
        "slideView"
    ],

    slideView: function() {
        $("#homepage_slider").owlCarousel({
            navigation : false,
            slideSpeed : 300,
            paginationSpeed : 400,
            singleItem : true,
            autoPlay : true
        });
        
        $("div.detailAverage").click(function () {
            $("div.detailRating").slideToggle();
            $("div.show-reviews").slideToggle();
        });
     
        $("div.detailRating").hide("fast");
         $("div.show-reviews").hide("fast");
    }
};

ACC.orderReview = {

    _autoload: [
        "initRatingStars",
        "bindRatingStarsSet"
    ],

    initRatingStars : function(){
        $('.review-rating-input').each(function(){
            var index = (parseFloat(this.value, 10)) * 2;
            index = (typeof index === "number" && !isNaN(index) ) ? index : 0;
            $(this).prev().children().slice(0, parseFloat(index, 10) ).addClass('active');
        });
    },
    
    bindRatingStarsSet: function(){
        
        var setReviewState = function(icons, index){
            icons.slice(0, parseFloat(index,10) ).addClass('active');
        };
        var clearReviewState = function(icons){
            icons.removeClass('active');
        };
        
        $('.review-rating-stars').each(function(){
            var icons = $(this).children();
            $(this).mouseleave(function(){
                clearReviewState(icons);
                var sV = (parseFloat( $(this).next().val(), 10)) * 2;
                (typeof sV === "number" && !isNaN(sV) )? setReviewState(icons, sV) : clearReviewState(icons);
            });
        });
        
        $('.review-js-ratingIcon').each(function(){
            var icons = $(this).parent().children();
            $(this).on({
                mouseenter: function mouseenter(){
                    clearReviewState(icons);
                    setReviewState(icons, $(this).index() + 1 );
                },
                mouseleave: function mouseleave(){
                    $(this).removeClass('active');
                },
                click: function click(){
                    $(this).parent().next().val(($(this).index() + 1) /2);
                }
            });
        });
    }
};

ACC.ratingstars = {

        _autoload: [
            ["bindRatingStars", $(".js-ratingCalc").length > 0],
            ["bindRatingStarsSet", $(".js-ratingCalcSet").length > 0]
        ],

        bindRatingStars: function(){

            $(".js-ratingCalc").each(function(){
                var rating =  $(this).data("rating");
                $(this).find(".js-greenStars").width(100 * ( parseFloat(rating.rating, 10) / rating.total ) + "%");
            });
        },
        bindRatingStarsSet: function(){
            var ratingIcons = $('.js-writeReviewStars .js-ratingIcon');
            var setReviewState = function(index){
                ratingIcons.slice(0, parseFloat(index,10) ).addClass('active');
            };
            var clearReviewState = function(){
                ratingIcons.removeClass('active');
            };
            $('.js-writeReviewStars').on({
                mouseleave: function mouseleave(){
                    clearReviewState();
                    var sV = (parseFloat( $(".js-ratingSetInput").val(), 10)) * 2;
                    (typeof sV === "number" && !isNaN(sV) )? setReviewState(sV) : clearReviewState();
                }
            });

            ratingIcons.on({
                mouseenter: function mouseenter(){
                    clearReviewState();
                    setReviewState( $(this).index()+1 );
                },
                mouseleave: function mouseleave(){
                    $(this).removeClass('active');
                },
                click: function click(){
                    $(".js-ratingSetInput").val( ($(this).index() + 1) /2);
                }
            });

        }

    };
