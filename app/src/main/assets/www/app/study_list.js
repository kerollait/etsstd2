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
	console.log("study_list onResume fire!!!!!!");
}

function onTabSelected() {
	etoos.setHeaderTitle('title', '수강중인 강좌', EtoosServiceUrl.study_list);
}

function onTabReselected() {
	document.location.href = EtoosServiceUrl.study_list;
}

function onAutoLoginFailCallback() {
}

function fnSwipePullRefresh() {
	etoos.showLoading();
	app.initContents();
}