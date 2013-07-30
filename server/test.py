import web
import json
import urllib2
from math import log

urls = (
    '/android', 'android',
    '/light', 'light',
    '/imu', 'imu',
    '/test', 'test',
    '/gesture', 'gesture',
    '/direction', 'direction'
    )
app = web.application(urls, globals())


data = []
last_distance = {}

global cur_volume
cur_volume = 0

global light_on
light_on = True

global past_imu
past_imu = None



last_data = 0;
class android:
  def GET(self):
    return 'Hello, android'
  def POST(self):
    global last_data
    datum = json.loads(web.data())
    if ('time' not in datum) or ('wifiData' not in datum):
      return "Error you must pass in time and wifidata"

    new_wifi_data = {}
    print(datum)
    if last_data != 0:
      print("ms since last data: " + str(int(datum['time'])-last_data))
    last_data = int(datum['time'])
    for ssid, strength in datum['wifiData'].iteritems():
      new_wifi_datum = {'strength': strength}
      if ssid == 'skynet':
        y = -5.8220137*10**-4 * strength **2 - 5.838352899*10**-1 * strength - 16.75303735
        #y = 29.5122864 *log(strength * -1) - 101.3780886
        #y = 3.941495274*10**-4 *strength**3 + 5.951424398*10**-2 *strength**2 + 2.222456471 * strength + 25.06045666
      elif ssid == 'SKYNET':
        y = 7.885911016*10**-3 *strength**2+ 6.641031158*10**-2 *strength - 3.85204015
        #y = 28.98041351 *log(strength * -1) - 97.34918427
        #y = 1.040973165*10**-3 *strength ** 3 + 1.394046443*10**-1 *strength**2 + 5.247509396 * strength + 60.87639786
      else:
        next
      print(y)
      new_wifi_datum['distance'] = y
      #if len(data) == 0:
      #  new_wifi_datum['distance'] = y
      #else:
      #  previous_wifi_datum = data[-1]['wifiData'][ssid]
        #if abs(y - previous_wifi_datum['distance']) > 3 and datum['time'] - data[-1]['time']:
        #  new_wifi_datum['distance'] = previous_wifi_datum['distance']
        #else:
      #  new_wifi_datum['distance'] = y * 0.2 + previous_wifi_datum['distance']* 0.8
      new_wifi_data[ssid] = new_wifi_datum
    print(new_wifi_data)

    if 'skynet' in new_wifi_data and 'SKYNET' in new_wifi_data:
      distance0 = new_wifi_data['skynet']['distance']
      distance1 = new_wifi_data['SKYNET']['distance']
      print("Distance0: " + str(distance0) + "Distance1: " + str(distance1))
      d = 8.916666667
      # x is distance from skynet to x coordinate
      x = (distance0 ** 2 - distance1 ** 2 + d**2) / (2 * d)
      y = abs(distance0 ** 2 - x ** 2) ** .5
      print(last_distance)
      print("X: " + str(x) + " Y: " + str(y))
      if 'x' not in last_distance or 'y' not in last_distance:
        datum['x'] = x
        datum['y'] = y
      else:
        if (abs(x-last_distance['x']) + abs(y - last_distance['y'])) > 15:
          datum['x'] = last_distance['x']
          datum['y'] = last_distance['y']
        else:
          datum['x'] = x * 0.07 + last_distance['x'] * 0.93
          datum['y'] = y * 0.07 + last_distance['y'] * 0.93
      print("X: " + str(datum['x']) + " Y: " + str(datum['y']))
      last_distance['x'] = datum['x']
      last_distance['y'] = datum['y']


    datum['wifiData'] = new_wifi_data

    data.append(datum)
    return "SUCCESS"

class light:
  def GET(self, name):
    if not name:
      name = 'World'
    return 'Hello, ' + name + '!'

