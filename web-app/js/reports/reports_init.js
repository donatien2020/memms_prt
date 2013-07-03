$(document).ready(function() {

    $('#top_tabs').children('li').children('a').click(function(e) {
        e.preventDefault();
        var clicked_tab = $(this);
        var top_tabs = $('#top_tabs');
        var active_tab = top_tabs.find('.active');
        if(active_tab) active_tab.removeClass('active');
        clicked_tab.addClass('active');
        var id = clicked_tab.attr('id');
        $('div#' + id).siblings('div').hide();
        $('div#' + id).show();
    });

    $('.v-tabs-fold-toggle').click(function(event) {
        var toggle = $(this);
        var li = toggle.parent().parent();
        if(li.hasClass('selected')) {
            li.children('.v-tabs-fold-container').children().hide();
            li.removeClass('selected');
        } else {
            li.children('.v-tabs-fold-container').children().show();
            li.addClass('selected');
            li.find('div#comparison').hide();
            li.find('div#geo_trend').hide();
            li.find('div#info_facility').hide();
        }
        li.children('.v-tabs-fold-container').toggle(500);
    });

    $('.v-tabs-nested-nav').children('li').children('a').click(function(e) {
        var tabs = $(this).parents('div').children('.v-tabs-nested-nav').children('li').children('a');
        var clicked_tab = $(this);
        var parent_div = $(this).parents('div');
        e.preventDefault();
        tabs.removeClass('active');
        clicked_tab.addClass('active');
        var id = $(this).attr('id');
        parent_div.children('.toggled_tab').hide().removeClass('toggled_tab');
        parent_div.children('div#' + id).addClass('toggled_tab');
        parent_div.children('div#' + id).show();
    });

});