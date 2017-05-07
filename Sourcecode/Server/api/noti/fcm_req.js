var FCM = require('fcm-node');
var serverKey = 'AIzaSyB8KGwGj0VB8Uba-RnaaQNG0-jXoQyg81A'
var fcm = new FCM(serverKey);

exports.sendMsg = function (deviceId, title, message) {
    var msg = {
        to: deviceId,
        notification: {
            title: title,
            body: message
        }
    };
    fcm.send(msg, function(err, response){
        if(err) {
            console.log("Send error");
        } else {
            console.log("Send success");
        }
    });
}

