
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    onDeviceReady: function() {
        initCommon();
        initEtoosUI();
        initTeacherList();
    }
};

app.initialize();

function initTeacherList() {
    etoosSwiper = new EtoosSwiper();
    etoosSwiper.init({
        menu_key: "area_cd",
        use_top_swiper_menu_hash_history: false,
        use_menu_reload_hash_history: false,
        use_random_default_menu: true,
        random_default_menu_range: {
            start: 1,
            end: 5
        },
        use_top_swiper_menu_nav: true,
        top_swiper_menu_config: {
            slidesPerView: 5,
            spaceBetween: 4.9
        }
    });
}

function onWebviewSubscribeCallback(params) {
	if (params == null || !typeof params =='Object' || params.length == 0) {
		return;
	}

	var obj = params[0];
 	if (obj.type == 'url_move') {
 	    var url = obj.url;

 	    if (url != null && typeof url == 'String') {
 	        document.location.href = url;
 	    }
 	}
}

function onTabSelected() {

}

function onTabReselected() {
	document.location.href = EtoosServiceUrl.teacher_list;
}

function onWebviewSubscribeCallbackError() {
	console.log('webview callback error!');
}