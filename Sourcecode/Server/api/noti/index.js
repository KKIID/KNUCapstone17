const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();
const controller = require('./noti.controller');

router.get('/', controller.notiAll);
router.get('/:id', controller.notiTo);
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({extended:true}));
router.post('/',controller.create);

module.exports = router;
