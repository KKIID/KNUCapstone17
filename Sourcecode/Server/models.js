const Sequelize = require('sequelize');
const sequelize = new Sequelize('SilverIoT','root','root',{
        timezone: '+09:00'
    }
);

const Dev = sequelize.define('dev', {
    name: Sequelize.STRING,
    status : Sequelize.BOOLEAN
});

const User = sequelize.define('user', {
    name: Sequelize.STRING,
    key: Sequelize.STRING
});

const Log = sequelize.define('log', {
    name: Sequelize.STRING
});

module.exports = {
    sequelize: sequelize,
    Dev: Dev,
    User: User,
    Log: Log
}
