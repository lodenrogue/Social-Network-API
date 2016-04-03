var options = {
	clickToHide : true,
	autoHide : false,
	elementPosition : "right",
	showAnimation : 'fadeIn',
	hideAnimation : 'fadeOut',
};

if ($(window).width() < 600) {
	options["elementPosition"] = "bottom center";
}

$.notify.defaults(options);