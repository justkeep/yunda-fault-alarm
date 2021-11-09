$(function () {
    var line = $("#line");
    var category = $("#category");
    var phones = $("#phones");
    var pushMsgFlag = $("#pushMsgFlag");
    var pushFrequency = $("#pushFrequency");
    var timeInterval = $("#timeInterval");

    $("#sb").click(function () {

        if (line.val() === null || line.val() === "") {
            alert("请填写线路名称~");
            return;
        }
        if (category.val() === null || category.val() === "") {
            alert("请填写分类名称~");
            return;
        }
        if (phones.val() === null || phones.val() === "") {
            alert("请输入电话号码，多个以“#”分隔~");
            return;
        }
        if (pushMsgFlag.val() === null || pushMsgFlag.val() === "") {
            alert("请选择是否发送短信提醒");
            return;
        }
        if (pushFrequency.val() === null || pushFrequency.val() === "") {
            alert("请设置短信发送频率");
            return;
        }
        if (timeInterval.val() === null || timeInterval.val() === "") {
            alert("请设置短信发送间隔时间，单位：小时");
            return;
        }
        $("form").submit();
    });

    $("#back").click(function () {
        history.back(-1);
    })
});