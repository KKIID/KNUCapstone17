const Sequelize = require('sequelize');
const sequelize = new Sequelize('SilverIoT','root','rudqnreogkrryBIST!');

const Dev = sequelize.define('dev', {
    name: Sequelize.STRING
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
