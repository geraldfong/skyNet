from flask import Flask
from flask import request
from pymongo import MongoClient
import json

app = Flask(__name__)

@app.route('/')
def hello_world():
  return 'Hello World!'

@app.route('/data', methods=['POST'])
def hande_data():
  json_data = request.form['data']
  decoded = json.loads(json_data)
  sensor_type = decoded['sensor_type']
  value = decoded['value']
  timestamp = decoded['timestamp']
  db = client['sensor_db']
  sensor_collection = db['sensor_data']
  document_data = {'sensor_type': sensor_type, 'value': value, 'timestamp': timestamp}
  sensor_collection.insert(document_data) 
  return "OK"

if __name__ == '__main__':
  client = MongoClient()

  # Debug auto restarts server on code changes
  # Setting host to 0.0.0.0 makes it externally visible through server
  app.run(host='0.0.0.0', debug=True)
