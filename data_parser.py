import json
import math
data_path = "data.json"

# feet to array of strengths
strength_data = {}
averaged_data = {}
sd_data = {}

post_filter_averaged_data = {}
post_filter_sd_data = {}

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

def square(x):
    return x * x

for ft, values in strength_data.items():
    avg = sum(values)/len(values)
    sd_total = 0
    for value in values:
        sd_total += square(value - avg)
    sd = math.sqrt(sd_total / len(values))

    sd_data[ft] = sd
    averaged_data[ft] = avg

    filtered_values = [x for x in values if (x > avg - (2*sd) and x < avg + (2*sd))]
    sd_total = 0
    filtered_avg = sum(filtered_values)/len(filtered_values)
    for value in filtered_values:
        sd_total += square(value - filtered_avg)
    post_filter_sd_data[ft] = math.sqrt(sd_total / len(filtered_values))
    post_filter_averaged_data[ft] = filtered_avg

medians = dict([(ft, sorted(values)[len(values)/2]) for ft, values in strength_data.items()])
print(medians)

tuple_values = [(ft, median) for ft, median in medians.items()]
print(tuple_values)
for k,v in medians.items():
    print(str(v) + " " + str(k))

#print("Averages: ")
#print(averaged_data)
#
#print("Filtered averages: ")
#print(post_filter_averaged_data)
#
#print("Standard deviations: ")
#print(sd_data)
#
#print("Filtered standard deviations: ")
#print(post_filter_sd_data)
#
#print("Num values: ")
#print(dict([(key,len(vals)) for key,vals in strength_data.items()]))
