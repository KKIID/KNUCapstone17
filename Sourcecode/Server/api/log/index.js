const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();
const controller = require('./log.controller');

router.get('/', controller.index);
router.get('/:id', controller.show);
router.delete('/:id', controller.destroy);
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({extended:true}));
router.post('/', controller.create);

module.exports = router;
