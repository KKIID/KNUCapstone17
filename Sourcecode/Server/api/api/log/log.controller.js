const models = require('../../models');

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
  }).then((user) => res.status(201).json(user))
};

