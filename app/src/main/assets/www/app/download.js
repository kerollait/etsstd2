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
	etoos.setHeaderTitle('title', '다운로드 강좌', EtoosServiceUrl.download);
}

function onTabReselected() {
	document.location.href = EtoosServiceUrl.download;
}

function onAutoLoginFailCallback() {

}

function fnSwipePullRefresh() {
	app.initContents();
}