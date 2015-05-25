function prepareDemo(id, structure) {
    if ($.jqx.mobile.isTouchDevice()) {
        var html = $("#container").html();
        $("#demoContainer").remove();
        $(document.body).html(html);
    }
}

function initDemo(id) {
    if ($.jqx.mobile.isTouchDevice()) {        
        return;
    }

    switch (id) {
        case "calendar":
            $("#fromCalendar").jqxCalendar({ enableHover: false, keyboardNavigation: false });
            $("#toCalendar").jqxCalendar({ enableHover: false, keyboardNavigation: false });         
            break;
        case "listbox":
            $("#listbox").jqxListBox({ touchMode: true, keyboardNavigation: false, enableMouseWheel: false });
            break;
        case "splitter":
            $("#listbox").jqxListBox({ touchMode: true, keyboardNavigation: false, enableMouseWheel: false });
            $("#splitter").jqxSplitter({ touchMode: true });
            break;
        case "menu":
            $("#menu").jqxMenu({ touchMode: true });
            break;
        case "tree":
            $("#tree").jqxTree({ touchMode: true, keyboardNavigation: false });
            break;
        case "tabs":
            $("#tabs").jqxTabs({ touchMode: true, keyboardNavigation: false });
            break;     
        case "grid":
            $("#grid").jqxGrid({ touchmode: true, keyboardnavigation: false, enablemousewheel: false });
            break;
    }
}