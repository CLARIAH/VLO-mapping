/* 
 * Copyright (C) 2015 CLARIN
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

var interval;

function startSearch() {
    // after a short while start animating the search button
    var element = $('form#search-form .search-button');
    element.prop('disabled', true);

    // this will animate (fade in, fade out) the search button
    interval = setInterval(function () {
        element.animate({opacity: '-=0.5'}, 500);
        element.animate({opacity: '+=1'}, 500);
    }, 500); // delay between 'loops'
}

function endSearch() {
    // done searching, clean up
    var element = $('form#search-form .search-button');
    element.prop('disabled', false);

    // stop the animation
    if (interval !== null) {
        clearInterval(interval);
    }
}

$(function () {
    //enable nicer bootstrap-style tooltip via plugin
    //http://getbootstrap.com/javascript/#tooltips
    $('form#search-form [data-toggle="tooltip"]').tooltip();
});

function transitionFromSimple(cb) {
    console.log("transition animation..");
    $('.simple-only').slideUp({
        duration: 'fast',
        start: function() {
            console.log("started..");
            $('.hide-simple').slideDown('fast');
        },
        done: function() {
            console.log("cb..");
            cb();
        }
    });
}

function showSearchContent() {
    $("#simple-filler").hide();
    $('.hide-simple').fadeIn();
}

$(document).ready(function() {
   //TODO: only if exists $("#simple")
   
   $(window).scroll(function() {
      var bottom_of_window = $(window).scrollTop() + $(window).height();
      var bottom_of_object = $("#simple-filler").offset().top;
      if(bottom_of_window > bottom_of_object) {
            showSearchContent();
      }
   });
   
   $(".simple a#switch-from-simple").click(function(evt) {
      evt.preventDefault(); 
      showSearchContent();
      $('html, body').animate({
        scrollTop: $("#topnavigation").offset().top - 5
        //TODO: hide non-simple 
    }, 1000);
   });
});
