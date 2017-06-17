const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();
const controller = require('./dev.controller');

let devs = [
    {
        id: 1,
        name: 'alice'
    },
    {
        id: 2,
        name: 'bek'
    },
    {
        id: 3,
        name: 'chris'
    }
]

router.get('/', controller.index);
router.get('/:id', controller.show);
router.delete('/:id', controller.destroy);
router.use(bodyParser.json());
router.use(bodyParser.urlencoded({extended:true}));
router.post('/', controller.create);
router.put('/', controller.edit);
module.exports = router;
