from flask import request, Flask
from pymongo import MongoClient
import json

app = Flask(__name__)

@app.route('/data', methods=['POST'])
def handle_data():
  json_data = request.form['data']
  decoded = json.loads(json_data)

  # sensor_type - the type of sensor, ie wifi, accelerometer, gyro, etc.
  # sensor_id - the id of the specific sensor, ie x-direction, y-direction, SKYNET, skynet
  # value - the data value of interest such as signal strength from wifi
  # timestamp - a timestamp in seconds from unix
  # experiment - hand picked experiment name which is later used to generate graphs with
  expected_keys = ('sensor_type', 'sensor_id', 'value', 'timestamp', 'experiment')
  if any(key not in decoded for key in expected_keys):
    return 'ERROR: Malformed json; json needs to have [%s]\n' % (' '.join(expected_keys))

  sensor_collection = client['sensor_db']['sensor_data']
  sensor_collection.insert(decoded) 
  return "OK\n"

if __name__ == '__main__':
  client = MongoClient()

  # Debug auto restarts server on code changes
  # Setting host to 0.0.0.0 makes it externally visible through server
  app.run(host='0.0.0.0', debug=True)
