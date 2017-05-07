const models = require('../../models');
const fcm = require('./fcm_req');

exports.notiAll = (req, res) => {
    models.User.findAll().then( 
        users=>{
            res.json(users) 
            users.forEach(function(item,index){
                fcm.sendMsg(item.dataValues.key,"종합설계프로젝트1","전체메시지");
            });
        }
    );
};

exports.notiTo = (req, res) => {
  const id = parseInt(req.params.id, 10);
  if(!id) {
  	return res.status(400).json({error: 'Incorrect id'});
  }
  models.User.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({error: 'No User'});
    } else {
      fcm.sendMsg(user.dataValues.key,"종합설계프로젝트1","개별메시지");
      return res.json(user);
    }
  });
};

exports.destroy = (req, res) => {
	const id = parseInt(req.params.id, 10);
  if (!id) {
    return res.status(400).json({error: 'Incorrect id'});
  }

  models.Dev.destroy({
    where: {
      id: id
    }
  }).then(() => res.status(204).send());

};

exports.create = (req, res) => {
    const name = req.body.name || '';
    const key = req.body.key || '';

    if (!name.length) {
        return res.status(400).json({error: 'Incorrect name'});
    }
    if (!key.length) {
        return res.status(400).json({error: 'Incorrect key'});
    }

    models.User.findOne({
        where: {
            key: key
        }
    }).then(user => {
        if(!user){
            models.User.create({
                name: name,
                key: key
            }).then((user) => res.status(201).json(user))
        } else {
            res.status(200).send('Already registered');
        }
    });
};

