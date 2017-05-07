const models = require('../../models');

exports.index = (req, res) => {
	 models.Dev.findAll().then(users => res.json(users));
};

exports.show = (req, res) => {
	const id = parseInt(req.params.id, 10);
  if (!id) {
  	return res.status(400).json({error: 'Incorrect id'});
  }
  models.Dev.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({error: 'No User'});
    }
    return res.json(user);
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
  if (!name.length) {
    return res.status(400).json({error: 'Incorrenct name'});
  }

  models.Dev.create({
    name: name
  }).then((user) => res.status(201).json(user))
};

