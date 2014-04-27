import matplotlib.pyplot as plt

def load_csv(filename):
	ts = []
	gf = []
	df = open(filename, "r")
	header = df.readline()
	for line in df:
		sa = line.split(",")
		ts.append(int(sa[0]))
		gf.append(float(sa[1]))
	return ts, gf

def exp_smooth(data, alpha):
	output = [data[0]]
	for i in range(1,len(data)):
		output.append(output[i-1] + alpha * (data[i] - output[i-1]))
	return output

def snip_ends(ts, gf):
	start = ts[0]
	for i in range(1, len(ts)):
		if ts[i] >= start + 150000:
			break;
	si = i
	end = ts[len(ts)-1]
	for i in range(1, len(ts)):
		if ts[i] >= end - 150000:
			break;
	ei = i
	return ts[si:ei],gf[si:ei]
	
def plot_init():
	plt.clf()

def plot_addfig(i):
	plt.figure(i)

def insert_subplot(x):
	plt.subplot(x)

def plot_signal(ts, gf, label):
	plt.plot(ts, gf, label=label)

def plot_show():
	plt.show()

if __name__ == "__main__":	
	ts,gf = load_csv("/home/wathsala/cos424/cos424project/data/"+
			"timestamp-vs-gforce/a/a_1397933233.gforce.csv")
	ts,gf = snip_ends(ts, gf)
	gf2 = exp_smooth(gf, 0.09)
	gf3 = exp_smooth(gf2, 0.2)
#gf3 = exp_smooth(gf3, 0.1)
	plot_init()
	plot_addfig(1)
	insert_subplot(411)
	plot_signal(ts, gf, "Original Signal")
	plot_signal(ts, gf2, "Smoothed Signal")
	insert_subplot(412)
	plot_signal(ts, gf, "Original Signal")
	insert_subplot(413)
	plot_signal(ts, gf2, "Smoothed Signal")
	insert_subplot(414)
	plot_signal(ts, gf3, "Smoothed Signal-2")
	plot_show()

