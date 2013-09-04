import argparse
from pymongo import MongoClient

def plot_time_series(sensor_type, sensor_id, experiment):
  sensor_collection = client['sensor_db']['sensor_data']
  timestamps = []
  values = []
  for data in sensor_collection.find({'sensor_type': sensor_type, 'sensor_id': sensor_id, 'experiment': experiment}):
    timestamps.append(data['timestamp'])
    values.append(data['value'])

  print(timestamps)
  print(values)

  # Plot the data here


def main():
  parser = argparse.ArgumentParser(description='Graph signal data stored in mongo')
  parser.add_argument('sensor_type', help='the sensor type of interest, ie wifi, or gyro')
  parser.add_argument('sensor_id', help='the sensor_id of interest')
  parser.add_argument('experiment', help='experiment that you would like to take the data from')

  args = parser.parse_args()
  plot_time_series(args.sensor_type, args.sensor_id, args.experiment)

if __name__ == '__main__':
  client = MongoClient()
  main()
