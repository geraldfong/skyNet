import argparse
from pymongo import MongoClient
import matplotlib.pyplot as plot

# Gathers the data for the given sensor type, sensor id, and experiment and plots both a histogram and 
# timeseries of the information and saves it to a pdf.
def plot_experiment(sensor_type, sensor_id, experiment):
  if sensor_type == 'wifi':
    value_unit = 'RSSI (dBm)'
  elif sensor_type == 'gyro':
    value_unit = 'Orientation (degrees)'
  else:
    value_unit = 'Unknown units'

  sensor_collection = client['sensor_db']['sensor_data']
  values, timestamps = [], []

  # Gather the data for the current experiment and sort by timestamp
  for data in sensor_collection.find({'sensor_type': sensor_type, 'sensor_id': sensor_id, 'experiment': experiment}).sort('timestamp', 1):
    values.append(data['value'])
    timestamps.append(data['timestamp'])

  title = '%s(%s) at %s' % (sensor_id, sensor_type, experiment)
  plot_histogram(values, value_unit, title)
  plot_time_series(values, timestamps, value_unit, title)

def plot_histogram(values, value_unit, title):
  plot_title = 'Histogram of %s' % title
  plot.title(plot_title)
  plot.xlabel(value_unit)
  plot.ylabel('Count')
  plot.hist(values)
  plot.grid(True)
  plot.savefig(plot_title)
  plot.close()

def plot_time_series(values, timestamps, value_unit, title):
  plot_title = 'Plot of %s' % title
  plot.title(plot_title)
  plot.xlabel('Time (seconds)')
  plot.ylabel('RSSI (dBm)')
  plot.plot(time, strength)
  plot.savefig(plot_title)
  plot.close()


def main():
  # Argument paarsers to handle command line arguments ot this script
  parser = argparse.ArgumentParser(description='Graph signal data stored in mongo')
  parser.add_argument('sensor_type', help='the sensor type of interest, ie wifi, or gyro')
  parser.add_argument('sensor_id', help='the sensor_id of interest')
  parser.add_argument('experiment', help='experiment that you would like to take the data from')

  args = parser.parse_args()
  plot_experiment(args.sensor_type, args.sensor_id, args.experiment)

if __name__ == '__main__':
  client = MongoClient()
  main()
