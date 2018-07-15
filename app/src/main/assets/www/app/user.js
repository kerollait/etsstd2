var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    onDeviceReady: function() {
    	etoos.showLoading();

        document.addEventListener('resume', onResume, true);

        initCommon();

		initEtoosUI();
		this.initContents();
    },

    initContents: function() {
        etoos.hideLoading();
    }
};

app.initialize();

function onResume() {
	console.log("user onResume fire!!!!!!");
}

function onTabSelected() {
	etoos.setHeaderTitle('title', '내 강의실', EtoosServiceUrl.user);
}

function onTabReselected() {
	document.location.href = EtoosServiceUrl.user;
}

function onAutoLoginFailCallback() {

}

function fnSwipePullRefresh() {
	app.initContents();
}