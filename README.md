# Setup

If you are just getting a client running, then you will need to cd into the "mac" folder and type

    ~ python controlMac.py

This will run a webpy server on port 8080. Next you will need to reverse ssh tunnel into a
computer running the server.

    ~ ssh -R 9997:localhost:8080 ubuntu@54.215.14.147

Next ssh into the server and make sure the server is running. If so, you should be good to go!

# Demo:

* Wave at computer 1 to turn increase brightness
* Use knob to adjust computer brightness
* Wave at speakers to turn on
* Use knob to adjust speakers
* File transfer from computer 2 to computer 3 using big wave
* Turn on light using push motion
* Computer 1 switch  screen
* Step away and wave to switch them all off
