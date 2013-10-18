from flask import Flask
from flask import request
import json
import urllib2

app = Flask(__name__)

past_imu = None
cur_imu = None

# The url of the ssh tunnel hole used to control gesture devices
hole_url = 'http://localhost:9994'
light_url = 'https://agent.electricimp.com/3zuteAE-YCQR'

cur_degree = 150
highest_deg = 120

computer_orientation = 245 
lightbulb_orientation = 190
speaker_orientation = 300

light_on = False

@app.route('/imu', methods=['POST'])
def imu():
  if cur_degree is None:
    return "Need to have orientation data!"

  imu = float(request.form['data'])
  global past_imu
  if past_imu is None:
    cur_imu = imu
  else:
    # Perform exponential smoothing to reduce jerky imu data
    cur_imu = past_imu * 0.6 + imu * 0.4

  past_imu = cur_imu
  percent = None
  
  # Find the percent from 0 to 1 that we have turned in our orientation
  # from -past_imu to +past_imu.
  if past_imu > highest_deg or past_imu < -1 * highest_deg:
    percent = None
  else:
    percent = (past_imu + highest_deg) / (highest_deg * 2)

  if percent is not None:
    #if within_deg(cur_degree, 150):
    #  bright_val = percent * percent
    #  command = '%s/on?brightness=%2.1f' % (light_url, bright_val)
    #  urllib2.urlopen(command)
    if within_deg(cur_degree, speaker_orientation):
      command = '%s/changeVolume?volume=%3.2f' % (hole_url, percent * 8)
      urllib2.urlopen(command)

  return "Success!"

@app.route('/gesture', methods=['POST'])
def gesture():
  if cur_degree is None:
    return "Need to have orientation data!"

  gesture = request.form['data']
  print(gesture)
  print(cur_degree)
  if gesture == 'LEFT' and within_deg(cur_degree, computer_orientation):
    urllib2.urlopen('%s/switchDeskLeft' % (hole_url))
  elif gesture == 'RIGHT' and within_deg(cur_degree, computer_orientation):
    print("Firing right")
    urllib2.urlopen('%s/switchDeskRight' % (hole_url))
  elif (gesture == 'PUSH') and within_deg(cur_degree, lightbulb_orientation):
    global light_on
    if light_on:
      urllib2.urlopen('https://agent.electricimp.com/3zuteAE-YCQR/off').read()
    else:
      urllib2.urlopen('https://agent.electricimp.com/3zuteAE-YCQR/on').read()
    light_on = not light_on
  elif (gesture == 'WAVE'):
    global light_on
    urllib2.urlopen('https://agent.electricimp.com/3zuteAE-YCQR/off').read()
    command = '%s/changeVolume?volume=%3.2f' % (hole_url, 0)
    urllib2.urlopen(command)
    command = '%s/lock' % (hole_url)
    urllib2.urlopen(command)
    light_on = False



  return 'Success'

@app.route('/direction', methods=['POST'])
def direction():
  global cur_degree
  cur_degree = float(request.form['data'])
  print(cur_degree)
  return "OK"

def within_deg(degrees, cur_degrees, delta=30):
  diff = abs(degrees - cur_degrees)
  return diff < delta or abs(diff - 360) < delta

if __name__ == '__main__':
  # Debug auto restarts server on code changes
  # Setting host to 0.0.0.0 makes it externally visible through server
  app.run(host='0.0.0.0', port=8080, debug=True)
