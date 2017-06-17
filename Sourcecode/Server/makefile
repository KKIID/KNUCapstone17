help:
	@echo "IoT Server Helper"
	@echo "make bind : to bind module\nmake unbind : to release module"

bind:
	@sudo rfcomm bind /dev/rfcomm0 20:16:08:03:64:43
	@echo "bluetooth binded"
unbind:
	@sudo rfcomm release 0
	@echo "bluetooth unbinded"
blue:
	@cat /dev/rfcomm0

run:
	@make unbind
	@make bind
	node app.js
