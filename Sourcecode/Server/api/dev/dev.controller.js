const sprintf = require('sprintf').sprintf;
const models = require('../../models');
const shell = require('shelljs');

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
    name: name,
    status : 1 
  }).then((user) => res.status(201).json(user))
};

exports.edit = (req, res) => {
    const id = req.body.id || '';
    const name = req.body.name || '';
    const status = req.body.status || '';
    const code = req.body.code || '';
   
    if(!id) {
        return res.status(400).json({error: 'Incorrect id'});
    }
    if(!name.length && !status.length){
        return res.status(400).json({error: 'No data changes'});
    }
    
    if(name.length) {
       models.Dev.update ({
           name: name
        }, {where: {id : id}, returning: true
        }).then((user) => res.status(202).json(user))
    }

    if(status.length&&code.length) {
        shell.exec(sprintf("./api/dev/controller/1/onoff /dev/rfcomm0 %s",code));
        models.Dev.update ({
            status: status
        }, {where : {id: id}, returning : true
        }).then((user) => res.status(202).json(user))
//    	shell.exec(sprintf("./api/dev/controller/1/onoff /dev/rfcomm0 %s",code));
    	console.log(sprintf("./api/dev/controller/1/onoff /dev/rfcomm0 %s",code));
    }
}
