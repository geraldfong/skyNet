import json
import math
import pprint
data_path = "data.json"

class SSIDData:
    def __init__(self, name):
        self.name = name
        self.data = {}
        self.finalized = False

    def add_data(self,ft, strength):
        if ft not in self.data:
            self.data[ft] = []
        self.data[ft].append(strength)

    def finalize(self):
        for values in self.data.values():
            values.sort()
        self.finalized = True

    def get_median_strength(self, ft):
        if not self.finalized:
            self.finalize()
        values = self.data[ft]
        return values[len(values)/2]

    def get_median_strengths(self):
        medians = {}
        for ft in self.data.keys():
            medians[ft] = self.get_median_strength(ft)
        return medians

ssid_datas = {}

for line in open(data_path):
    try:
        data = json.loads(line)
    except ValueError:
        print("Couldn't read line: ")
        print(line)
        raise
    ft = data['feet']
    all_strengths = data['data'] # yes.
    for strength_data in all_strengths:
        ssid_name = strength_data['ssid']
        if ssid_name not in ssid_datas:
            ssid_datas[ssid_name] = SSIDData(ssid_name)
        ssid_datas[ssid_name].add_data(ft, strength_data['strength'])

medians = dict([(name, data.get_median_strengths()) for name, data in ssid_datas.items()])

pprint.pprint(medians)
