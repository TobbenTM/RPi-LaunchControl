var http = require('http');
var gpio = require('rpi-gpio');

/* Channel:     g 1 2 3     4 5    6 7 8
*       p2   * * * * * * * * * * * * * p26
*       p1   * * * * * * * * * * * * * p25
*/
var pins = [8, 10, 12, 16, 18, 22, 24, 26];
var channels = [];
for(var i = 0; i < 8; i++){
  channels[i] = {ready: true, pin: pins[i]};
  gpio.setup(pins[i], gpio.DIR_OUT);
}
gpio.reset();

var server = http.createServer(function(req, res) {
  res.writeHead(200);
  if(req.url == '/api-status'){
    if(shitIsGone()){
      res.write('empty');
    } else {
      res.write('ready');
    }
  } else if (req.url == '/api-fire') {
    if(fire())
      res.write('success');
    else
      res.write('failed');
  } else if (req.url == '/api-fuckall') {
    if(fuckall(0))
      res.write('success');
    else
      res.write('failed');
  } else {
    if(shitIsGone()){
      res.write('All shit is gone. Happy new-year!');
    } else {
      if(req.url == '/fire'){
        res.write('Firing random channel!');
        fire();
      }
      for(var j = 0; j < 8; j++){
        res.write('\nChannel ' + (j+1) + ': ' + channels[j].ready);
      }
    }
  }
  res.end();
});

server.listen(80);
console.log('Server running! Ready to blow shit up.');

function shitIsGone(){
  for(var i = 0; i < 8; i++){
    if(channels[i].ready){
      return false;
    }
  }
  return true;
}

function fire(){
  var fired = false;
  while(!fired && !shitIsGone()){
    var i = Math.floor((Math.random()*10)%8);
    if(channels[i].ready){
      activateChannel(channels[i]);
      fired = true;
    }
  }
  return fired;
}

function fuckall(ch){
  if(!shitIsGone()){
    activateChannel(channels[ch]);
    setTimeout(fuckall, 1000, ++ch);
  }
  return true;
}

function activateChannel(channel){
  channel.ready = false;
  gpio.write(channel.pin, 1, gpiowrite);
  setTimeout(function(){
    gpio.write(channel.pin, 0, gpiowrite);
  }, 200);
}

function gpiosetup(){
  if (err) console.log(err);
}

function gpiowrite(err){
  if (err) console.log(err);
}

gpio.on('change', function(channel, value){
  if(value){
    console.log('Blowing up stuff on pin ' + channel);
  } else {
    console.log('Done blowing up shit on pin ' + channel + ' ' + value);
  }
});

process.on('SIGINT', function(){
  console.log('\tShutting down new-years fun.');
  gpio.destroy(function(){
    process.exit();
  });
});