class imu:
  def POST(self):
    imu = float(web.data())
    global past_imu
    if past_imu is None:
      cur_imu = imu
    else:
      cur_imu = past_imu * 0.6 + imu * 0.4

    global cur_degrees
    degrees = 0

    #print(imu)
    volume = None
    past_imu = cur_imu
    if past_imu > 70:
      volume = 8
    elif past_imu > 50:
      volume = 7
    elif past_imu > 30:
      volume = 6
    elif past_imu > 10:
      volume = 5
    elif past_imu > -10:
      volume = 4
    elif past_imu > -30:
      volume = 3
    elif past_imu > -50:
      volume = 2
    elif past_imu > -70:
      volume = 1
    if volume is not None:
      if abs(degrees - cur_degrees) < 30 or abs(cur_degrees + 360 - degrees) < 30 or abs(degrees + 360 - cur_degrees) < 30:
        urllib2.urlopen('http://localhost:9997/changeVolume?volume=' + str(volume))
        print("Setting volume: " + str(volume))
    return "Success!"

class direction:
  def POST(self):
    datum = json.loads(web.data())
    print "Print degrees:"
    print datum['degrees']
    global cur_degrees
    cur_degrees = datum['degrees']
    return "HI"


class test:
  def GET(self):
    urllib2.urlopen('http://localhost:9999/switchDeskRight')
    return 'Hello, '

object_gestures = [{
  "object": "speakers-fine-tuned",
  "gesture": "test",
  "x": 10,
  "y": 20
}, {
  "object": "speakers",
  "gesture": "blahWAVE",
  "degrees": 0
}, {
  "object": "file-transfer",
  "gesture": "WAVE",
  "degrees": 270
}, {
  "object": "light",
  "gesture": "PUSH",
  "degrees": 100
}, {
  "object": "light",
  "gesture": "PULL",
  "degrees": 100
}, {
  "object": "switch-screen-left",
  "gesture": "LEFT",
  "degrees": 150
}, {
  "object": "switch-screen-right",
  "gesture": "RIGHT",
  "degrees": 150
}, {
  "object": "all-off",
  "gesture": "WAVE",
  "degrees": 180
}]

class gesture:
  def POST(self):
    data = web.data()
    print data
    motion = data
    global cur_volume
    global light_on

    matching_gestures = []


    for object_gesture in object_gestures:
      if 'degrees' not in object_gesture:
        continue
      if object_gesture['gesture'] == motion:
        degrees = object_gesture['degrees']
        if abs(degrees - cur_degrees) < 50 or abs(cur_degrees + 360 - degrees) < 50 or abs(degrees + 360 - cur_degrees) < 50:
          matching_gestures.append(object_gesture)

    for object_gesture in matching_gestures:
      if object_gesture['object'] == "brightness":
        # dosomething
        print("blah")
      if object_gesture['object'] == "brightness-fine-tuned":
        # dosomething
        print("blah")
      if object_gesture['object'] == "speakers":
        cur_volume = 8
        urllib2.urlopen('http://localhost:9997/changeVolume?volume=' + str(cur_volume))
        print("blah")
      if object_gesture['object'] == "speakers-fine-tuned":
        cur_volume = max(cur_volume - 1, 0)
        urllib2.urlopen('http://localhost:9997/changeVolume?volume=' + str(cur_volume))
        print("blah")
      if object_gesture['object'] == "file-transfer":
        # dosomething
        print("blah")
      if object_gesture['object'] == "light":
        if light_on:
          print("Trying to turn off")
          output = urllib2.urlopen('https://agent.electricimp.com/3zuteAE-YCQR/off').read()
          print(output)
        else:
          print("Trying to turn on")
          output = urllib2.urlopen('https://agent.electricimp.com/3zuteAE-YCQR/on').read()
          print(output)
        light_on = not light_on

        print("blah")
      if object_gesture['object'] == "switch-screen-left":
        urllib2.urlopen('http://localhost:9997/switchDeskLeft')
      if object_gesture['object'] == "switch-screen-right":
        print("About to try to screen right")
        urllib2.urlopen('http://localhost:9997/switchDeskRight')
      if object_gesture['object'] == "all-off":
        # do something
        print("blah")

    return 'Hello, '


if __name__ == "__main__":
  app.run()
