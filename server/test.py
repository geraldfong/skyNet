import web
import json
import urllib2

urls = (
    '/android', 'android',
    '/light', 'light',
    '/imu', 'imu',
    '/test', 'test'
    )
app = web.application(urls, globals())


data = []

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
    print("Input")
    print(datum)
    if last_data != 0:
      print("ms since last data: " + str(int(datum['time'])-last_data))
    last_data = int(datum['time'])
    for ssid, strength in datum['wifiData'].iteritems():
      new_wifi_datum = {'strength': strength}
      y = 2.423267447*0.01*strength**2 + 1.133836023 * strength + 13.8074953
      print(y)
      if len(data) == 0:
        new_wifi_datum['distance'] = y
      else:
        previous_wifi_datum = data[-1]['wifiData'][ssid]
        #if abs(y - previous_wifi_datum['distance']) > 3 and datum['time'] - data[-1]['time']:
        #  new_wifi_datum['distance'] = previous_wifi_datum['distance']
        #else:
        new_wifi_datum['distance'] = y * 0.2 + previous_wifi_datum['distance']* 0.8
      new_wifi_data[ssid] = new_wifi_datum
    print(new_wifi_data)

    if 'skynet' in new_wifi_data and 'SKYNET' in new_wifi_data:
      strength0 = new_wifi_data['skynet']
      strength1 = new_wifi_data['SKYNET']
      d = 2
      # x is distance from skynet to x coordinate
      x = (strength0 ** 2 - strength1 ** 2 + d**2) / (2 * d)
      y = (strength0 ** 2 - x ** 2) ** .5
      datum['x'] = x
      datum['y'] = y

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
    print web.data()
    return "Success!"

class test:
  def GET(self):
    urllib2.urlopen('http://localhost:9999/switchDeskRight')
    return 'Hello, '

if __name__ == "__main__":
  app.run()
