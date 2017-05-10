const models = require('../../models');
const noti = require('../noti/fcm_req');

exports.index = (req, res) => {
	 models.Log.findAll().then(users => res.json(users));
};

exports.show = (req, res) => {
	const id = parseInt(req.params.id, 10);
  if (!id) {
  	return res.status(400).json({error: 'Incorrect id'});
  }
  models.Log.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({error: 'No log'});
    }
    return res.json(user);
  });
};

exports.destroy = (req, res) => {
	const id = parseInt(req.params.id, 10);
  if (!id) {
    return res.status(400).json({error: 'Incorrect id'});
  }

  models.Log.destroy({
    where: {
      id: id
    }
  }).then(() => res.status(204).send());

};

exports.create = (req, res) => {
  const name = req.body.name || '';
  if (!name.length) {
    return res.status(400).json({error: 'Incorrenct name'});
  }

  models.Log.create({
    name: name
  }).then((log) => {
    models.User.findAll().then(users => {
        users.forEach( (item,index)=> {
            noti.sendMsg(item.dataValues.key,"SmartHome 알리미", name);
        });
        res.status(201).json(log)
    })
  })
};

