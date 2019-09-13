"use strict";
$(document).ready(
    function () {
        let clickTime = 3;
        let timeout = 0;
        setTimeout(function () {
            $("#site-loader").fadeOut();
        }, 500);
        displayTime();
        $("#btn_camera").click(
            function () {
                window.java.startApplication("com.android.camera2");
            }
        );
        $("#btn_record").click(
            function () {
                window.java.startApplication("com.android.soundrecorder");
            }
        );
        $("#btn_remote").click(
            function () {
                window.java.startApplication("com.duoqin.remote");
            }
        );
        $("#btn_music").click(
            function () {
                window.java.startApplication("com.android.music");
            }
        );
        $("#btn_assistant").click(
            function () {
                window.java.startApplication("com.xiaomi.xiaoailite");
            }
        );
        $("#btn_note").click(
            function () {
                window.java.startApplication("com.smartisan.notes");
            }
        );
        $("#btn_files").click(
            function () {
                window.java.startApplication("com.android.documentsui");
            }
        );
        $("#text").click(
            function () {
                window.clearTimeout(timeout);
                clickTime += 1;
                if (clickTime >= 10) {
                    window.java.finish();
                } else {
                    $("#number").html(clickTime);
                }
                timeout = window.setTimeout(function () {
                    clickTime = 3;
                    showSnackbar("#toast", "click times reset");
                    $("#number").html(clickTime);
                }, 5000);
            }
        );
    }
);

function showSnackbar(selectors, message, timeout = 1000, actionHandler = null, actionText = null) {
    document.querySelector(selectors).MaterialSnackbar.showSnackbar({
        message: message,
        timeout: timeout,
        actionHandler: actionHandler,
        actionText: actionText
    });
}

function displayTime() {
    let date = new Date();
    let month = date.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    let dat = date.getDate();
    if (dat < 10) {
        dat = "0" + dat;
    }
    let day = date.getDay();
    switch (day) {
        case 1:
            day = "星期一";
            break;
        case 2:
            day = "星期二";
            break;
        case 3:
            day = "星期三";
            break;
        case 4:
            day = "星期四";
            break;
        case 5:
            day = "星期五";
            break;
        case 6:
            day = "星期六";
            break;
        case 7:
            day = "星期日";
            break;
        default:
            day = "未知次元";
    }
    let hour = date.getHours();
    if (hour < 10) {
        hour = "0" + hour;
    }
    let min = date.getMinutes();
    if (min < 10) {
        min = "0" + min;
    }
    let sec = date.getSeconds();
    if (sec < 10) {
        sec = "0" + sec;
    }
    $("#time").html(hour + " : " + min + " : " + sec);
    $("#date").html(date.getFullYear() + " / " + month + " / " + dat + "<br />" + day);

    // Battery module
    let battery = window.java.batteryInfo();
    let batteryState = window.java.batteryState();
    $("#battery_text").html(battery);
    if (batteryState !== 3) {
        $("#battery_icon").html("battery_charging_full");
    } else {
        if (battery <= 20) {
            $("#battery_icon").html("battery_alert");
        } else {
            $("#battery_icon").html("battery_full");
        }
    }
    setTimeout(displayTime, 100);
}
