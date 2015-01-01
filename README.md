#RPi-LaunchControl

Fireworks Launch Control using a Raspberry Pi and Android phones.

##Disclaimer

None of this is safe. 

##RPi

Using node.js there is a small webserver (app.js) that currently displays which channels are ready to fire, along with a super-simple API for the Android app.
This is simply triggering the correct GPIO pins for 200 ms at a time (see below).

##Electrical

I used a [Sainsmart 8-channel relay board](http://www.sainsmart.com/8-channel-dc-5v-relay-module-for-arduino-pic-arm-dsp-avr-msp430-ttl-logic.html), with an [isolator](https://docs.google.com/file/d/0B5-HND9HJkXWSTQtYlFTZ3VyODA/edit) in between. Fairly straight forward soldering and wiring job. 
I connected the pins on the relay board (through the isolator) to GPIO pins 8, 10, 12, 16, 18, 22, 24 and 26.

As for the ignition of the fuses; I used some 34 AWG Nichrome wire looped twice around each fuse. Any decent 12v battery should be enough for the wire to glow hot, and ignite anything. The Pi is currently programmed to power the relay for 200 ms at a time, this works quite nicely as I found 100 ms not be enough. 

##Android

Pretty simple Android app using standard motion sensors and AsyncHTTP. The idea behind the app is that each user has to shake their phone in order to 'charge' the tnt plunger. Full source under /android. As mentioned; the only depedency is Async-HTTP (compile 'com.loopj.android:android-async-http:1.4.5'
) from loopj.

##API

The following commands are implemented:

* /api-status - returns wether or not any fireworks are ready
* /api-fire - fires one random firework
* /api-fuckall - fires ALL the fireworks!

##Aftermath

Turned out to be a Great Success! All but two tubes detonated as people used the app. 

![Complete rig](http://tobbentm.com/ul/rpilc.jpg)

And documented on [video](https://www.youtube.com/watch?v=qWbJb29nAGA)! (In Norwegian though)