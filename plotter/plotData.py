import matplotlib.pyplot as plt
import json
import numpy
from pylab import * 

f = open('../coffee_table_data.txt', 'r')
#f = open('coffee_table_data_bigger.txt', 'r')

ssid_to_router_name = {
  'skynet': 'a',
  'SKYNET': 'b'
}
data = {}

for line in f:
  line_json = json.loads(line)
  cur_data = {
    'a': {
      'strength': [],
      'time': []
    },
    'b': {
      'strength': [],
      'time': []
    }
  }
  for strength_and_ssid in line_json['data']:
    cur_ssid = ssid_to_router_name[strength_and_ssid['ssid']]
    cur_data[cur_ssid]['strength'].append(strength_and_ssid['strength'])
    cur_data[cur_ssid]['time'].append(strength_and_ssid['time'])


  data[line_json['feet']] = cur_data


for i in range(4):
  for ssid, strength_and_time in data[i].iteritems():
    title = '%s at %s' % (ssid, i)

    strength = strength_and_time['strength']
    time = strength_and_time['time']
    plt.hist(strength)
    plt.xlabel('RSSI (dBm)')
    plt.ylabel('Count')
    hist_title = 'Histogram of %s' % title
    plt.title(hist_title)
    plt.grid(True)
    plt.savefig(hist_title)
    plt.close()

    plot_title = 'Plot of %s' % title
    plt.plot(time, strength)
    plt.xlabel('Time (seconds)')
    plt.ylabel('RSSI (dBm)')
    plt.title(plot_title)
    plt.savefig(plot_title)


    print(title)
    avg, std, median = numpy.average(strength), numpy.std(strength), numpy.median(strength)
    print('Avg: %.3f, Std: %.3f, Median: %.3f' % (avg, std, median))

    plt.close()


