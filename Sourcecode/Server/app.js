const express = require('express');
const app = express();
const shell = require('shelljs');

app.get('/',function (req,res){
    res.send('Hello World\n');
});

app.listen(9191,function() {
    console.log("Welcome to Silver-IoT Server...");
    require('./models').sequelize.sync({        
        force: false})
        .then(()=>{
            console.log('Database Sync');
            console.log("Synchronization Done\nServer On");
    });
});

app.use('/devs', require('./api/dev'));
app.use('/notis/', require('./api/noti'));
app.use('/logs/', require('./api/log'));
app.use('/password/',(req,res)=> {
    res.send("8085");
});
