import json
data_path = "data.json"

# feet to array of strengths
strength_data = {}
averaged_data = {}

for line in open(data_path):
    try:
        data = json.loads(line)
        ft = data['feet']
        values = data['data'] # yes.
        accum = 0
        for value in values:
            if ft not in strength_data:
                strength_data[ft] = []
            strength_data[ft].append(int(value['strength']))
    except ValueError:
        print("Couldn't load line: " + line)

for ft, values in strength_data.items():
    averaged_data[ft] = sum(values)/len(values)

print(averaged_data)
